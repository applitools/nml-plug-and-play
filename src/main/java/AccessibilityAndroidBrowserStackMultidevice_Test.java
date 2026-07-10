import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.AndroidDeviceTarget;
import com.applitools.eyes.visualgrid.model.AndroidMultiDeviceTarget;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Android Accessibility App — Applitools NML Multi-Device + BrowserStack Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *
 * APP:
 *   Accessibility Android APK — BrowserStack requires apps to be uploaded via their API
 *   first (https://api-cloud.browserstack.com/app-automate/upload). Paste the returned
 *   bs://... app id into APP_ID below (placeholder — not yet uploaded).
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 *
 * CREDENTIALS:
 *   BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY must be set as environment
 *   variables before running.
 */
public class AccessibilityAndroidBrowserStackMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Placeholder — upload the Accessibility APK via the BrowserStack API and replace.
    private static final String APP_ID = "bs://6f17cea1d44630c42013b3ffbc906251bc745835";

    // ── Update to match the Accessibility APK manifest ───────────────────────
    private static final String APP_PACKAGE  = "com.applitools.accessibilitytest";
    private static final String APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility Android");

        // ── Credentials ─────────────────────────────────────────────────────
        // Placeholders — no BrowserStack account/credentials exist in this workspace.
        String apiKey             = System.getenv("APPLITOOLS_API_KEY");
        String bstackUsername     = System.getenv("BROWSERSTACK_USERNAME");
        String bstackAccessKey    = System.getenv("BROWSERSTACK_ACCESS_KEY");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:deviceName",         "Samsung Galaxy S23");
        capabilities.setCapability("appium:platformVersion",    "13.0");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // appium:optionalIntentArguments stays a PLAIN top-level Appium capability on
        // BrowserStack (no vendor nesting needed) — we only need to
        // drop the iOS-only processArguments cap.
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        capabilities.setCapability("appium:processArguments", (Object) null);

        // ── Attach bstack:options ──────────────────────────────────────────────
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName",      bstackUsername);
        bstackOptions.put("accessKey",     bstackAccessKey);
        bstackOptions.put("projectName",   "Applitools-Android-Accessibility");
        bstackOptions.put("buildName",     "Applitools-Android-Accessibility-Build");
        bstackOptions.put("sessionName",   "Applitools-Android-Accessibility-Test");

        capabilities.setCapability("bstack:options", bstackOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising AndroidDriver");

        AndroidDriver driver = new AndroidDriver(
                new URL("https://hub-cloud.browserstack.com/wd/hub"),
                capabilities
        );

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("AndroidDriver ready");

        // ── Eyes ────────────────────────────────────────────────────────────
        System.setProperty("APPLITOOLS_SHOW_LOGS",    "true");
        System.setProperty("APPLITOOLS_VERBOSE_LOGS", "true");

        Eyes eyes = new Eyes();

        Configuration config = new Configuration();
        config.setApiKey(apiKey);
        config.setBatch(new BatchInfo("Java BrowserStack | NML | Android Accessibility | Multi Device"));
        config.setUseDom(true);
        config.setSendDom(true);
        config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25(), AndroidMultiDeviceTarget.Galaxy_S25_Ultra(), AndroidMultiDeviceTarget.Pixel_9());

        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "BrowserStack Android Accessibility App",
                    "Android Accessibility Validation"
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
