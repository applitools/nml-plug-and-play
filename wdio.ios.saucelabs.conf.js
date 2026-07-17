import 'dotenv/config';
import { Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS = {
  analyticsx: {
    appUrl: process.env.SAUCE_APP_ANALYTICSX,
    specs: ['./test/specs/ios/analyticsX.ios.test.js'],
  },
  accessibility: {
    appUrl: process.env.SAUCE_APP_ACCESSIBILITY,
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
  'appium:deviceName': process.env.DEVICE_NAME,
  'appium:platformVersion': process.env.PLATFORM_VERSION,
  'appium:automationName': 'XCUITest',
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY);


// Attach sauce:options
// appiumVersion is required for iOS 17+ real devices, which Sauce only accepts over Appium 2 / W3C
caps['sauce:options'] = {
  username: process.env.SAUCE_USERNAME,
  accessKey: process.env.SAUCE_ACCESS_KEY,
  build: 'Demo-Applitools-iOS-Build',
  name: 'Demo-Applitools-iOS-Test',
  appiumVersion: 'latest',
};

const region = process.env.SAUCE_REGION || 'us-west-1';

export const config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: `ondemand.${region}.saucelabs.com`,
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
    await browser.execute(`sauce:job-result=${!error}`);
  },
};
