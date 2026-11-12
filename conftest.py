"""
Shared pytest fixtures — Applitools NML + local Appium server (iOS)

ENV VARS:
  APPLITOOLS_API_KEY     — Applitools API key (required)
  APPLITOOLS_SERVER_URL  — optional; defaults to the Applitools public cloud
  APPIUM_URL             — optional; default http://127.0.0.1:4723
  IOS_TARGET             — simulator (default) | real
  DEVICE_NAME            — Simulator name     (default: iPhone 16)   — simulator only
  PLATFORM_VERSION       — Simulator iOS ver. (default: 18.0)        — simulator only
  IOS_UDID               — optional; pins a specific device/Simulator by UDID

PREREQUISITES:
  - A local Appium server running at http://127.0.0.1:4723 (`appium`)
  - A booted Simulator, or a real iOS device connected and provisioned for
    XCUITest (IOS_TARGET=real)
"""
import os

import pytest
from appium import webdriver
from appium.options.common import AppiumOptions
from applitools.selenium import BatchInfo, ClassicRunner, Configuration, Eyes

PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))

# IOS_TARGET=real runs against a physically connected iPhone using the
# real-device .ipa. Anything else (default) targets a Simulator using the
# Simulator-built .app.
IOS_TARGET = os.getenv("IOS_TARGET", "simulator").strip().lower()
IS_REAL_DEVICE = IOS_TARGET == "real"


def app_path(real_device_app, simulator_app):
    # Appium resolves relative app paths against the Appium server's working
    # directory, not this project, so always hand it an absolute path.
    path = os.path.join(PROJECT_ROOT, real_device_app if IS_REAL_DEVICE else simulator_app)
    if not os.path.exists(path):
        pytest.exit(
            "App not found: {}\nCopy static_instrumented_sample_application/ "
            "from the main branch first (see README).".format(path)
        )
    return path


# ── Record each test phase's outcome so the Eyes fixture can abort on failure ─
@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    report = outcome.get_result()
    setattr(item, "rep_" + report.when, report)


@pytest.fixture(scope="session")
def api_key():
    key = os.getenv("APPLITOOLS_API_KEY")
    if not key:
        pytest.exit("APPLITOOLS_API_KEY is not set")
    return key


@pytest.fixture(scope="session")
def server_url():
    return os.getenv("APPLITOOLS_SERVER_URL")  # None → public cloud


@pytest.fixture(scope="session")
def runner():
    runner = ClassicRunner()
    yield runner
    print("\n" + str(runner.get_all_test_results(False)))


@pytest.fixture(scope="session")
def eyes_config(api_key, server_url):
    config = Configuration()
    config.set_api_key(api_key)
    if server_url:
        config.set_server_url(server_url)
    config.set_batch(BatchInfo("Python Local | NML | iOS"))
    return config


def build_capabilities(app, api_key, server_url):
    caps = {
        "platformName": "iOS",
        "appium:automationName": "XCUITest",
        "appium:newCommandTimeout": 300,
        "appium:noReset": False,
        "appium:app": app,
    }
    if not IS_REAL_DEVICE:
        caps["appium:deviceName"] = os.getenv("DEVICE_NAME", "iPhone 16")
        caps["appium:platformVersion"] = os.getenv("PLATFORM_VERSION", "18.0")
    udid = os.getenv("IOS_UDID")
    if udid:
        caps["appium:udid"] = udid

    # ── NML ─────────────────────────────────────────────────────────────────
    # Injects processArguments (iOS) and optionalIntentArguments (Android)
    # unconditionally, without the "appium:" prefix. AppiumOptions adds the
    # prefix when the capabilities are loaded below, so no manual nesting is
    # needed against a local Appium server.
    Eyes.set_mobile_capabilities(caps, api_key, server_url)
    return caps


@pytest.fixture
def make_driver(api_key, server_url):
    drivers = []

    def _make(app):
        options = AppiumOptions().load_capabilities(build_capabilities(app, api_key, server_url))
        driver = webdriver.Remote(
            os.getenv("APPIUM_URL", "http://127.0.0.1:4723"), options=options
        )
        driver.implicitly_wait(10)
        drivers.append(driver)
        return driver

    yield _make
    for driver in drivers:
        driver.quit()


@pytest.fixture
def eyes(request, runner, eyes_config):
    eyes = Eyes(runner)
    eyes.set_configuration(eyes_config)
    yield eyes

    call = getattr(request.node, "rep_call", None)
    if call is None or call.failed or not eyes.is_open:
        eyes.abort()
    else:
        # raise_ex=True fails the test on unresolved visual differences.
        results = eyes.close(raise_ex=True)
        print("\n{} → {}".format(request.node.name, results.url))
