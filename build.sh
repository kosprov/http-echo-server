#!/usr/bin/env bash

mvn clean package

keytool -genkey -alias localhost \
    -keystore keystore.p12 -storetype pkcs12 -keypass EchoServer -storepass EchoServer \
    -keyalg RSA -keysize 2048 -validity 825 \
    -dname "cn=localhost, ou=tools, o=kosprov.com, c=US"
docker build --network=host --build-arg TAG=$1 -t kosprov/http-echo-server:$1 .
docker tag kosprov/http-echo-server:$1 kosprov/http-echo-server:latest
