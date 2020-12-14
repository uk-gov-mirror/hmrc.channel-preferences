
# channel-preferences

This is a placeholder README.md for a new repository


## Swagger UI

To get a feel for the endpoints exposted by `releases-api`, you can use
[Swagger UI](https://swagger.io/tools/swagger-ui/).

You an access it at https://catalogue-labs.tax.service.gov.uk/swagger-ui/ :)

### Running swagger locally

To load the UI, run `./swagger-ui.sh` after you have `releases-api` running.

> You need to have docker setup for this. You can populate the version used in the UI if you run with
`sbt 'run -DAPP_VERSION=<version>'. This gets injected automatically on k8s`


Then just open the UI which will be running here http://localhost:8009! 

> The `swagger.json` is served from `releases-api` at `/swagger/swagger.json`.
 

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
