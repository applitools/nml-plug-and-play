import { Eyes, Target, BatchInfo } from '@applitools/eyes-webdriverio';

const FLOW = process.env.FLOW ?? 'full';

describe('AnalyticsX Android NML - SauceLabs', () => {
  let eyes;

  before(async () => {
    eyes = new Eyes();
     eyes.setLogHandler({ type: 'file', filename: './logs/eyes_saucelabs.log' });

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY);
    eyes.setBatch(new BatchInfo('JS SauceLabs | NML | Android AnalyticsX'));
    await eyes.open(browser, 'SauceLabs Android AnalyticsX App', 'Android AnalyticsX Validation');
    console.log('Eyes open — FLOW =', FLOW);
  });

  after(async () => {
    await eyes.abortIfNotClosed();
  });

  it('validates the AnalyticsX app flow', async () => {

    // ── Screen 1: Login ────────────────────────────────────────────────────
    await eyes.check('Main Page', Target.window());
    console.log('Checked: Main Page');

    await browser.$('~loginTitle').waitForExist({ timeout: 10000 });
    console.log('Login page visible');

    await browser.pause(2000);

    const loginButton = await browser.$('~loginButton');
    if (!(await loginButton.isDisplayed())) {
      await browser.execute('mobile: scrollGesture', {
        left: 100, top: 300, width: 200, height: 600,
        direction: 'down', percent: 0.75,
      });
    }
    await loginButton.click();
    console.log('Login button clicked');

    // ── Screen 2: Flow Selector ────────────────────────────────────────────
    await browser.$('~flowSelectorTitle').waitForExist({ timeout: 10000 });
    await eyes.check('Flow Selector', Target.window().fully());
    console.log('Checked: Flow Selector');

    // ── Flow Branch ────────────────────────────────────────────────────────
    if (FLOW === 'compact') {
      await runCompactFlow();
    } else {
      await runFullFlow();
    }

    await eyes.close(false);
    console.log('Eyes closed');
  });

  async function runCompactFlow() {
    console.log('Running COMPACT flow');

    await (await browser.$('~compactFlowCard')).click();
    await browser.pause(3000);

    await eyes.check('Compact Dashboard', Target.window().fully());
    console.log('Checked: Compact Dashboard');

    await (await browser.$('~compactVisualAICard')).click();
    await browser.pause(1000);

    await eyes.check('Compact Visual AI Playground', Target.window().fully());
    console.log('Checked: Compact Visual AI Playground');
  }

  async function runFullFlow() {
    console.log('Running FULL flow');

    await (await browser.$('~fullFlowCard')).click();
    await browser.pause(3000);

    await browser.$('~dashboardTitle').waitForExist({ timeout: 10000 });
    await eyes.check('Dashboard Screen', Target.window().fully());
    console.log('Checked: Dashboard Screen');

    await browser.pause(2000);
    await (await browser.$('~visualAIPlaygroundCard')).click();
    await browser.pause(3000);

    await browser.$('~playgroundTitle').waitForExist({ timeout: 10000 });
    await browser.pause(2000);

    await eyes.check('Visual AI Playground', Target.window().fully());
    console.log('Checked: Visual AI Playground');
  }
});
