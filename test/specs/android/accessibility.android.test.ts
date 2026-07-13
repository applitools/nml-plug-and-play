import { Eyes, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility Android NML - Local', () => {
  let eyes: Eyes;

  before(async () => {
    eyes = new Eyes();

    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY as string);
    eyes.setBatch(new BatchInfo('TS Local | Slicing Instrument | Android Accessibility'));

    await eyes.open(browser, 'Local Android Accessibility App', 'Android Accessibility Validation');
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
