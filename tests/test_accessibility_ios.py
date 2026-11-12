"""
iOS Accessibility App — Applitools NML + local Appium server

APP:
  Application1 — Accessibility app (UIKit), statically instrumented with NML.
  Simulator .app by default; real-device .ipa when IOS_TARGET=real.
"""
from applitools.selenium import Target

from conftest import app_path

REAL_DEVICE_APP = "static_instrumented_sample_application/ios/Accessibility/AccessibilityTestUIKit.ipa"
SIMULATOR_APP = "static_instrumented_sample_application/ios/Accessibility/AccessibilityTestUIKit.app"


def test_accessibility_ios(make_driver, eyes):
    driver = make_driver(app_path(REAL_DEVICE_APP, SIMULATOR_APP))

    eyes.open(driver, "Local iOS Accessibility App", "iOS Accessibility Validation")

    eyes.check("Main Screen", Target.window().fully(False))
    eyes.check("Main Screen | Fully", Target.window().fully())
