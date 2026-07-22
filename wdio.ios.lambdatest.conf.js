import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS = {
  analyticsx: {
    appUrl: process.env.LT_APP_ANALYTICSX,
    specs: ['./test/specs/ios/analyticsX.ios.test.js'],
  },
  accessibility: {
    appUrl: process.env.LT_APP_ACCESSIBILITY,
    specs: ['./test/specs/ios/accessibility.ios.test.js'],
  },
};

const activeAppName = process.env.ACTIVE_APP?.trim().toLowerCase();
const activeApp = APPS[activeAppName];
if (!activeApp) {
  throw new Error(
    `ACTIVE_APP "${process.env.ACTIVE_APP}" must be one of: ${Object.keys(APPS).join(', ')}`
  );
}

// Eyes.setMobileCapabilities injects appium:processArguments (iOS NML config)
const caps = Eyes.setMobileCapabilities({
  platformName: 'iOS',
  'appium:app': activeApp.appUrl,
  'appium:automationName': 'XCUITest',
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY);

// Build lt:options
const ltOptions = {
  user: process.env.LT_USERNAME,
  accessKey: process.env.LT_ACCESS_KEY,
  build: 'Applitools-iOS-Infosys-Build',
  name: 'Applitools-iOS-Infosys-Test',
  'appium:deviceName': process.env.DEVICE_NAME,
  'appium:platformVersion': process.env.PLATFORM_VERSION,
  isRealMobile: true,
  devicelog: true,
  visual: true,
  network: true,
  w3c: true,
};

// Move processArguments into lt:options; remove Android cap (not needed for iOS)
const processArguments = caps['appium:processArguments'];
if (processArguments != null) {
  ltOptions.processArguments = processArguments;
  delete caps['appium:processArguments'];
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
