import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;

import com.applitools.eyes.config.Configuration;
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
 * Android AnalyticsX App — Applitools NML + Sauce Labs Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY     — Applitools API key
 *   SAUCE_USERNAME         — Sauce Labs username
 *   SAUCE_ACCESS_KEY       — Sauce Labs access key
 *   SAUCE_REGION           — Sauce Labs data center region (default: us-west-1)
 *   FLOW                   — compact | full  (default: full)
 *                            compact → Compact Dashboard → Compact Visual AI Playground
 *                            full    → Dashboard → Visual AI Playground
 *
 * APP:
 *   AnalyticsX Android APK — upload to Sauce Labs app storage and paste the returned
 *   storage:filename=... value into APP_ID below (placeholder — not yet uploaded).
 *   Also update APP_PACKAGE and APP_ACTIVITY to match the app's manifest.
 */
public class AnalyticsXAndroidSauceLabsTest {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Placeholder — upload the AnalyticsX APK to Sauce Labs app storage and replace.
    private static final String APP_ID = "storage:filename=AnalyticsX_XMLLayout.apk";

    // ── Update to match the AnalyticsX APK manifest ─────────────────────────
    private static final String APP_PACKAGE  = "com.apexlytics.analyticsxandroid";
    private static final String APP_ACTIVITY = "com.apexlytics.analyticsxandroid.LoginActivity";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — AnalyticsX Android");

        // ── Toggles ─────────────────────────────────────────────────────────
        String flow = Optional.ofNullable(System.getenv("FLOW")).orElse("full");
        System.out.println("FLOW = " + flow);

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey           = System.getenv("APPLITOOLS_API_KEY");
        String serverUrl        = System.getenv("APPLITOOLS_SERVER_URL"); // optional; defaults to Applitools public cloud if unset
        String sauceUsername    = System.getenv("SAUCE_USERNAME");
        String sauceAccessKey   = System.getenv("SAUCE_ACCESS_KEY");
        String sauceRegion      = Optional.ofNullable(System.getenv("SAUCE_REGION")).orElse("us-west-1");

        // ── Capabilities ────────────────────────────────────────────────────
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName",              "Android");
        capabilities.setCapability("appium:platformVersion",    "16");
        capabilities.setCapability("appium:deviceName",         "Samsung Galaxy S22");
        capabilities.setCapability("appium:automationName",     "UiAutomator2");
        capabilities.setCapability("appium:newCommandTimeout",  "300");
        capabilities.setCapability("appium:noReset",            false);
        capabilities.setCapability("appium:app",                APP_ID);
        capabilities.setCapability("appium:appPackage",         APP_PACKAGE);
        capabilities.setCapability("appium:appActivity",        APP_ACTIVITY);

        System.out.println("Capabilities set");

        // ── NML ─────────────────────────────────────────────────────────────
        // Eyes.setMobileCapabilities injects BOTH optionalIntentArguments (Android)
        // AND processArguments (iOS), unconditionally, regardless of platform. On
        // Sauce Labs neither needs vendor-options nesting (unlike LambdaTest), so
        // both stay as plain top-level Appium capabilities, left as-is.
        Eyes.setMobileCapabilities(capabilities, apiKey, serverUrl);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── Attach sauce:options ──────────────────────────────────────────────
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username",   sauceUsername);
        sauceOptions.put("accessKey",  sauceAccessKey);
        sauceOptions.put("build",      "Applitools-Android-AnalyticsX-Build");
        sauceOptions.put("name",       "Applitools-Android-AnalyticsX-Test");
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
        config.setBatch(new BatchInfo("Java SauceLabs | NML | Android AnalyticsX"));
        config.setUseDom(true);
        config.setSendDom(true);
        eyes.setConfiguration(config);

        try {

            eyes.open(
                    driver,
                    "Sauce Labs Android AnalyticsX App",
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
