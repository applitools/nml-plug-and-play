import { Eyes, ClassicRunner, Target, BatchInfo, Configuration } from '@applitools/eyes-webdriverio';

describe('Accessibility iOS NML - Perfecto', () => {
  let eyes, runner;

  before(async () => {
    runner = new ClassicRunner();
    eyes = new Eyes(runner);
    eyes.setLogHandler({ type: 'file', filename: './logs/eyes_perfecto.log' });
    const config = new Configuration();
    config.setUseDom(true);
    config.setSendDom(true);
     config.addMultiDeviceTarget("iPhone 15 Pro Max", "iPhone 11 Pro", "iPhone 13","iPhone 12 Pro Max");
    eyes.setConfiguration(config);

    eyes.setApiKey(process.env.APPLITOOLS_API_KEY);
    eyes.setBatch(new BatchInfo('JS Perfecto | Static/Slicing Dynamic | NML | iOS Accessibility | Multi Device'));

    await eyes.open(browser, 'Perfecto iOS Accessibility App', 'iOS Accessibility Validation');
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
