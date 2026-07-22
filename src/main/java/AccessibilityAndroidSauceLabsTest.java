import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;

import com.applitools.eyes.config.Configuration;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Android Accessibility App — Applitools NML + Sauce Labs Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   SAUCE_USERNAME         — Sauce Labs username
 *   SAUCE_ACCESS_KEY       — Sauce Labs access key
 *   SAUCE_REGION           — Sauce Labs data center region (default: us-west-1)
 *
 * APP:
 *   Accessibility Android APK — uploaded to Sauce Labs app storage under the filename
 *   referenced by APP_ID below. Update APP_PACKAGE and APP_ACTIVITY to match the app's
 *   manifest if you swap in a different build.
 */
public class AccessibilityAndroidSauceLabsTest {

    // ── App ID ──────────────────────────────────────────────────────────────
    private static final String APP_ID = "storage:filename=Accessibility_static_xmllayout.apk";

    // ── Update to match the Accessibility APK manifest ───────────────────────
    private static final String APP_PACKAGE  = "com.applitools.accessibilitytest";
    private static final String APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility Android");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey           = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl        = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String sauceUsername    = System.getenv("SAUCE_USERNAME");
        String sauceAccessKey   = System.getenv("SAUCE_ACCESS_KEY");
        String sauceRegion      = Optional.ofNullable(System.getenv("SAUCE_REGION")).orElse("us-west-1");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:deviceName",         "Samsung Galaxy S22");
        capabilities.setCapability("appium:platformVersion",    "16");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // appium:optionalIntentArguments stays a PLAIN top-level Appium capability on
        // Sauce Labs (no vendor nesting needed, unlike LambdaTest). The iOS-only
        // processArguments cap is left as-is too — Eyes.setMobileCapabilities() sets
        // both unconditionally, regardless of platform.
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Attach sauce:options ──────────────────────────────────────────────
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username",   sauceUsername);
        sauceOptions.put("accessKey",  sauceAccessKey);
        sauceOptions.put("build",      "Applitools-Android-Accessibility-Build");
        sauceOptions.put("name",       "Applitools-Android-Accessibility-Test");
        // appiumVersion is required for Android 14+ real devices, which Sauce only accepts over Appium 2 / W3C
        sauceOptions.put("appiumVersion", "latest");

        capabilities.setCapability("sauce:options", sauceOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising AndroidDriver");

        AndroidDriver driver = new AndroidDriver(
                new URL("https://ondemand." + sauceRegion + ".saucelabs.com/wd/hub"),
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
        if (serverUrl != null) {
            config.setServerUrl(serverUrl);
        }
        config.setBatch(new BatchInfo("Java SauceLabs | NML | Android Accessibility"));
        config.setUseDom(true);
        config.setSendDom(true);
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "Sauce Labs Android Accessibility App",
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
