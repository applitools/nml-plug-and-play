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

/**
 * iOS AnalyticsX App — Applitools NML + LambdaTest Real Device
 *
 * ENV VARS:
 *   APPLITOOLS_API_KEY   — Applitools API key
 *   LT_USERNAME          — LambdaTest username
 *   LT_ACCESS_KEY        — LambdaTest access key
 *   FLOW                 — compact | full  (default: full)
 *                          compact → Compact Dashboard → Compact Visual AI Playground
 *                          full    → Dashboard → Visual AI Playground
 *
 * APP:
 *   Application2 — AnalyticsX app (SwiftUI or UIKit real device IPA)
 *   Upload to LambdaTest and set LT_APP_ANALYTICSX to the returned lt:// URL,
 *   or replace the hardcoded value in APP_ID below.
 */
public class AnalyticsXIOSLambda_NML_Multidevice_Test {

    // ── App ID ──────────────────────────────────────────────────────────────
    // Replace with your uploaded LambdaTest app URL, e.g. lt://APP123...
    private static final String APP_ID = "lt://APP1016034271783487202563507";

    public static void main(String[] args) throws Exception {

        System.out.println("Test Started — AnalyticsX iOS");

        // ── Toggles ─────────────────────────────────────────────────────────
        String flow =
                java.util.Optional.ofNullable(System.getenv("FLOW"))
                        .orElse("full");

        System.out.println("FLOW = " + flow);

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
        ltOptions.put("build",         "Applitools-iOS-AnalyticsX-Build");
        ltOptions.put("name",          "Applitools-iOS-AnalyticsX-Test");
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
        config.addMultiDeviceTarget(IosMultiDeviceTarget.iPhone_11_Pro(), IosMultiDeviceTarget.iPhone_11_Pro_Max(), IosMultiDeviceTarget.iPhone_12());
        eyes.setConfiguration(config);


        eyes.setBatch(new BatchInfo("Java LambdaTest | Static/Slicing Dynamic | NML | iOS AnalyticsX | Multi Device"));

        try {

            eyes.open(
                    driver,
                    "LambdaTest iOS AnalyticsX App",
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
