import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.appium.Eyes;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgrid.model.IosMultiDeviceTarget;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * iOS AnalyticsX App — Applitools NML (multi-device) + Sauce Labs Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 *   SAUCE_USERNAME       — Sauce Labs username
 *   SAUCE_ACCESS_KEY     — Sauce Labs access key
 *   SAUCE_REGION         — Sauce Labs data center region (default: us-west-1)
 *   FLOW                 — compact | full  (default: full)
 *                          compact → Compact Dashboard → Compact Visual AI Playground
 *                          full    → Dashboard → Visual AI Playground
 *
 * APP:
 *   Application2 — AnalyticsX app (SwiftUI or UIKit real device IPA)
 *   Upload the IPA to Sauce Labs Temporary Storage (Sauce Storage API) and
 *   replace the hardcoded value in APP_ID below with the returned
 *   storage:filename=... reference.
 */
public class AnalyticsXIOSSauceLabsMultidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // TODO: upload AnalyticsXUIKit.ipa to Sauce Labs storage and confirm the filename.
    private static final String APP_ID = "storage:filename=AnalyticsXUIKit.ipa";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — AnalyticsX iOS");

        // ── Toggles ─────────────────────────────────────────────────────────
        String flow =
                java.util.Optional.ofNullable(System.getenv("FLOW"))
                        .orElse("full");

        System.out.println("FLOW = " + flow);

        // ── Credentials ─────────────────────────────────────────────────────
        String apiKey          = System.getenv("APPLITOOLS_API_KEY");
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
        Eyes.setMobileCapabilities(capabilities, apiKey);

        System.out.println("Eyes.setMobileCapabilities() done");

        // ── sauce:options ───────────────────────────────────────────────────
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username",   sauceUsername);
        sauceOptions.put("accessKey",  sauceAccessKey);
        sauceOptions.put("build",      "Applitools-iOS-AnalyticsX-Build");
        sauceOptions.put("name",       "Applitools-iOS-AnalyticsX-Test");
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
        config.setUseDom(true);
        config.setSendDom(true);
        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro(), IosMultiDeviceTarget.iPhone_11_Pro_Max());
        eyes.setConfiguration(config);


        eyes.setBatch(new BatchInfo("Java Sauce Labs | iOS AnalyticsX"));

        try {

            eyes.open(
                    driver,
                    "Sauce Labs iOS AnalyticsX App",
                    "iOS AnalyticsX Validation"
            );
            System.out.println("Eyes open");

            // ── Screen 1: Login ──────────────────────────────────────────────
            eyes.check("Main Page", Target.window().fully(false));
            System.out.println("Checked: Main Page");

            driver.findElement(AppiumBy.accessibilityId("loginTitle"));
            System.out.println("Login page visible");

            Thread.sleep(2000);

            WebElement loginButton =
                    driver.findElement(AppiumBy.accessibilityId("loginButton"));

            if (!loginButton.isDisplayed()) {
                driver.executeScript(
                        "mobile: dragFromToForDuration",
                        Map.of(
                                "duration", 1,
                                "fromX", 200, "fromY", 300,
                                "toX",   200, "toY",   1400
                        )
                );
            }

            loginButton.click();
            System.out.println("Login button clicked");

            // ── Screen 2: Flow Selector ──────────────────────────────────────
            driver.findElement(AppiumBy.accessibilityId("flowSelectorTitle"));

            eyes.check("Flow Selector", Target.window().fully(false));
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
    private static void runCompactFlow(IOSDriver driver, Eyes eyes)
            throws Exception {

        System.out.println("Running COMPACT flow");

        driver.findElement(
                AppiumBy.accessibilityId("compactFlowCard")
        ).click();

        Thread.sleep(3000);

        eyes.check("Compact Dashboard", Target.window());
        System.out.println("Checked: Compact Dashboard");

        driver.findElement(
                AppiumBy.accessibilityId("compactVisualAICard")
        ).click();

        Thread.sleep(1000);

        eyes.check("Compact Visual AI Playground", Target.window());
        System.out.println("Checked: Compact Visual AI Playground");
    }

    // ── Full Flow ─────────────────────────────────────────────────────────────
    private static void runFullFlow(IOSDriver driver, Eyes eyes)
            throws Exception {

        System.out.println("Running FULL flow");

        driver.findElement(
                AppiumBy.accessibilityId("fullFlowCard")
        ).click();

        Thread.sleep(3000);

        driver.findElement(AppiumBy.accessibilityId("dashboardTitle"));

        eyes.check("Dashboard Screen", Target.window());
        System.out.println("Checked: Dashboard Screen");

        Thread.sleep(2000);

        driver.findElement(
                AppiumBy.accessibilityId("visualAIPlaygroundCard")
        ).click();

        Thread.sleep(3000);

        driver.findElement(AppiumBy.accessibilityId("playgroundTitle"));

        Thread.sleep(2000);

        eyes.check("Visual AI Playground", Target.window());
        System.out.println("Checked: Visual AI Playground");
    }
}
