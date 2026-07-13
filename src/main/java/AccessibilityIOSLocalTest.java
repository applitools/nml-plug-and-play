import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;

import com.applitools.eyes.config.Configuration;
import io.appium.java_client.ios.IOSDriver;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.Optional;

/**
 * iOS Accessibility App — Applitools NML + local Appium server
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 * APP:
 *   Application1 — Accessibility app (SwiftUI real device IPA).
 *   APP_ID below points at the bundled static_instrumented_sample_application
 *   IPA. NOTE: a real-device .ipa generally will NOT boot on the iOS
 *   Simulator — this local variant targets a real device connected via
 *   Appium's XCUITest driver. To run against the Simulator instead, swap
 *   APP_ID for your own .app build.
 *
 * PREREQUISITES:
 *   - A local Appium server running at http://127.0.0.1:4723 (`appium`)
 *   - A real iOS device connected and provisioned for XCUITest, or a
 *     Simulator-compatible .app if you replace APP_ID
 */
public class AccessibilityIOSLocalTest {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Real-device build (used when IOS_TARGET=real).
    private static final String APP_ID = "./static_instrumented_sample_application/ios/Accessibility/AccessibilityTestUIKit.ipa";
    // Simulator build (used when IOS_TARGET=simulator, the default).
    private static final String SIMULATOR_APP_ID = "./static_instrumented_sample_application/ios/Accessibility/AccessibilityTestUIKit.app";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility iOS");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey = System.getenv("APPLITOOLS_API_KEY");

        // Toggle: IOS_TARGET=real runs against a physically connected iPhone
        // (Appium auto-detects the UDID; set IOS_UDID to target a specific
        // device) using the real-device .ipa. Anything else (default) targets
        // a named Simulator via DEVICE_NAME/PLATFORM_VERSION using the
        // Simulator-built .app.
        String iosTarget = Optional.ofNullable(System.getenv("IOS_TARGET")).orElse("simulator").trim().toLowerCase();

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",            "iOS");
        capabilities.setCapability("appium:automationName",   "XCUITest");
        capabilities.setCapability("appium:newCommandTimeout", "300");
        capabilities.setCapability("appium:noReset",          false);
        capabilities.setCapability("appium:app",              iosTarget.equals("real") ? APP_ID : SIMULATOR_APP_ID);

        if (iosTarget.equals("real")) {
            String udid = System.getenv("IOS_UDID");
            if (udid != null && !udid.isEmpty()) {
                capabilities.setCapability("appium:udid", udid);
            }
        } else {
            capabilities.setCapability("appium:deviceName",      Optional.ofNullable(System.getenv("DEVICE_NAME")).orElse("iPhone 14"));
            capabilities.setCapability("appium:platformVersion", Optional.ofNullable(System.getenv("PLATFORM_VERSION")).orElse("17"));
        }

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Eyes.setMobileCapabilities injects appium:processArguments. A local
        // Appium server needs no vendor-specific nesting, so it stays a plain
        // top-level Appium capability.
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising IOSDriver");

        IOSDriver driver = new IOSDriver(
                new URL("http://127.0.0.1:4723/wd/hub"),
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
        eyes.setConfiguration(config);

        eyes.setBatch(new BatchInfo("Java Local | iOS Accessibility"));

        try {

            eyes.open(
                    driver,
                    "Local iOS Accessibility App",
                    "iOS Accessibility Validation"
            );
            System.out.println("Eyes open");


            eyes.check("Main Screen", Target.window().fully(false));
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
