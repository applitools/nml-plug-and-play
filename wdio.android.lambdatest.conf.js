import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/package/activity/specs from that
const APPS = {
  analyticsx: {
    appUrl: process.env.LT_APP_ANALYTICSX,
    appPackage: process.env.ANALYTICSX_APP_PACKAGE,
    appActivity: process.env.ANALYTICSX_APP_ACTIVITY,
    specs: ['./test/specs/android/analyticsX.android.test.js'],
  },
  accessibility: {
    appUrl: process.env.LT_APP_ACCESSIBILITY,
    appPackage: process.env.ACCESSIBILITY_APP_PACKAGE,
    appActivity: process.env.ACCESSIBILITY_APP_ACTIVITY,
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
  'appium:automationName': 'UiAutomator2',
  'appium:appPackage': activeApp.appPackage,
  'appium:appActivity': activeApp.appActivity,
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY);

// Build lt:options
const ltOptions = {
  user: process.env.LT_USERNAME,
  accessKey: process.env.LT_ACCESS_KEY,
  build: 'Applitools-Android-Infosys-Build',
  name: 'Applitools-Android-Infosys-Test',
  'appium:deviceName': process.env.DEVICE_NAME ,
  'appium:platformVersion': process.env.PLATFORM_VERSION,
  isRealMobile: true,
  devicelog: true,
  visual: true,
  network: true,
  w3c: true,
};

//Move optionalIntentArguments into lt:options; remove iOS cap (not needed for Android)
const optionalIntentArguments = caps['appium:optionalIntentArguments'];
if (optionalIntentArguments != null) {
  ltOptions.optionalIntentArguments = optionalIntentArguments;
}

// Attach lt:options to caps
caps['lt:options'] = ltOptions;

export const config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: 'mobile-hub.lambdatest.com',
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
    await browser.execute(`lambda-status=${status}`);
  },
};
