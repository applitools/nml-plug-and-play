import { Eyes, ClassicRunner, Target, BatchInfo, Configuration, AndroidMultiDeviceTarget } from '@applitools/eyes-webdriverio';

describe('Accessibility Android NML - LambdaTest', () => {
  let eyes,runner, config;
  before(async () => {
    
    runner = new ClassicRunner();
    eyes = new Eyes(runner);
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_lambdatest.log' });

    config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    config.setApiKey(process.env.APPLITOOLS_API_KEY);
    config.setBatch(new BatchInfo('JS LambdaTest | NML | Android Accessibility | Multi Device'));
    config.addMultiDeviceTarget(AndroidMultiDeviceTarget.Galaxy_S25);
    eyes.setConfiguration(config);

    await eyes.open(browser, 'LambdaTest Android Accessibility App', 'Android Accessibility Validation');
    console.log('Eyes open');
    await driver.pause(5000);
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
