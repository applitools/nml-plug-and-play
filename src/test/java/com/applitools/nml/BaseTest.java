package com.applitools.nml;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Configuration;
import com.applitools.eyes.appium.Eyes;
import io.appium.java_client.ios.IOSDriver;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    protected static final String APPLITOOLS_API_KEY = env("APPLITOOLS_API_KEY");
    protected static final String EYES_APP_NAME      = env("EYES_APP_NAME",   "NML iOS Sample");
    protected static final String EYES_BATCH_NAME    = env("EYES_BATCH_NAME", "NML iOS Local Tests");
    protected static final String APPIUM_URL         = env("APPIUM_URL",      "http://127.0.0.1:4723");
    protected static final String DEVICE_NAME        = env("DEVICE_NAME",     "iPhone 16");
    protected static final String DEVICE_UDID        = env("DEVICE_UDID",     "");
    protected static final String PLATFORM_VERSION   = env("PLATFORM_VERSION","18.0");

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
    public void tearDownBatch() {
        // no-op — Eyes closes per test
    }

    protected IOSDriver createDriver(String appPath, String bundleId) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName",             "iOS");
        caps.setCapability("appium:automationName",    "XCUITest");
        caps.setCapability("appium:deviceName",        DEVICE_NAME);
        caps.setCapability("appium:udid",              DEVICE_UDID);
        caps.setCapability("appium:platformVersion",   PLATFORM_VERSION);
        caps.setCapability("appium:app",               appPath);
        caps.setCapability("appium:bundleId",          bundleId);
        caps.setCapability("appium:noReset",           false);
        caps.setCapability("appium:newCommandTimeout", 300);

        IOSDriver driver = new IOSDriver(new URL(APPIUM_URL), caps);
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
}
