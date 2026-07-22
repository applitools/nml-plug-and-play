import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.AndroidMultiDeviceTarget;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Android AnalyticsX App — Applitools NML + LambdaTest Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   LT_USERNAME            — LambdaTest username
 *   LT_ACCESS_KEY          — LambdaTest access key
 *   LT_APP_ANALYTICSX      — LambdaTest app URL (lt://...) for the AnalyticsX APK
 *   FLOW                   — compact | full  (default: full)
 *                            compact → Compact Dashboard → Compact Visual AI Playground
 *                            full    → Dashboard → Visual AI Playground
 *
 * APP:
 *   AnalyticsX Android APK — upload to LambdaTest and paste the returned lt:// URL
 *   into LT_APP_ANALYTICSX, or replace the fallback value in APP_ID below.
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 */
public class AnalyticsXAndroidLambda_NMLMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    private static final String APP_ID ="lt://APP10160262301783505342365395";

    // ── Update to match the AnalyticsX APK manifest ─────────────────────────
    private static final String APP_PACKAGE  = "com.applitools.analyticsx";
    private static final String APP_ACTIVITY = "com.applitools.analyticsx.MainActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — AnalyticsX Android");

        // ── Toggles ─────────────────────────────────────────────────────────
        String flow = Optional.ofNullable(System.getenv("FLOW")).orElse("full");
        System.out.println("FLOW = " + flow);

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey      = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl   = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String ltUsername  = System.getenv("LT_USERNAME");
        String ltAccessKey = System.getenv("LT_ACCESS_TOKEN");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:platformVersion",    "16");
        capabilities.setCapability("appium:deviceName",         "Galaxy S26");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Eyes.setMobileCapabilities injects BOTH optionalIntentArguments (Android)
        // AND processArguments (iOS) — we move optionalIntentArguments into lt:options
        // and delete processArguments (not needed for Android).
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Move optionalIntentArguments into lt:options ─────────────────────
        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("user",          ltUsername);
        ltOptions.put("accessKey",     ltAccessKey);
        ltOptions.put("appium:deviceName",         "Galaxy S26");
        ltOptions.put("appium:platformVersion",    "16");
        ltOptions.put("build",         "Applitools-Android-AnalyticsX-Build");
        ltOptions.put("name",          "Applitools-Android-AnalyticsX-Test");
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
        config.setBatch(new BatchInfo("Java LambdaTest | NML | Android AnalyticsX | Multi Device"));
        config.setUseDom(true);
        config.setSendDom(true);
        config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25(), AndroidMultiDeviceTarget.Galaxy_S25_Ultra(), AndroidMultiDeviceTarget.Pixel_9());
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "LambdaTest Android AnalyticsX App",
                    "Android AnalyticsX Validation"
            );
            System.out.println("Eyes open");

            // ── Screen 1: Login ──────────────────────────────────────────────
            eyes.check("Main Page", Target.window());
            System.out.println("Checked: Main Page");

            driver.findElement(AppiumBy.accessibilityId("loginTitle"));
            System.out.println("Login page visible");

            Thread.sleep(2000);

            WebElement loginButton =
                    driver.findElement(AppiumBy.accessibilityId("loginButton"));

            if (!loginButton.isDisplayed()) {
                driver.executeScript(
                        "mobile: scrollGesture",
                        Map.of(
                                "left",      100,
                                "top",       300,
                                "width",     200,
                                "height",    600,
                                "direction", "down",
                                "percent",   0.75
                        )
                );
            }

            loginButton.click();
            System.out.println("Login button clicked");

            // ── Screen 2: Flow Selector ──────────────────────────────────────
            driver.findElement(AppiumBy.accessibilityId("flowSelectorTitle"));

            eyes.check("Flow Selector", Target.window().fully());
            System.out.println("Checked: Flow Selector");

            // ── Flow Branch ──────────────────────────────────────────────────
            if (flow.equals("compact")) {
                runCompactFlow(driver, eyes);
            } else {
                runFullFlow(driver, eyes);
            }

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

    // ── Compact Flow ─────────────────────────────────────────────────────────
    private static void runCompactFlow(AndroidDriver driver, Eyes eyes)
            throws Exception {

        System.out.println("Running COMPACT flow");

        driver.findElement(AppiumBy.accessibilityId("compactFlowCard")).click();
        Thread.sleep(3000);

        eyes.check("Compact Dashboard", Target.window().fully());
        System.out.println("Checked: Compact Dashboard");

        driver.findElement(AppiumBy.accessibilityId("compactVisualAICard")).click();
        Thread.sleep(1000);

        eyes.check("Compact Visual AI Playground", Target.window().fully());
        System.out.println("Checked: Compact Visual AI Playground");
    }

    // ── Full Flow ─────────────────────────────────────────────────────────────
    private static void runFullFlow(AndroidDriver driver, Eyes eyes)
            throws Exception {

        System.out.println("Running FULL flow");

        driver.findElement(AppiumBy.accessibilityId("fullFlowCard")).click();
        Thread.sleep(3000);

        driver.findElement(AppiumBy.accessibilityId("dashboardTitle"));

        eyes.check("Dashboard Screen", Target.window().fully());
        System.out.println("Checked: Dashboard Screen");

        Thread.sleep(2000);

        driver.findElement(AppiumBy.accessibilityId("visualAIPlaygroundCard")).click();
        Thread.sleep(3000);

        driver.findElement(AppiumBy.accessibilityId("playgroundTitle"));
        Thread.sleep(2000);

        eyes.check("Visual AI Playground", Target.window().fully());
        System.out.println("Checked: Visual AI Playground");
    }
}
