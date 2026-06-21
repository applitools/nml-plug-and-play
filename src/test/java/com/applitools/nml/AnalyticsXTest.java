package com.applitools.nml;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * AnalyticsX full-flow test demonstrating NML advantages:
 *  - sendDom(true)  → captures native accessibility tree for root-cause analysis
 *  - Target.window().fully() → automatic full-page screenshot via NML scrolling
 */
public class AnalyticsXTest extends BaseTest {

    private static final String APP_PATH  = env("ANALYTICSX_APP_PATH",
            "SampleApplication/Application2-analyticsx-app/IOS Native/UIKit/Emulator/AnalyticsXUIKit-Simulator.app");
    private static final String BUNDLE_ID = env("ANALYTICSX_BUNDLE_ID", "com.applitools.AnalyticsXUIKit");

    private IOSDriver driver;
    private Eyes eyes;

    @BeforeMethod
    public void setUp() throws Exception {
        driver = createDriver(APP_PATH, BUNDLE_ID);
        eyes   = createEyes();
    }

    @AfterMethod
    public void tearDown() {
        if (eyes   != null) eyes.abortIfNotClosed();
        if (driver != null) driver.quit();
    }

    @Test
    public void analyticsXFullFlow() throws Exception {
        Thread.sleep(2000);
        eyes.open(driver, EYES_APP_NAME, "AnalyticsX - Full Flow");

        // ── Step 1: Login screen ─────────────────────────────────────────────
        // fully() captures the complete screen including any content below the fold.
        // sendDom(true) sends the native accessibility tree so Applitools can
        // pinpoint exactly which element changed during diff analysis.
        eyes.check(Target.window().fully().withName("Login Screen"));

        // ── Step 2: Login ─────────────────────────────────────────────────────
        driver.findElement(AppiumBy.accessibilityId("Login")).click();
        Thread.sleep(2000);

        // ── Step 3: Home / Dashboard ──────────────────────────────────────────
        // NML stitches multiple native screenshots into one full-page capture,
        // even for long scrollable lists or charts that exceed the viewport.
        eyes.check(Target.window().fully().withName("Home Dashboard"));

        // ── Step 4: Navigate into first item / detail view ────────────────────
        driver.findElement(AppiumBy.accessibilityId("Dashboard Item")).click();
        Thread.sleep(1500);
        eyes.check(Target.window().fully().withName("Detail View"));

        // ── Step 5: Scroll down to capture content below the fold ─────────────
        driver.executeScript("mobile: scroll", Map.of("direction", "down"));
        Thread.sleep(1000);
        eyes.check(Target.window().fully().withName("Detail View Scrolled"));

        // ── Step 6: Navigate back to the dashboard ────────────────────────────
        driver.navigate().back();
        Thread.sleep(1500);
        eyes.check(Target.window().fully().withName("Home Dashboard After Navigation"));

        eyes.close();
    }
}
