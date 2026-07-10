# java-appium-nml-android-multi-device-browserstack

## Environment variables

- `APPLITOOLS_API_KEY`
- `BROWSERSTACK_USERNAME`
- `BROWSERSTACK_ACCESS_KEY`

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXAndroidBrowserStackMultidevice_Test` by default. For `AccessibilityAndroidBrowserStackMultidevice_Test`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityAndroidBrowserStackMultidevice_Test
```

## Upload application to BrowserStack

```
curl -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" \
  -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  -F "file=@/path/to/YourApp.apk"
```

Response: `{"app_url":"bs://<app_id>"}`
