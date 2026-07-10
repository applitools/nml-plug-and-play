# javascript-wdio-appium-nml-ios-multi-device-perfecto

WDIO + Applitools NML — iOS on Perfecto (multi-device variant).

Ported from `javascript-wdio-appium-nml-ios-lambdatest` (capability shape) and
`javascript-wdio-appium-nml-android-multi-device-perfecto` (Perfecto hostname/options
pattern). Runs `analyticsX_nml_multidevice.ios.test.js`, which uses Applitools'
`Configuration.addMultiDeviceTarget(...)` to render the same checkpoints across
multiple simulated iPhone form factors from a single Perfecto session.

## Run

```
npm test
```

## Credential caveats

- `.env` carries a real, working `PERFECTO_CLOUD_NAME` / `PERFECTO_SECURITY_TOKEN`
  (the same Perfecto account already used and tested for the Android suites) and a
  real `APPLITOOLS_API_KEY`.
- `PERFECTO_APP_ANALYTICSX` / `PERFECTO_APP_ACCESSIBILITY` are **placeholders**: no iOS
  `.ipa` has been uploaded to this Perfecto account's media repository yet. Upload
  `AnalyticsXUIKit.ipa` / `AccessibilityTestUIKit.ipa` (see
  `wdio-js-nml-lambdatest_old/Sample-Application-Static-Instrument/ios/`) to the
  Perfecto media repository and paste the returned `PERFECTO:REPOSITORY:...` path into
  `.env` before running.
