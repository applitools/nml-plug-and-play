import { Eyes, Target, BatchInfo, Configuration, AndroidMultiDeviceTarget } from '@applitools/eyes-webdriverio';

describe('Accessibility Android NML - Perfecto', () => {
  let eyes;

  before(async () => {
    eyes = new Eyes();
      eyes.setLogHandler({ type: 'file', filename: './logs/eyes_perfecto.log' });

    const config = new Configuration();
    config.setShowLogs(true);
    config.setLogHandler({ type: 'file', filename: './logs/eyes_perfecto.log' });
    config.setUseDom(true);
    config.setSendDom(true);
    config.addMultiDeviceTarget("Galaxy S25","Galaxy S25 Ultra","Pixel 9");
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY);
    eyes.setBatch(new BatchInfo('JS Perfecto | NML | Accessibility | Multi Device'));

    await driver.pause(5000);

    await eyes.open(browser, 'Perfecto Android Accessibility App', 'Android Accessibility Validation');
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
