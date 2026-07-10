import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.IosMultiDeviceTarget;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * iOS Accessibility App — Applitools NML (multi-device) + BrowserStack Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 * APP:
 *   Application1 — Accessibility app (SwiftUI real device IPA)
 *   BrowserStack apps must be pre-uploaded via the App Live / App Automate
 *   Upload API (https://api-cloud.browserstack.com/app-automate/upload).
 *   Replace the hardcoded value in APP_ID below with the returned bs://... id.
 *
 * NOTE: BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY must be set as
 * environment variables before running.
 */
public class AccessibilityIOSBrowserStackMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // TODO: upload AccessibilityTestUIKit.ipa via the BrowserStack App Automate Upload API.
    private static final String APP_ID = "bs://13726cc85e4ce4810cd1dbd07a5f8e1ff66adea8";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility iOS");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey                = System.getenv("APPLITOOLS_API_KEY");
        String browserStackUsername  = System.getenv("BROWSERSTACK_USERNAME");
        String browserStackAccessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",            "iOS");
        capabilities.setCapability("appium:automationName",   "XCUITest");
        capabilities.setCapability("appium:deviceName",       "iPhone 14");
        capabilities.setCapability("appium:platformVersion",  "16");
        capabilities.setCapability("appium:newCommandTimeout", "300");
        capabilities.setCapability("appium:noReset",          false);
        capabilities.setCapability("appium:app",              APP_ID);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── bstack:options ──────────────────────────────────────────────────
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName",     browserStackUsername);
        bstackOptions.put("accessKey",    browserStackAccessKey);
        bstackOptions.put("projectName",  "Applitools iOS Accessibility");
        bstackOptions.put("buildName",    "Applitools-iOS-Accessibility-Build");
        bstackOptions.put("sessionName",  "Applitools-iOS-Accessibility-Test");

        capabilities.setCapability("bstack:options", bstackOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising IOSDriver");

        IOSDriver driver = new IOSDriver(
                new URL("https://hub-cloud.browserstack.com/wd/hub"),
                capabilities
        );

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("IOSDriver ready");

        // ── Eyes ────────────────────────────────────────────────────────────
        System.setProperty("APPLITOOLS_SHOW_LOGS",    "true");
        System.setProperty("APPLITOOLS_VERBOSE_LOGS", "true");

        Eyes eyes = new Eyes();

        Configuration config = new Configuration();
        config.setApiKey(apiKey);
        config.setUseDom(true);
        config.setSendDom(true);

        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro(), IosMultiDeviceTarget.iPhone_11_Pro_Max());

        eyes.setConfiguration(config);

        eyes.setBatch(new BatchInfo("Java BrowserStack | NML | iOS Accessibility | Multi Device"));

        try {

            eyes.open(
                    driver,
                    "BrowserStack iOS Accessibility App",
                    "iOS Accessibility Validation"
            );
            System.out.println("Eyes open");


            eyes.check("Main Screen", Target.window());
            System.out.println("Checked: Main Screen");

            eyes.check("Main Screen | Fully", Target.window().fully());

            eyes.close();
            System.out.println("Eyes closed");

        } catch (Exception e) {

            eyes.abort();
            System.out.println("Exception — eyes aborted: " + e.getMessage());
            throw e;

        } finally {

            driver.quit();
            System.out.println("Driver quit");
        }
    }
}
