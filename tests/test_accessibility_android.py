"""
Android Accessibility App — Applitools NML + Local Appium Server / Emulator

APP:
  Accessibility Android APK — bundled locally under
  ./static_instrumented_sample_application/android/Accessibility/Accessibility_static_xmllayout.apk
"""
from applitools.selenium import Target

from conftest import app_path

APP = "static_instrumented_sample_application/android/Accessibility/Accessibility_static_xmllayout.apk"

# ── Update to match the Accessibility APK manifest ───────────────────────────
APP_PACKAGE = "com.applitools.accessibilitytest"
APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity"


def test_accessibility_android(make_driver, eyes):
    driver = make_driver(app_path(APP), APP_PACKAGE, APP_ACTIVITY)

    eyes.open(driver, "Local Android Accessibility App", "Android Accessibility Validation")

    eyes.check("Main Screen", Target.window())
    eyes.check("Main Screen | Fully", Target.window().fully())
