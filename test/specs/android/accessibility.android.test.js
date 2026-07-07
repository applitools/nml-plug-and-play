import { Eyes, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility Android NML - LambdaTest', () => {
  let eyes;

  before(async () => {
    eyes = new Eyes();
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_accessibility_lambdatest.log' });
    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY);
    eyes.setBatch(new BatchInfo('JS LambdaTest | Slicing Instrument | Android Accessibility'));

    await eyes.open(browser, 'LambdaTest Android Accessibility App', 'Android Accessibility Validation');
    console.log('Eyes open');
  });

  after(async () => {
    await eyes.abortIfNotClosed();
  });

  it('validates the Accessibility app main screen', async () => {
    await eyes.check('Main Screen', Target.window().fully());
    console.log('Checked: Main Screen');

    await eyes.close(false);
    console.log('Eyes closed');
  });
});
