"""
Shared pytest fixtures — Applitools NML + local Appium server (Android)

ENV VARS:
  APPLITOOLS_API_KEY     — Applitools API key (required)
  APPLITOOLS_SERVER_URL  — optional; defaults to the Applitools public cloud
  APPIUM_URL             — optional; default http://127.0.0.1:4723
  AVD_NAME               — Android emulator/device name  (default: Pixel_9)
  PLATFORM_VERSION       — Android platform version       (default: 16)
  ANDROID_UDID           — optional; pins a device when several are attached (e.g. emulator-5554)

PREREQUISITE:
  Start a local Appium server first, e.g.:
    appium
  (listens on http://127.0.0.1:4723 by default)
"""
import os

import pytest
from appium import webdriver
from appium.options.common import AppiumOptions
from applitools.selenium import BatchInfo, ClassicRunner, Configuration, Eyes

PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))


def app_path(relative_path):
    # Appium resolves relative app paths against the Appium server's working
    # directory, not this project, so always hand it an absolute path.
    path = os.path.join(PROJECT_ROOT, relative_path)
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
    config.set_batch(BatchInfo("Python Local | NML | Android"))
    return config


def build_capabilities(app, app_package, app_activity, api_key, server_url):
    caps = {
        "platformName": "Android",
        "appium:automationName": "UiAutomator2",
        "appium:deviceName": os.getenv("AVD_NAME", "Pixel_9"),
        "appium:platformVersion": os.getenv("PLATFORM_VERSION", "16"),
        "appium:newCommandTimeout": 300,
        "appium:uiautomator2ServerLaunchTimeout": 90000,  # cold emulators can be slow
        "appium:noReset": False,
        "appium:app": app,
        "appium:appPackage": app_package,
        "appium:appActivity": app_activity,
    }
    udid = os.getenv("ANDROID_UDID")
    if udid:
        caps["appium:udid"] = udid

    # ── NML ─────────────────────────────────────────────────────────────────
    # Injects optionalIntentArguments (Android) and processArguments (iOS)
    # unconditionally, without the "appium:" prefix. AppiumOptions adds the
    # prefix when the capabilities are loaded below, so no manual nesting is
    # needed against a local Appium server.
    Eyes.set_mobile_capabilities(caps, api_key, server_url)
    return caps


@pytest.fixture
def make_driver(api_key, server_url):
    drivers = []

    def _make(app, app_package, app_activity):
        caps = build_capabilities(app, app_package, app_activity, api_key, server_url)
        options = AppiumOptions().load_capabilities(caps)
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
