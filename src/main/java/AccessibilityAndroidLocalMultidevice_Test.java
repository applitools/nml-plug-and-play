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
import java.util.Optional;

/**
 * Android Accessibility App — Applitools NML Multi-Device + Local Appium Server / Emulator
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   AVD_NAME               — Android emulator/device name  (default: Pixel_8_API_35)
 *   PLATFORM_VERSION       — Android platform version       (default: 15.0)
 *
 * APP:
 *   Accessibility Android APK — bundled locally under
 *   ./static_instrumented_sample_application/android/Accessibility/Accessibility_static_xmllayout.apk
 *
 * PREREQUISITE:
 *   Start a local Appium server first, e.g.:
 *     appium
 *   (listens on http://127.0.0.1:4723 by default)
 */
public class AccessibilityAndroidLocalMultidevice_Test {

    // ── App path (relative to project root) ────────────────────────────────
    private static final String APP_PATH =
            "./static_instrumented_sample_application/android/Accessibility/Accessibility_static_xmllayout.apk";

    // ── Update to match the Accessibility APK manifest ───────────────────────
    private static final String APP_PACKAGE  = "com.applitools.accessibilitytest";
    private static final String APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility Android");

        // ── Credentials / device config ────────────────────────────────────
        String apiKey           = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl        = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String avdName          = Optional.ofNullable(System.getenv("AVD_NAME")).orElse("Pixel_8_API_35");
        String platformVersion  = Optional.ofNullable(System.getenv("PLATFORM_VERSION")).orElse("15.0");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:deviceName",         avdName);
        capabilities.setCapability("appium:platformVersion",    platformVersion);
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_PATH);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Against a local Appium server there is no vendor options object at all —
        // optionalIntentArguments stays a plain top-level Appium capability — we only
        // need to drop the iOS-only processArguments cap.
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising AndroidDriver");

        AndroidDriver driver = new AndroidDriver(
                new URL("http://127.0.0.1:4723/wd/hub"),
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
        config.setBatch(new BatchInfo("Java Local | NML | Android Accessibility | Multi Device"));
        config.setUseDom(true);
        config.setSendDom(true);
        config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25(), AndroidMultiDeviceTarget.Galaxy_S25_Ultra(), AndroidMultiDeviceTarget.Pixel_9());

        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "Local Android Accessibility App",
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
