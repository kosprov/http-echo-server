package com.kosprov.http.echo.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import java.util.UUID;

/**
 * @author kosprov on 11/11/19.
 */
public class EchoServer {

    private static final Logger log = LoggerFactory.getLogger(EchoServer.class);

    public static void main(String... args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "3000"));
        int tlsPort = Integer.parseInt(System.getenv().getOrDefault("TLS_PORT", "-1"));
        String instanceId = System.getenv().getOrDefault("INSTANCE_ID", UUID.randomUUID().toString());

        String keystore = System.getenv().getOrDefault("KEYSTORE", "/keystore.p12");
        String keystorePass = System.getenv().getOrDefault("KEYSTORE_PASS", "EchoServer");
        String keyPass = System.getenv().getOrDefault("KEY_PASS", "EchoServer");

        log.info("Starting server on port {}", port);

        Server server = new Server();

        ServerConnector http = new ServerConnector(server);
        http.setPort(port);
        http.setName("http");
        server.addConnector(http);

        if (tlsPort != -1) {
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());
            SslContextFactory sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(keystore);
            sslContextFactory.setKeyStorePassword(keystorePass);
            sslContextFactory.setKeyManagerPassword(keyPass);
            ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
            sslConnector.setPort(tlsPort);
            server.addConnector(sslConnector);
        }

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        ServletHolder echoServletHolder = new ServletHolder(new EchoServlet());
        handler.addServletWithMapping(echoServletHolder, "/*");

        Filter instanceIdFilter = new InstanceIdFilter();
        FilterHolder filterHolder = new FilterHolder();
        filterHolder.setName(instanceIdFilter.getClass().getSimpleName());
        filterHolder.setFilter(instanceIdFilter);
        filterHolder.setInitParameter("instanceId", instanceId);

        FilterMapping filterMapping = new FilterMapping();
        filterMapping.setPathSpec("/*");
        filterMapping.setFilterName(filterHolder.getName());
        handler.addFilter(filterHolder, filterMapping);

        server.start();
    }
}
