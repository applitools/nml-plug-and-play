# typescript-wdio-appium-nml-ios-browserstack
## Run

```
npm test
```



#### Upload application to Browser Stack
Endpoint: POST https://api-cloud.browserstack.com/app-automate/upload

Auth: HTTP Basic Auth — BROWSERSTACK_USERNAME : BROWSERSTACK_ACCESS_KEY

Request: multipart/form-data with a file field (the .ipa/.apk)

```
  curl example:
  curl -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" \
    -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
    -F "file=@/path/to/YourApp.ipa"
```

  Response:
  {"app_url":"bs://<app_id>"}
