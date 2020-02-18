#!/bin/sh

set -e

if [ "x$1" == "x-h" ] || [ "x$1" == "x--help" ]; then
  cat << EOF
http-echo-server version: ${TAG}.

Usage:
docker run --rm -it -e PORT=3000 -e TLS_PORT=3001 -e INSTANCE_ID=my-server -p 3000:3000 -p 3001:3001 kosprov/http-echo-server:${TAG}
EOF
  exit 0;
fi

if [ "x$TLS_PORT" != "x" ]; then
  domain_name=${DOMAIN_NAME:-localhost}
  password=EchoServer

  sed -i "s/HOST_PLACEHOLDER/${domain_name}/g" echo-server-csr.json
  cfssl genkey echo-server-csr.json | cfssljson -bare echo-server
  cfssl sign -ca=CA.pem -ca-key=CA-key.pem -config=ca-config.json -hostname=${domain_name} echo-server.csr | cfssljson -bare echo-server
  openssl pkcs12 -export -passout pass:${password} -in echo-server.pem -inkey echo-server-key.pem -out keystore.p12 -name echo-server -CAfile CA.pem -caname CA -chain
fi

/echo-server -Djava.library.path=/opt/lib -Djavax.net.ssl.trustStore=/opt/cacerts &
pid=$!
trap "kill -INT ${pid}" TERM INT

while [ -e "/proc/${pid}" ]; do
  sleep 1
done
