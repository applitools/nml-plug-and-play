# typescript-wdio-appium-ios

## Environment variables

- `APPLITOOLS_API_KEY`
- `DEVICE_NAME`
- `PLATFORM_VERSION`
- `IOS_TARGET`
- `IOS_UDID`
- `ACTIVE_APP`
- `FLOW`

## Sample application

This branch does not commit the sample app binaries. Download them from `main`:

```
git clone --depth 1 --branch main https://github.com/applitools/NML.git nml-main
cp -R nml-main/static_instrumented_sample_application ./static_instrumented_sample_application
rm -rf nml-main
```

## Run

```
npx wdio run ./wdio.ios.local.conf.ts
```
