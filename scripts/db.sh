#!/usr/bin/env bash

docker rm --force -v deffishdb
docker run -d -p 27017:27017 --name deffishdb mongo