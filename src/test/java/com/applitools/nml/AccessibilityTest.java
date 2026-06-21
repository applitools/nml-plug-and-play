package com.applitools.nml;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class AccessibilityTest extends BaseTest {

    private static final String APP_PATH     = env("ACCESSIBILITY_APP_PATH",
            "SampleApplication/Applitcation1-accessibility-app/Android Native/AndroidX/accessibility-test-androidxml.apk");
    private static final String APP_PACKAGE  = env("ACCESSIBILITY_APP_PACKAGE",  "com.applitools.accessibilitytest");
    private static final String APP_ACTIVITY = env("ACCESSIBILITY_APP_ACTIVITY", "com.applitools.accessibilitytest.MainActivity");

    private AndroidDriver driver;
    private Eyes eyes;

    @AfterMethod
    public void tearDown() throws Exception {
        if (eyes   != null) eyes.abortIfNotClosed();
        if (driver != null) driver.quit();
        setFontScale("1.0");
    }

    @Test
    public void normalFontSize() throws Exception {
        setFontScale("1.0");
        driver = createDriver(APP_PATH, APP_PACKAGE, APP_ACTIVITY);
        eyes   = createEyes();

        Thread.sleep(2000);
        eyes.open(driver, EYES_APP_NAME, "Accessibility - Normal Font Size");
        eyes.check(Target.window().withName("Normal Font"));
        eyes.close();
    }

    @Test
    public void largeFontSize() throws Exception {
        setFontScale("2.0");
        driver = createDriver(APP_PATH, APP_PACKAGE, APP_ACTIVITY);
        eyes   = createEyes();

        Thread.sleep(2000);
        eyes.open(driver, EYES_APP_NAME, "Accessibility - Large Font Size");
        eyes.check(Target.window().withName("Large Font"));
        eyes.close();
    }
}
