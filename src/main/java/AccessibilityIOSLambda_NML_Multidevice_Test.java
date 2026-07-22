import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.IosMultiDeviceTarget;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * iOS Accessibility App — Applitools NML + LambdaTest Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 *   LT_USERNAME          — LambdaTest username
 *   LT_ACCESS_KEY        — LambdaTest access key
 * APP:
 *   Application1 — Accessibility app (SwiftUI real device IPA)
 *   Upload to LambdaTest and set LT_APP_ACCESSIBILITY to the returned lt:// URL,
 *   or replace the hardcoded value in APP_ID below.
 */
public class AccessibilityIOSLambda_NML_Multidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Replace with your uploaded LambdaTest app URL, e.g. lt://APP123...
    private static final String APP_ID = "lt://APP1016034271783487183474806";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility iOS");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey      = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl   = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String ltUsername  = System.getenv("LT_USERNAME");
        String ltAccessKey = System.getenv("LT_ACCESS_KEY");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",            "iOS");
        capabilities.setCapability("appium:automationName",   "XCUITest");
        capabilities.setCapability("appium:newCommandTimeout", "300");
        capabilities.setCapability("appium:noReset",          false);
        capabilities.setCapability("appium:app",              APP_ID);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Move processArguments into lt:options ────────────────────────────
        // Applitools injects processArguments; LambdaTest requires them inside lt:options.
        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("user",          ltUsername);
        ltOptions.put("accessKey",     ltAccessKey);
        ltOptions.put("build",         "Applitools-iOS-Accessibility-Build");
        ltOptions.put("name",          "Applitools-iOS-Accessibility-Test");
        ltOptions.put("appium:deviceName","iPhone 16 Pro");
        ltOptions.put("appium:platformVersion","18");
        ltOptions.put("isRealMobile",  "true");
        ltOptions.put("devicelog",     "true");
        ltOptions.put("visual",        "true");
        ltOptions.put("network",       "true");
        ltOptions.put("w3c",           "true");

        Object processArguments =
                capabilities.getCapability("appium:processArguments");

        if (processArguments != null) {
            ltOptions.put("processArguments", processArguments);
            capabilities.setCapability("appium:processArguments", (Object) null);
        }

        capabilities.setCapability("lt:options", ltOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising IOSDriver");

        IOSDriver driver = new IOSDriver(
                new URL("https://mobile-hub.lambdatest.com/wd/hub"),
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

        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro(), IosMultiDeviceTarget.iPhone_11_Pro_Max(), IosMultiDeviceTarget.iPhone_12(), IosMultiDeviceTarget.iPhone_13_Pro_Max());
        eyes.setConfiguration(config);

        eyes.setBatch(new BatchInfo("Java LambdaTest | Static/Slicing Dynamic | NML | iOS Accessibility | Multi Device"));

        try {

            eyes.open(
                    driver,
                    "LambdaTest iOS Accessibility App",
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
