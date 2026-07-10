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
 * Android AnalyticsX App — Applitools NML Multi-Device + BrowserStack Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   FLOW                   — compact | full  (default: full)
 *                            compact → Compact Dashboard → Compact Visual AI Playground
 *                            full    → Dashboard → Visual AI Playground
 *
 * APP:
 *   AnalyticsX Android APK — BrowserStack requires apps to be uploaded via their API
 *   first (https://api-cloud.browserstack.com/app-automate/upload). Paste the returned
 *   bs://... app id into APP_ID below (placeholder — not yet uploaded).
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 *
 * CREDENTIALS:
 *   BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY must be set as environment
 *   variables before running.
 */
public class AnalyticsXAndroidBrowserStackMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Placeholder — upload the AnalyticsX APK via the BrowserStack API and replace.
    private static final String APP_ID = "bs://d47d523298b773262a9e121e39410f89e4ea1ffe";

    // ── Update to match the AnalyticsX APK manifest ─────────────────────────
    private static final String APP_PACKAGE  = "com.apexlytics.analyticsxandroid";
    private static final String APP_ACTIVITY = "com.apexlytics.analyticsxandroid.LoginActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — AnalyticsX Android");

        // ── Toggles ─────────────────────────────────────────────────────────
        String flow = Optional.ofNullable(System.getenv("FLOW")).orElse("full");
        System.out.println("FLOW = " + flow);

        // ── Credentials ─────────────────────────────────────────────────────
        // Placeholders — no BrowserStack account/credentials exist in this workspace.
        String apiKey             = System.getenv("APPLITOOLS_API_KEY");
        String bstackUsername     = System.getenv("BROWSERSTACK_USERNAME");
        String bstackAccessKey    = System.getenv("BROWSERSTACK_ACCESS_KEY");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:platformVersion",    "13.0");
        capabilities.setCapability("appium:deviceName",         "Samsung Galaxy S23");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Eyes.setMobileCapabilities injects BOTH optionalIntentArguments (Android)
        // AND processArguments (iOS). On BrowserStack, appium:optionalIntentArguments
        // stays a PLAIN top-level Appium capability (no vendor nesting needed)
        // — we only need to drop the iOS-only processArguments cap.
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        capabilities.setCapability("appium:processArguments", (Object) null);

        // ── Attach bstack:options ──────────────────────────────────────────────
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName",      bstackUsername);
        bstackOptions.put("accessKey",     bstackAccessKey);
        bstackOptions.put("projectName",   "Applitools-Android-AnalyticsX");
        bstackOptions.put("buildName",     "Applitools-Android-AnalyticsX-Build");
        bstackOptions.put("sessionName",   "Applitools-Android-AnalyticsX-Test");

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
        config.setBatch(new BatchInfo("Java BrowserStack | NML | Android AnalyticsX | Multi Device"));
        config.setUseDom(true);
        config.setSendDom(true);
        config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25(), AndroidMultiDeviceTarget.Galaxy_S25_Ultra(), AndroidMultiDeviceTarget.Pixel_9());
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "BrowserStack Android AnalyticsX App",
                    "Android AnalyticsX Validation"
            );
            System.out.println("Eyes open");

            // ── Screen 1: Login ──────────────────────────────────────────────
            eyes.check("Main Page", Target.window());
            System.out.println("Checked: Main Page");

            eyes.check("Main Page | Fully", Target.window().fully());

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
