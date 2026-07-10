# java-appium-nml-android-multi-device-perfecto

## Environment variables

- `APPLITOOLS_API_KEY`
- `PERFECTO_CLOUD_NAME`
- `PERFECTO_SECURITY_TOKEN`
- `FLOW`

## Run

```
mvn compile exec:java
```

Runs `AnalyticsXAndroidPerfectoMultidevice_Test` by default. For `AccessibilityAndroidPerfectoMultidevice_Test`:

```
mvn compile exec:java -Dexec.mainClass=AccessibilityAndroidPerfectoMultidevice_Test
```
