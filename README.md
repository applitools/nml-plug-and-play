# java-appium-nml-ios

## Environment variables

- `APPLITOOLS_API_KEY`
- `DEVICE_NAME` / `PLATFORM_VERSION` (Simulator)
- `IOS_TARGET` (`simulator` default, or `real`)
- `IOS_UDID` (real device only)

## Sample application

This branch does not commit the sample app binaries. Download them from `main`:

```
git clone --depth 1 --branch main https://github.com/applitools/NML.git nml-main
cp -R nml-main/static_instrumented_sample_application ./static_instrumented_sample_application
rm -rf nml-main
```

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXIOSLocalTest` by default. For `AccessibilityIOSLocalTest`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityIOSLocalTest
```
