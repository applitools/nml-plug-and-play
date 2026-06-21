package com.applitools.nml;

import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AnalyticsXTest extends BaseTest {

    private static final String APP_PATH     = env("ANALYTICSX_APP_PATH",
            "SampleApplication/Application2-analyticsx-app/Android Native/XMLLayout/AnalyticsXAndroid.apk");
    private static final String APP_PACKAGE  = env("ANALYTICSX_APP_PACKAGE",  "");
    private static final String APP_ACTIVITY = env("ANALYTICSX_APP_ACTIVITY", "");

    private AndroidDriver driver;
    private Eyes eyes;

    @BeforeMethod
    public void setUp() throws Exception {
        driver = createDriver(APP_PATH, APP_PACKAGE, APP_ACTIVITY);
        eyes   = createEyes();
    }

    @AfterMethod
    public void tearDown() {
        if (eyes   != null) eyes.abortIfNotClosed();
        if (driver != null) driver.quit();
    }

    @Test
    public void loginFlow() throws Exception {
        Thread.sleep(2000);
        eyes.open(driver, EYES_APP_NAME, "AnalyticsX - Login");
        eyes.check(Target.window().withName("Login Screen"));

        WebElement loginButton = driver.findElement(
                AppiumBy.androidUIAutomator("new UiSelector().text(\"Login\")")
        );
        loginButton.click();
        Thread.sleep(2000);

        eyes.check(Target.window().withName("Post Login"));
        eyes.close();
    }
}
