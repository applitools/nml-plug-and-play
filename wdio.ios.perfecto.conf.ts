import 'dotenv/config';
import { ConfigurationPlain, Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS: Record<string, { appUrl?: string; specs: string[] }> = {
  analyticsx: {
    appUrl: process.env.PERFECTO_APP_ANALYTICSX,
    specs: ['./test/specs/ios/analyticsX_nml_multidevice.ios.test.ts'],
  },
  accessibility: {
    appUrl: process.env.PERFECTO_APP_ACCESSIBILITY,
    // no multi-device variant exists for iOS accessibility yet — falls back to the plain spec
    specs: ['./test/specs/ios/accessibility_nml_multidevice.ios.test.ts'],
  },
};

const activeAppName = process.env.ACTIVE_APP?.trim().toLowerCase();
const activeApp = activeAppName ? APPS[activeAppName] : undefined;
if (!activeApp) {
  throw new Error(
    `ACTIVE_APP "${process.env.ACTIVE_APP}" must be one of: ${Object.keys(APPS).join(', ')}`
  );
}

// Step 1: Eyes.setMobileCapabilities injects appium:processArguments (iOS NML config)
const caps = Eyes.setMobileCapabilities<Record<string, unknown>>({
  platformName: 'iOS',
  'appium:app': activeApp.appUrl,
  'appium:deviceName': process.env.DEVICE_NAME || 'iPhone 14',
  'appium:platformVersion': process.env.PLATFORM_VERSION || '17',
  'appium:automationName': 'XCUITest',
  'appium:noReset': false,
  'appium:newCommandTimeout': 300,
}, process.env.APPLITOOLS_API_KEY as ConfigurationPlain);

// Step 2: appium:processArguments stays as a plain Appium capability on Perfecto
// (no vendor nesting needed on Perfecto — the capability stays top-level).
// appium:optionalIntentArguments (Android-only) is left as-is too — Eyes.setMobileCapabilities() sets both unconditionally.

// Step 3: Attach perfecto:options
const perfectoOptions: Record<string, unknown> = {
  securityToken: process.env.PERFECTO_SECURITY_TOKEN,
};
caps['perfecto:options'] = perfectoOptions;

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
