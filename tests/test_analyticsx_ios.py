"""
iOS AnalyticsX App — Applitools NML + local Appium server

ENV VARS:
  FLOW — compact | full  (default: full)
         compact → Compact Dashboard → Compact Visual AI Playground
         full    → Dashboard → Visual AI Playground

APP:
  Application2 — AnalyticsX app (UIKit), statically instrumented with NML.
  Simulator .app by default; real-device .ipa when IOS_TARGET=real.
"""
import os
import time

from appium.webdriver.common.appiumby import AppiumBy
from applitools.selenium import Target

from conftest import app_path

REAL_DEVICE_APP = "static_instrumented_sample_application/ios/AnalyticsX/AnalyticsXUIKit.ipa"
SIMULATOR_APP = "static_instrumented_sample_application/ios/AnalyticsX/AnalyticsXUIKit-Simulator.app"

FLOW = os.getenv("FLOW", "full")


def test_analyticsx_ios(make_driver, eyes):
    driver = make_driver(app_path(REAL_DEVICE_APP, SIMULATOR_APP))

    eyes.open(driver, "Local iOS AnalyticsX App", "iOS AnalyticsX Validation")

    # ── Screen 1: Login ──────────────────────────────────────────────────────
    eyes.check("Main Page", Target.window().fully(False))
    eyes.check("Main Page | Fully", Target.window().fully())

    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "loginTitle")
    time.sleep(2)

    login_button = driver.find_element(AppiumBy.ACCESSIBILITY_ID, "loginButton")
    if not login_button.is_displayed():
        driver.execute_script(
            "mobile: dragFromToForDuration",
            {"duration": 1, "fromX": 200, "fromY": 300, "toX": 200, "toY": 1400},
        )
    login_button.click()

    # ── Screen 2: Flow Selector ──────────────────────────────────────────────
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "flowSelectorTitle")
    eyes.check("Flow Selector", Target.window().fully(False))

    # ── Flow Branch ──────────────────────────────────────────────────────────
    if FLOW == "compact":
        run_compact_flow(driver, eyes)
    else:
        run_full_flow(driver, eyes)


def run_compact_flow(driver, eyes):
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "compactFlowCard").click()
    time.sleep(3)
    eyes.check("Compact Dashboard", Target.window())

    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "compactVisualAICard").click()
    time.sleep(1)
    eyes.check("Compact Visual AI Playground", Target.window())


def run_full_flow(driver, eyes):
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "fullFlowCard").click()
    time.sleep(3)
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "dashboardTitle")
    eyes.check("Dashboard Screen", Target.window())

    time.sleep(2)
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "visualAIPlaygroundCard").click()
    time.sleep(3)
    driver.find_element(AppiumBy.ACCESSIBILITY_ID, "playgroundTitle")
    time.sleep(2)
    eyes.check("Visual AI Playground", Target.window())
