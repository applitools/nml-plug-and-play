import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS = {
  analyticsx: {
    appUrl: process.env.BROWSERSTACK_APP_ANALYTICSX,
    appPackage: 'com.apexlytics.analyticsxandroid',
    appActivity: 'com.apexlytics.analyticsxandroid.LoginActivity',
    specs: ['./test/specs/android/analyticsX.android.test.js'],
  },
  accessibility: {
    appUrl: process.env.BROWSERSTACK_APP_ACCESSIBILITY,
    appPackage: 'com.applitools.accessibilitytest',
    appActivity: 'com.applitools.accessibilitytest.MainActivity',
    specs: ['./test/specs/android/accessibility.android.test.js'],
  },
};

const activeAppName = process.env.ACTIVE_APP?.trim().toLowerCase();
const activeApp = APPS[activeAppName];
if (!activeApp) {
  throw new Error(
    `ACTIVE_APP "${process.env.ACTIVE_APP}" must be one of: ${Object.keys(APPS).join(', ')}`
  );
}

// Eyes.setMobileCapabilities injects appium:optionalIntentArguments (Android NML config)
const caps = Eyes.setMobileCapabilities({
  platformName: 'Android',
  'appium:app': activeApp.appUrl,
  'appium:appPackage': activeApp.appPackage,
  'appium:appActivity': activeApp.appActivity,
  'appium:deviceName': process.env.DEVICE_NAME || 'Samsung Galaxy S23',
  'appium:platformVersion': process.env.PLATFORM_VERSION || '13',
  'appium:automationName': 'UiAutomator2',
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY);


// Attach bstack:options
// NOTE: BrowserStack requires pre-uploading the .apk via their REST API; you cannot
// point appium:app at a local file path. BROWSERSTACK_APP_ANALYTICSX/ACCESSIBILITY must
// each be a bs://<app_id> value.
caps['bstack:options'] = {
  userName: process.env.BROWSERSTACK_USERNAME,
  accessKey: process.env.BROWSERSTACK_ACCESS_KEY,
  projectName: 'Applitools-NML',
  buildName: 'Applitools-Android-NML-Build',
  sessionName: 'Applitools-Android-NML-Test',
};

export const config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: 'hub-cloud.browserstack.com',
  port: 443,
  protocol: 'https',
  path: '/wd/hub',

  capabilities: [caps],

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

  afterTest: async function (test, context, { error }) {
    const status = error ? 'failed' : 'passed';
    await browser.execute(
      `browserstack_executor: {"action": "setSessionStatus", "arguments": {"status":"${status}"}}`
    );
  },
};
