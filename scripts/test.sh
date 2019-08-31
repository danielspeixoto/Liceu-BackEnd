#!/usr/bin/env bash

docker rm --force -v deffishdb
docker run -d -p 27017:27017 --name deffishdb mongo

export MONGO_CONNECTION=mongodb://localhost:27017
export MONGO_DB_NAME=test

./gradlew test