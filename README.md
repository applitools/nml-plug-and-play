# java-appium-nml-ios-local

Java Appium + Applitools NML — iOS against a local Appium server (plain variant), no cloud vendor.

Ported from `java-ios-nml-lambdatest_old`. The `static_instrumented_sample_application/ios/` IPAs were copied in alongside it.

## Prerequisites

- A local Appium server running: `appium` (defaults to `http://127.0.0.1:4723`)
- A target: either a real iOS device, or the iOS Simulator

`static_instrumented_sample_application/ios/` bundles both a real-device `.ipa` and a
Simulator-compatible `.app` for each app. The `IOS_TARGET` environment variable
(`real` | `simulator`, default `simulator`) selects which one gets used and whether
`DEVICE_NAME`/`PLATFORM_VERSION` (Simulator) or `IOS_UDID` (real device) applies.

## Sample application (plug-and-play)

This branch does not commit the sample app binaries — download them from the
`main` branch's `static_instrumented_sample_application/` folder and place that
folder at the project root (same level as `package.json`/`pom.xml`):

    git clone --depth 1 --branch main https://github.com/applitools/NML.git nml-main
    cp -R nml-main/static_instrumented_sample_application ./static_instrumented_sample_application
    rm -rf nml-main

`static_instrumented_sample_application/ios/` contains both the real-device `.ipa`
and a Simulator-compatible `.app` for each app (AnalyticsX, Accessibility).

## Run

```
mvn compile exec:java
```

APPLITOOLS_API_KEY must be set as an environment variable before running. No vendor credentials are needed for this variant.
