import { Eyes, Target, BatchInfo } from '@applitools/eyes-webdriverio';

const FLOW = process.env.FLOW ?? 'full';

describe('AnalyticsX iOS NML - Local', () => {
  let eyes: Eyes;

  before(async () => {
    eyes = new Eyes();
    eyes.setApiKey(process.env.APPLITOOLS_API_KEY as string);
    eyes.setBatch(new BatchInfo('TS Local | NML | iOS AnalyticsX'));
    await eyes.open(browser, 'Local iOS AnalyticsX App', 'iOS AnalyticsX Validation');
    console.log('Eyes open — FLOW =', FLOW);
  });

  after(async () => {
    await eyes.abortIfNotClosed();
  });

  it('validates the AnalyticsX app flow', async () => {

    // ── Screen 1: Login ────────────────────────────────────────────────────
    await eyes.check('Main Page', Target.window());
    console.log('Checked: Main Page');

    await eyes.check('Main Page | Fully', Target.window().fully());

    await browser.$('~loginTitle').waitForExist({ timeout: 10000 });
    console.log('Login page visible');

    await browser.pause(2000);

    const loginButton = await browser.$('~loginButton');
    if (!(await loginButton.isDisplayed())) {
      await browser.execute('mobile: dragFromToForDuration', {
        duration: 1, fromX: 200, fromY: 300, toX: 200, toY: 1400,
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

  async function runCompactFlow(): Promise<void> {
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

  async function runFullFlow(): Promise<void> {
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
