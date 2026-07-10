import 'dotenv/config';
import { ConfigurationPlain, Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS: Record<string, { appUrl?: string; appPackage?: string; appActivity?: string; specs: string[] }> = {
  analyticsx: {
    appUrl: process.env.PERFECTO_APP_ANALYTICSX,
    appPackage: process.env.ANALYTICSX_APP_PACKAGE,
    appActivity: process.env.ANALYTICSX_APP_ACTIVITY,
    specs: ['./test/specs/android/analyticsX_nml_multidevice.android.test.ts'],
  },
  accessibility: {
    appUrl: process.env.PERFECTO_APP_ACCESSIBILITY,
    appPackage: process.env.ACCESSIBILITY_APP_PACKAGE,
    appActivity: process.env.ACCESSIBILITY_APP_ACTIVITY,
    specs: ['./test/specs/android/accessibility_nml_multidevice.android.test.ts'],
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

// Step 2: appium:optionalIntentArguments stays as a plain Appium capability on Perfecto
// (no vendor nesting needed on Perfecto — the capability stays top-level).
// appium:processArguments is iOS-only, so drop it for an Android run.
delete caps['appium:processArguments'];

// Step 3: Attach perfecto:options
caps['perfecto:options'] = {
  securityToken: process.env.PERFECTO_SECURITY_TOKEN,
};

const cloudName = process.env.PERFECTO_CLOUD_NAME;

export const config: WebdriverIO.Config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: `${cloudName}.perfectomobile.com`,
  port: 443,
  protocol: 'https',
  path: '/nexperience/perfectomobile/wd/hub',

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
    const status = error ? 'FAILED' : 'PASSED';
    await browser.execute(`perfectomobile:status=${status}`);
  },
};
