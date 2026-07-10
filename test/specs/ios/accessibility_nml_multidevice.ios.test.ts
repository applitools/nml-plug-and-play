import { Eyes, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility iOS NML - LambdaTest', () => {
  let eyes: Eyes;

  before(async () => {
    eyes = new Eyes();
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_lambdatest.log' });
    
    const config = new Configuration();
    config.addMultiDeviceTarget('iPhone 15 Pro Max', 'iPhone 11 Pro', 'iPhone 13',"iPhone 13 Pro Max");
    config.setUseDom(true);
    config.setSendDom(true);
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY as string);
    eyes.setBatch(new BatchInfo('TS LambdaTest | Static/Slicing Dynamic | NML | iOS Accessibility | Multi Device'));

    await eyes.open(browser, 'LambdaTest iOS Accessibility App', 'iOS Accessibility Validation');
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
