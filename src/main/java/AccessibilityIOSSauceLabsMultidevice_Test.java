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
import java.util.Optional;

/**
 * iOS Accessibility App — Applitools NML (multi-device) + Sauce Labs Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 *   SAUCE_USERNAME       — Sauce Labs username
 *   SAUCE_ACCESS_KEY     — Sauce Labs access key
 *   SAUCE_REGION         — Sauce Labs data center region (default: us-west-1)
 * APP:
 *   Application1 — Accessibility app (SwiftUI real device IPA)
 *   Upload the IPA to Sauce Labs Temporary Storage (Sauce Storage API) and
 *   replace the hardcoded value in APP_ID below with the returned
 *   storage:filename=... reference.
 */
public class AccessibilityIOSSauceLabsMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // TODO: upload AccessibilityTestUIKit.ipa to Sauce Labs storage and confirm the filename.
    private static final String APP_ID = "storage:filename=AccessibilityTestUIKit.ipa";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility iOS");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey          = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl       = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String sauceUsername    = System.getenv("SAUCE_USERNAME");
        String sauceAccessKey   = System.getenv("SAUCE_ACCESS_KEY");
        String sauceRegion      = Optional.ofNullable(System.getenv("SAUCE_REGION")).orElse("us-west-1");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",            "iOS");
        capabilities.setCapability("appium:automationName",   "XCUITest");
        capabilities.setCapability("appium:deviceName",       "iPhone 14");
        capabilities.setCapability("appium:platformVersion",  "17");
        capabilities.setCapability("appium:newCommandTimeout", "300");
        capabilities.setCapability("appium:noReset",          false);
        capabilities.setCapability("appium:app",              APP_ID);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Eyes.setMobileCapabilities injects appium:processArguments. Sauce Labs
        // does not require it to be nested under sauce:options, so it stays a
        // plain top-level Appium capability.
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── sauce:options ───────────────────────────────────────────────────
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username",   sauceUsername);
        sauceOptions.put("accessKey",  sauceAccessKey);
        sauceOptions.put("build",      "Applitools-iOS-Accessibility-Build");
        sauceOptions.put("name",       "Applitools-iOS-Accessibility-Test");
        // appiumVersion is required for iOS 17+ real devices, which Sauce only accepts over Appium 2 / W3C
        sauceOptions.put("appiumVersion", "latest");

        capabilities.setCapability("sauce:options", sauceOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising IOSDriver");

        IOSDriver driver = new IOSDriver(
                new URL("https://ondemand." + sauceRegion + ".saucelabs.com/wd/hub"),
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
        if (serverUrl != null) {
            config.setServerUrl(serverUrl);
        }
        config.setUseDom(true);
        config.setSendDom(true);

        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro(), IosMultiDeviceTarget.iPhone_11_Pro_Max());
        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro().portrait());

        eyes.setConfiguration(config);

        eyes.setBatch(new BatchInfo("Java SauceLabs | NML | iOS Accessibility | Multi Device"));

        try {

            eyes.open(
                    driver,
                    "Sauce Labs iOS Accessibility App",
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
