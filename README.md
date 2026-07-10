# java-appium-nml-android-perfecto

## Environment variables

- `APPLITOOLS_API_KEY`
- `PERFECTO_CLOUD_NAME`
- `PERFECTO_SECURITY_TOKEN`
- `FLOW`

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXAndroidPerfectoTest` by default. For `AccessibilityAndroidPerfectoTest`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityAndroidPerfectoTest
```
