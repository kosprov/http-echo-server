#!/bin/sh

set -e

if [ "x$1" == "x-h" ] || [ "x$1" == "x--help" ]; then
  cat << EOF
http-echo-server version: ${TAG}.

Usage:
docker run --rm -it -e PORT=3000 -e INSTANCE_ID=my-server -p 3000:3000 kosprov/http-echo-server:${TAG}
EOF
  exit 0;
fi

/echo-server -Djava.library.path=/opt/lib -Djavax.net.ssl.trustStore=/opt/cacerts &
pid=$!
trap "kill -INT ${pid}" TERM INT

while [ -e "/proc/${pid}" ]; do
  sleep 1
done