<link href="https://raw.github.com/clownfart/Markdown-CSS/master/markdown.css" rel="stylesheet"></link>
# CloudFoundry User Account and Authentication (UAA) Server

This is a copy of the [UAA component](https://github.com/cloudfoundry/uaa), forked in support of the Cloud Foundry Certified Developer Training Course.

The full README for UAA is still available with the original code.

## Deploying UAA

1. Clone this repository
2. Come up with a unique name for your UAA app
3. Identify the domain of your CF (based on the routes of the apps created so far)
4. Run the following from the root of the UAA clone:

```
./gradlew manifests -Dapp=$YOUR_UNIQUE_APP_NAME -Dapp-domain=$DOMAIN
cf push -f build/sample-manifests/uaa-cf-application.yml
```
