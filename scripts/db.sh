#!/usr/bin/env bash

docker rm --force -v kotlinServerDB
docker run -d -p 27017:27017 --name kotlinServerDB mongo