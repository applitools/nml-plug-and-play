# java-appium-nml-ios-multi-device-saucelabs

Java Appium + Applitools NML — iOS on Sauce Labs (multi-device variant).

Ported from `java-ios-nml-lambdatest_old`.

## Run

```
mvn compile exec:java
```

APPLITOOLS_API_KEY, SAUCE_USERNAME, SAUCE_ACCESS_KEY, and SAUCE_REGION (optional, default us-west-1) must be set as environment variables before running. `APP_ID` is a placeholder (`storage:filename=AnalyticsXUIKit.ipa` / `AccessibilityTestUIKit.ipa`) — upload the corresponding IPA to Sauce Labs Temporary Storage via the Sauce Storage API and confirm the filename before running.
