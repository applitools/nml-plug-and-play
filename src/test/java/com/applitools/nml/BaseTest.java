package com.applitools.nml;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.appium.Eyes;
import io.appium.java_client.android.AndroidDriver;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    protected static final String APPLITOOLS_API_KEY = env("APPLITOOLS_API_KEY");
    protected static final String EYES_APP_NAME      = env("EYES_APP_NAME",   "NML Android Sample");
    protected static final String EYES_BATCH_NAME    = env("EYES_BATCH_NAME", "NML Android Local Tests");
    protected static final String APPIUM_URL         = env("APPIUM_URL",      "http://127.0.0.1:4723");
    protected static final String AVD_NAME           = env("AVD_NAME",        "Pixel_8_API_35");
    protected static final String PLATFORM_VERSION   = env("PLATFORM_VERSION","15.0");

    protected static BatchInfo batch;

    protected static String env(String key) {
        String val = dotenv.get(key, null);
        return val != null ? val : System.getenv(key);
    }

    protected static String env(String key, String defaultValue) {
        String val = env(key);
        return val != null ? val : defaultValue;
    }

    @BeforeSuite
    public void setUpBatch() {
        batch = new BatchInfo(EYES_BATCH_NAME);
    }

    @AfterSuite
    public void tearDownBatch() {}

    protected AndroidDriver createDriver(String appPath, String appPackage, String appActivity) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",             "Android");
        caps.setCapability("appium:automationName",    "UiAutomator2");
        caps.setCapability("appium:deviceName",        AVD_NAME);
        caps.setCapability("appium:avd",               AVD_NAME);
        caps.setCapability("appium:platformVersion",   PLATFORM_VERSION);
        caps.setCapability("appium:app",               appPath);
        caps.setCapability("appium:appPackage",        appPackage);
        caps.setCapability("appium:appActivity",       appActivity);
        caps.setCapability("appium:fullReset",         true);
        caps.setCapability("appium:newCommandTimeout", 300);

        AndroidDriver driver = new AndroidDriver(new URL(APPIUM_URL), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    protected Eyes createEyes() {
        Configuration config = new Configuration();
        config.setApiKey(APPLITOOLS_API_KEY);
        config.setBatch(batch);
        config.setSendDom(true);

        Eyes eyes = new Eyes();
        eyes.setConfiguration(config);
        return eyes;
    }

    protected void setFontScale(String scale) throws Exception {
        new ProcessBuilder("adb", "shell", "settings", "put", "system", "font_scale", scale)
                .inheritIO().start().waitFor();
    }
}
