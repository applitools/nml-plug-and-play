import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Toggle: IOS_TARGET=real runs against a physically connected iPhone (uses the
// real-device .ipa; Appium auto-detects the UDID — set IOS_UDID to target a
// specific device). Anything else (default) targets a named Simulator via
// DEVICE_NAME/PLATFORM_VERSION (uses the Simulator-built .app).
const iosTarget = (process.env.IOS_TARGET || 'simulator').trim().toLowerCase();

// Resolve which app is under test from ACTIVE_APP, and derive its path/specs from that
const APPS = {
  analyticsx: {
    appPath: iosTarget === 'real' ? process.env.ANALYTICSX_APP_PATH : process.env.ANALYTICSX_APP_PATH_SIMULATOR,
    specs: ['./test/specs/ios/analyticsX_nml_multidevice.ios.test.js'],
  },
  accessibility: {
    appPath: iosTarget === 'real' ? process.env.ACCESSIBILITY_APP_PATH : process.env.ACCESSIBILITY_APP_PATH_SIMULATOR,
    specs: ['./test/specs/ios/accessibility_nml_multidevice.ios.test.js'],
  },
};

const activeAppName = process.env.ACTIVE_APP?.trim().toLowerCase();
const activeApp = APPS[activeAppName];
if (!activeApp) {
  throw new Error(
    `ACTIVE_APP "${process.env.ACTIVE_APP}" must be one of: ${Object.keys(APPS).join(', ')}`
  );
}

const baseCaps = {
  platformName: 'iOS',
  'appium:app': activeApp.appPath,
  'appium:automationName': 'XCUITest',
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
};

if (iosTarget === 'real') {
  if (process.env.IOS_UDID) baseCaps['appium:udid'] = process.env.IOS_UDID;
} else {
  baseCaps['appium:deviceName'] = process.env.DEVICE_NAME || 'iPhone 14';
  baseCaps['appium:platformVersion'] = process.env.PLATFORM_VERSION || '17';
}

// Step 1: Eyes.setMobileCapabilities injects appium:processArguments (iOS NML config)
const caps = Eyes.setMobileCapabilities(baseCaps, process.env.APPLITOOLS_API_KEY);

// Step 2: appium:processArguments stays a plain Appium cap for a local Appium server
// (no vendor nesting needed for a local run).
// appium:optionalIntentArguments is Android-only, so drop it for an iOS run.

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
