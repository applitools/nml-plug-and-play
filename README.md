# python-appium-nml-ios

Applitools Native Mobile Library (NML) with **Python + pytest + Appium**, against a local Appium server and iOS Simulator or real device.

## Environment variables

- `APPLITOOLS_API_KEY` (required)
- `APPLITOOLS_SERVER_URL` (optional — private/dedicated Eyes cloud)
- `IOS_TARGET` (`simulator` default, or `real`)
- `DEVICE_NAME` / `PLATFORM_VERSION` (Simulator)
- `IOS_UDID` (optional — pin a specific Simulator or real device)
- `APPIUM_URL` (optional — default `http://127.0.0.1:4723`)

## Sample application

This branch does not commit the sample app binaries. Download them from `main`:

```
git clone --depth 1 --branch main https://github.com/applitools/nml-plug-and-play.git nml-main
cp -R nml-main/static_instrumented_sample_application ./static_instrumented_sample_application
rm -rf nml-main
```

## Install

```
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Run

Start a local Appium server first (`appium`) and boot a Simulator (or connect a device with `IOS_TARGET=real`), then:

```
pytest
```

## How NML is wired in

`conftest.py` builds the capabilities, then calls:

```python
Eyes.set_mobile_capabilities(caps, api_key, server_url)
```

before the driver is created. This injects `optionalIntentArguments` (Android) and `processArguments` (iOS) carrying the Applitools API key into the app launch, so the NML library inside the instrumented app takes the screenshots — not Appium.

The SDK adds these keys **without** the `appium:` prefix; `AppiumOptions().load_capabilities(caps)` adds it automatically. If you build capabilities another way, make sure they reach Appium as `appium:optionalIntentArguments` / `appium:processArguments`.

## Adding to an existing pytest framework

Only three things are needed:

1. `Eyes.set_mobile_capabilities(caps, api_key)` before `webdriver.Remote(...)`.
2. `eyes.open(driver, app_name, test_name)` → `eyes.check(name, Target.window())` → `eyes.close()`.
3. An app that has the NML library in it (statically instrumented, as in the sample app).

The `eyes` / `make_driver` fixtures in `conftest.py` can be copied as-is.

## AnalyticsX flows

`tests/test_analyticsx_ios.py` honours `FLOW`:

```
FLOW=full pytest      # Dashboard → Visual AI Playground (default)
FLOW=compact pytest   # Compact Dashboard → Compact Visual AI Playground
```
