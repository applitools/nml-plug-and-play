import { Eyes, ClassicRunner, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility iOS NML - Multi Device', () => {
  let eyes: Eyes;
  let runner: ClassicRunner;

  before(async () => {
    runner = new ClassicRunner();
    eyes = new Eyes(runner);
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_saucelabs.log' });

    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    config.addMultiDeviceTarget('iPhone 11 Pro Max', 'iPhone 13');
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY as string);
    eyes.setBatch(new BatchInfo('TS SauceLabs | NML | iOS Accessibility | Multi Device'));

    await eyes.open(browser, 'SauceLabs iOS Accessibility App', 'iOS Accessibility Validation');
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
