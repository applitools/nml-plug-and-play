import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its path/specs from that
const APPS = {
  analyticsx: {
    appPath: process.env.ANALYTICSX_APP_PATH,
    appPackage: 'com.apexlytics.analyticsxandroid',
    appActivity: 'com.apexlytics.analyticsxandroid.LoginActivity',
    specs: ['./test/specs/android/analyticsX.android.test.js'],
  },
  accessibility: {
    appPath: process.env.ACCESSIBILITY_APP_PATH,
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

// Step 1: Eyes.setMobileCapabilities injects appium:optionalIntentArguments (Android NML config)
const caps = Eyes.setMobileCapabilities({
  platformName: 'Android',
  'appium:app': activeApp.appPath,
  'appium:deviceName': process.env.AVD_NAME || 'Pixel_8_API_35',
  'appium:platformVersion': process.env.PLATFORM_VERSION || '15.0',
  'appium:automationName': 'UiAutomator2',
  'appium:appPackage': activeApp.appPackage,
  'appium:appActivity': activeApp.appActivity,
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY);

// Step 2: appium:optionalIntentArguments stays a plain Appium cap for a local Appium server
// (no vendor nesting needed for a local run).
// appium:processArguments is iOS-only, so drop it for an Android run.
delete caps['appium:processArguments'];

export const config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: '127.0.0.1',
  port: 4723,
  path: '/',

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
};
