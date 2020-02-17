#!/usr/bin/env bash

mvn clean package

docker build --network=host --build-arg TAG=$1 -t kosprov/http-echo-server:$1 .
docker tag kosprov/http-echo-server:$1 kosprov/http-echo-server:latest
