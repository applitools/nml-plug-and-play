import 'dotenv/config';
import { ConfigurationPlain, Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/package/activity/specs from that
const APPS: Record<string, { appUrl?: string; appPackage?: string; appActivity?: string; specs: string[] }> = {
  analyticsx: {
    appUrl: process.env.SAUCE_APP_ANALYTICSX,
    appPackage: process.env.ANALYTICSX_APP_PACKAGE,
    appActivity: process.env.ANALYTICSX_APP_ACTIVITY,
    specs: ['./test/specs/android/analyticsX.android.test.ts'],
  },
  accessibility: {
    appUrl: process.env.SAUCE_APP_ACCESSIBILITY,
    appPackage: process.env.ACCESSIBILITY_APP_PACKAGE,
    appActivity: process.env.ACCESSIBILITY_APP_ACTIVITY,
    specs: ['./test/specs/android/accessibility.android.test.ts'],
  },
};

const activeAppName = process.env.ACTIVE_APP?.trim().toLowerCase();
const activeApp = activeAppName ? APPS[activeAppName] : undefined;
if (!activeApp) {
  throw new Error(
    `ACTIVE_APP "${process.env.ACTIVE_APP}" must be one of: ${Object.keys(APPS).join(', ')}`
  );
}

// Step 1: Eyes.setMobileCapabilities injects appium:optionalIntentArguments (Android NML config)
const caps = Eyes.setMobileCapabilities<Record<string, unknown>>({
  platformName: 'Android',
  'appium:app': activeApp.appUrl,
  'appium:deviceName': process.env.DEVICE_NAME || 'Samsung Galaxy S23',
  'appium:platformVersion': process.env.PLATFORM_VERSION || '13',
  'appium:automationName': 'UiAutomator2',
  'appium:appPackage': activeApp.appPackage,
  'appium:appActivity': activeApp.appActivity,
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY as ConfigurationPlain);

// Step 2: appium:optionalIntentArguments stays as a plain Appium capability on Sauce Labs
// (unlike LambdaTest, Sauce Labs does not require it to be nested under its vendor options).
// appium:processArguments is iOS-only, so drop it for an Android run.
delete caps['appium:processArguments'];

// Step 3: Attach sauce:options
// appiumVersion is required for Android 14+ real devices, which Sauce only accepts over Appium 2 / W3C
caps['sauce:options'] = {
  username: process.env.SAUCE_USERNAME,
  accessKey: process.env.SAUCE_ACCESS_KEY,
  build: 'Applitools-Android-EONNEXT-Build',
  name: 'Applitools-Android-EONNEXT-Test',
  appiumVersion: 'latest',
};

const region = process.env.SAUCE_REGION || 'us-west-1';

export const config: WebdriverIO.Config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: `ondemand.${region}.saucelabs.com`,
  port: 443,
  protocol: 'https',
  path: '/wd/hub',

  capabilities: [caps as WebdriverIO.Capabilities],

  logLevel: 'info',

  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,

  services: [],

  framework: 'mocha',

  mochaOpts: {
    timeout: 300000,
  },

  reporters: ['spec'],

  afterTest: async function (_test, _context, { error }: { error?: Error }) {
    await browser.execute(`sauce:job-result=${!error}`);
  },
};
