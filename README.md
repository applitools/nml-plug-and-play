# java-appium-nml-android-lambdatest

Java Appium + Applitools NML — Android on LambdaTest.

## Test classes

- `AccessibilityAndroidLambdaTest`
- `AnalyticsXAndroidLambdaTest`

## Required environment variables

- `APPLITOOLS_API_KEY` — Applitools API key
- `LT_USERNAME` — LambdaTest username
- `LT_ACCESS_TOKEN` — LambdaTest access key
- `APP_ID` — optional; overrides the default `lt://` app URL hardcoded in each test class

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXAndroidLambdaTest` by default. To run the other test class:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityAndroidLambdaTest
```
