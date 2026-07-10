import 'dotenv/config';
import { ConfigurationPlain, Eyes } from '@applitools/eyes-webdriverio';

// Resolve which app is under test from ACTIVE_APP, and derive its url/specs from that
const APPS: Record<string, { appUrl?: string; specs: string[] }> = {
  analyticsx: {
    appUrl: process.env.BROWSERSTACK_APP_ANALYTICSX,
    specs: ['./test/specs/ios/analyticsX.ios.test.ts'],
  },
  accessibility: {
    appUrl: process.env.BROWSERSTACK_APP_ACCESSIBILITY,
    specs: ['./test/specs/ios/accessibility.ios.test.ts'],
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

// Step 2: appium:processArguments stays as a plain Appium capability on BrowserStack
// (no vendor nesting needed on BrowserStack — the capability stays top-level).
// appium:optionalIntentArguments is Android-only, so drop it for an iOS run.
delete caps['appium:optionalIntentArguments'];

// Step 3: Attach bstack:options
// NOTE: BrowserStack requires pre-uploading the .ipa via their REST API; you cannot
// point appium:app at a local file path. BROWSERSTACK_APP_ANALYTICSX/ACCESSIBILITY must
// each be a bs://<app_id> value.
const bstackOptions: Record<string, unknown> = {
  userName: process.env.BROWSERSTACK_USERNAME,
  accessKey: process.env.BROWSERSTACK_ACCESS_KEY,
  projectName: 'Applitools-NML',
  buildName: 'Applitools-iOS-NML-Build',
  sessionName: 'Applitools-iOS-NML-Test',
};
caps['bstack:options'] = bstackOptions;

export const config: WebdriverIO.Config = {

  // Auto-selected based on ACTIVE_APP — see the APPS lookup above
  specs: activeApp.specs,

  maxInstances: 1,

  hostname: 'hub-cloud.browserstack.com',
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
    const status = error ? 'failed' : 'passed';
    await browser.execute(
      `browserstack_executor: {"action": "setSessionStatus", "arguments": {"status":"${status}"}}`
    );
  },
};
