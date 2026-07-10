# java-appium-nml-ios-browserstack

## Environment variables

- `APPLITOOLS_API_KEY`
- `BROWSERSTACK_USERNAME`
- `BROWSERSTACK_ACCESS_KEY`

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXIOSBrowserStackTest` by default. For `AccessibilityIOSBrowserStackTest`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityIOSBrowserStackTest
```

## Upload application to BrowserStack

```
curl -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" \
  -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  -F "file=@/path/to/YourApp.ipa"
```

Response: `{"app_url":"bs://<app_id>"}`
