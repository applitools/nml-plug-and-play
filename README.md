# typescript-wdio-appium-nml-android-multi-device-browserstack

## Environment variables

- `APPLITOOLS_API_KEY`
- `BROWSERSTACK_USERNAME`
- `BROWSERSTACK_ACCESS_KEY`
- `BROWSERSTACK_APP_ANALYTICSX`
- `BROWSERSTACK_APP_ACCESSIBILITY`
- `ACTIVE_APP`
- `DEVICE_NAME`
- `PLATFORM_VERSION`
- `FLOW`

## Run

```
npm test
```

## Upload application to BrowserStack

```
curl -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" \
  -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  -F "file=@/path/to/YourApp.apk"
```

Response: `{"app_url":"bs://<app_id>"}`
