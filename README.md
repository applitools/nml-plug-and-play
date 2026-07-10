# java-appium-nml-android-browserstack

## Environment variables

- `APPLITOOLS_API_KEY`
- `BROWSERSTACK_USERNAME`
- `BROWSERSTACK_ACCESS_KEY`

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXAndroidBrowserStackTest` by default. For `AccessibilityAndroidBrowserStackTest`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityAndroidBrowserStackTest
```
