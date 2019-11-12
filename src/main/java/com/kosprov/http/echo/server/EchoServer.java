package com.kosprov.http.echo.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kosprov on 11/11/19.
 */
public class EchoServer {

    private static final Logger log = LoggerFactory.getLogger(EchoServer.class);

    public static void main(String... args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "3000"));

        log.info("Starting server on port {}", port);

        Server server = new Server();

        ServerConnector http = new ServerConnector(server);
        http.setPort(port);
        http.setName("http");
        server.addConnector(http);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        ServletHolder echoServletHolder = new ServletHolder(new EchoServlet());
        handler.addServletWithMapping(echoServletHolder, "/*");

        server.start();
    }
}