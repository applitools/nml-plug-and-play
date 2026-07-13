# javascript-wdio-appium-nml-android-local

WDIO + Applitools NML — Android against a **local Appium server** (plain variant), no cloud vendor.

## Prerequisites

- Android emulator/device running, matching `AVD_NAME` in `.env`
- Appium server running locally: `appium` (defaults to http://127.0.0.1:4723)

## Sample application (plug-and-play)

This branch does not commit the sample app binaries — download them from the
`main` branch's `static_instrumented_sample_application/` folder and place that
folder at the project root (same level as `package.json`):

    git clone --depth 1 --branch main https://github.com/applitools/NML.git nml-main
    cp -R nml-main/static_instrumented_sample_application ./static_instrumented_sample_application
    rm -rf nml-main

`static_instrumented_sample_application/android/` contains the `.apk` for each app
(AnalyticsX, Accessibility) — the same APK runs on both a real device and an emulator.

## Run

```
npm test
```
