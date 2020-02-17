# Welcome to the `http-echo-server` repository

`http-echo-server` is a tiny HTTP server based on GraalVM native-image and Jetty.

It is meant as a testing utility, for example when you test proxies or load balancers.

Keep in mind that whatever is echoed is after Jetty has parsed the request. There might be slight differences from the 
echoed content to whatever was received on the wire (for example, the order of headers).

If you want a TCP echo server, you can use [`watson/http-echo-server`](https://github.com/watson/http-echo-server). The 
downside is the 2 seconds delay before sending the response.

## How to use it

The easiest way is to use the pre-built Docker image:

    docker run --rm -it -p 3000:3000 kosprov/http-echo-server
    
and then:

    curl -vs http://localhost:3000/foo
    
To enable TLS, use:

    docker run --rm -it -e TLS_PORT=3001 -p 3001:3001 kosprov/http-echo-server
    
This will use an internal certificate. If you want a different one, you can use variables `KEYSTORE`, `KEYSTORE_PASS` 
and `KEY_PASS`. For example:

    docker run --rm -it -e TLS_PORT=3001 -p 3001:3001 
        -v $(pwd)/keystore.p12:/tmp/keystore.p12 \
        -e KEYSTORE=/tmp/keystore.p12 \
        -e KEYSTORE_PASS=keystore-pass \
        -e KEY_PASS=key-pass \
        kosprov/http-echo-server
