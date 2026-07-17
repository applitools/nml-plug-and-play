import { Eyes, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility iOS NML - Saucelabs', () => {
  let eyes;

  before(async () => {
    eyes = new Eyes();
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_saucelabs.log' });
    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    config.addMultiDeviceTarget("iPhone 15 Pro Max", "iPhone 11 Pro", "iPhone 13");
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY);
    eyes.setBatch(new BatchInfo('JS Saucelabs | NML - Static / Slicing Dynamic | iOS Accessibility | NML MUlti Device'));

    await eyes.open(browser, 'Saucelabs iOS Accessibility App', 'iOS Accessibility Validation');
    console.log('Eyes open');
  });

  after(async () => {
    await eyes.abortIfNotClosed();
  });

  it('validates the Accessibility app main screen', async () => {
    await eyes.check('Main Screen', Target.window());
    console.log('Checked: Main Screen');

    await eyes.check('Main Screen | Fully', Target.window().fully());

    await eyes.close(false);
    console.log('Eyes closed');
  });
});
