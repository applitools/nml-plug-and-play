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
