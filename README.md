# Channel preferencs microservice
Micro-service responsible for providing an API to CDS services.

# API

- Link to OpenApi definitions: [schema](https://github.com/hmrc/channel-preferences/blob/public/schema.json)

## Run the project locally

Ensure you have service-manager python environment setup:

`source ../servicemanager/bin/activate`

`sm --start DC_TWSM_ALL`

`sm --stop CHANNEL_PREFERENCES`

`sbt "run 9052 -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"`

## Run the tests and sbt fmt before raising a PR

Ensure you have service-manager python environment setup:

`source ../servicemanager/bin/activate`

Format:

`sbt fmt`

Then run the tests and coverage report:

`sbt clean coverage test coverageReport`

If your build fails due to poor test coverage, *DO NOT* lower the test coverage threshold, instead inspect the generated report located here on your local repo: `/target/scala-2.12/scoverage-report/index.html`

Then run the integration tests:

`sbt it:test`

## Swagger endpoint

Available locally here: http://localhost:9052/assets/schema.json

# License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
