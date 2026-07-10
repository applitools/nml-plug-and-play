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

/**
 * Android Accessibility App — Applitools NML + Perfecto Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   PERFECTO_CLOUD_NAME    — Perfecto cloud name
 *   PERFECTO_SECURITY_TOKEN — Perfecto security token
 *
 * APP:
 *   Accessibility Android APK — placeholder value. Upload the Accessibility APK to the
 *   Perfecto repository and paste the returned reference into APP_ID below.
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 */
public class AccessibilityAndroidPerfectoTest {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Placeholder — upload the Accessibility APK to the Perfecto repository and replace.
    private static final String APP_ID = "PERFECTO:REPOSITORY:PASTE_ACCESSIBILITY_APK_HERE";

    // ── Update to match the Accessibility APK manifest ───────────────────────
    private static final String APP_PACKAGE  = "com.applitools.accessibilitytest";
    private static final String APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility Android");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey             = System.getenv("APPLITOOLS_API_KEY");
        String perfectoCloudName = System.getenv("PERFECTO_CLOUD_NAME");
        String perfectoToken     = System.getenv("PERFECTO_SECURITY_TOKEN");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:deviceName",         "Samsung Galaxy S23");
        capabilities.setCapability("appium:platformVersion",    "13");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // appium:optionalIntentArguments stays a PLAIN top-level Appium capability on
        // Perfecto (no vendor nesting needed) — we only need to
        // drop the iOS-only processArguments cap.
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        capabilities.setCapability("appium:processArguments", (Object) null);

        // ── Attach perfecto:options ────────────────────────────────────────────
        Map<String, Object> perfectoOptions = new HashMap<>();
        perfectoOptions.put("securityToken", perfectoToken);

        capabilities.setCapability("perfecto:options", perfectoOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising AndroidDriver");

        AndroidDriver driver = new AndroidDriver(
                new URL("https://" + perfectoCloudName + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"),
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
        config.setBatch(new BatchInfo("Java Perfecto | NML | Accessibility"));
        config.setUseDom(true);
        config.setSendDom(true);
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "Perfecto Android Accessibility App",
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
