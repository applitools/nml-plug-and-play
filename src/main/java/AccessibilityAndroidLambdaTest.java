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
 * Android Accessibility App — Applitools NML + LambdaTest Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   LT_USERNAME            — LambdaTest username
 *   LT_ACCESS_KEY          — LambdaTest access key
 *   LT_APP_ACCESSIBILITY   — LambdaTest app URL (lt://...) for the Accessibility APK
 *
 * APP:
 *   Accessibility Android APK — upload to LambdaTest and paste the returned lt:// URL
 *   into LT_APP_ACCESSIBILITY, or replace the fallback value in APP_ID below.
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 */
public class AccessibilityAndroidLambdaTest {

    // ── App ID ──────────────────────────────────────────────────────────────
    private static final String APP_ID ="lt://APP1016034271783446544203731";

    // ── Update to match the Accessibility APK manifest ───────────────────────
    private static final String APP_PACKAGE  = "com.applitools.accessibilitytest";
    private static final String APP_ACTIVITY = "com.applitools.accessibilitytest.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — Accessibility Android");

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey      = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl   = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String ltUsername  = System.getenv("LT_USERNAME");
        String ltAccessKey = System.getenv("LT_ACCESS_TOKEN");


        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Move optionalIntentArguments into lt:options ─────────────────────
        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("user",          ltUsername);
        ltOptions.put("accessKey",     ltAccessKey);
        ltOptions.put("appium:deviceName",         "Galaxy S26");
        ltOptions.put("appium:platformVersion",    "16");
        ltOptions.put("build",         "Applitools-Android-Accessibility-Build");
        ltOptions.put("name",          "Applitools-Android-Accessibility-Test");
        ltOptions.put("isRealMobile",  "true");
        ltOptions.put("devicelog",     "true");
        ltOptions.put("visual",        "true");
        ltOptions.put("network",       "true");
        ltOptions.put("w3c",           "true");



        Object intentArguments = capabilities.getCapability("appium:optionalIntentArguments");
        if (intentArguments != null) {
            ltOptions.put("optionalIntentArguments", intentArguments);
            capabilities.setCapability("appium:optionalIntentArguments", (Object) null);
        }
        // iOS cap not needed for Android
        capabilities.setCapability("appium:processArguments", (Object) null);

        capabilities.setCapability("lt:options", ltOptions);

        // ── Driver ──────────────────────────────────────────────────────────
        System.out.println("Initialising AndroidDriver");

        AndroidDriver driver = new AndroidDriver(
                new URL("https://mobile-hub.lambdatest.com/wd/hub"),
                capabilities
        );

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("AndroidDriver ready");

        // ── Eyes ────────────────────────────────────────────────────────────

        Eyes eyes = new Eyes();

        Configuration config = new Configuration();
        config.setApiKey(apiKey);
        if (serverUrl != null) {
            config.setServerUrl(serverUrl);
        }
        config.setBatch(new BatchInfo("Java LambdaTest | NML | Android Accessibility"));
        config.setUseDom(true);
        config.setSendDom(true);
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "LambdaTest Android Accessibility App",
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
