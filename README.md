# typescript-wdio-appium-android

## Environment variables

- `APPLITOOLS_API_KEY`
- `AVD_NAME`
- `PLATFORM_VERSION`
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
npx wdio run ./wdio.android.local.conf.ts
```
