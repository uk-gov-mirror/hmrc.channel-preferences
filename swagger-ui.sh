#!/usr/bin/env bash

echo "Swagger-ui started at http://localhost:8009"
docker run -p 8009:8080 \
    -e URLS="[
    {url: 'http://localhost:8008/releases-api/swagger/swagger.json', name: 'releases-api'},
    {url: 'https://petstore.swagger.io/v2/swagger.json', name: 'petstore'}
    ]" \
    -e API_URL=https://catalogue-labs.tax.service.gov.uk/releases-api/swagger/swagger.json \
    -e VALIDATOR_URL=null \
    -e DISPLAY_REQUEST_DURATION=true \
    --name swagger-ui \
    --rm \
    swaggerapi/swagger-ui
