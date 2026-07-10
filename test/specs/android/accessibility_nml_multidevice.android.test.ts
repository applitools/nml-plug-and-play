import { Eyes, Target, BatchInfo, Configuration, AndroidMultiDeviceTarget } from '@applitools/eyes-webdriverio';

describe('Accessibility Android NML - Multi Device', () => {
  let eyes: Eyes;

  before(async () => {
    eyes = new Eyes();
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_lambdatest.log' });

    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25);
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY as string);
    eyes.setBatch(new BatchInfo('TS LambdaTest | NML | Android Accessibility | Multi Device'));

    await driver.pause(5000);

    await eyes.open(browser, 'LambdaTest Android Accessibility App', 'Android Accessibility Validation');
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
