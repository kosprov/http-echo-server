package com.kosprov.http.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * @author kosprov on 12/11/19.
 */
public class EchoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(EchoServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Received request: {} {} {}", req.getMethod(), req.getPathInfo(), req.getProtocol());

        // set response to application/octet-stream as body may contain binary data
        resp.setContentType("application/octet-stream");

        ServletOutputStream out = resp.getOutputStream();

        // wrap output stream to send request line and headers
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

        // send request line
        writer.write(req.getMethod() + " " + req.getPathInfo() + " " + req.getProtocol() + '\n');

        // send headers
        Enumeration<String> allHeaders = req.getHeaderNames();
        while (allHeaders.hasMoreElements()) {
            String multiHeaderName = allHeaders.nextElement();
            Enumeration<String> multiHeaderValues = req.getHeaders(multiHeaderName);
            while (multiHeaderValues.hasMoreElements()) {
                String multiHeaderValue = multiHeaderValues.nextElement();
                writer.write(multiHeaderName + ": " + multiHeaderValue + '\n');
            }
        }

        // prepare for sending the body
        writer.write('\n');
        writer.flush();

        // copy the request body from the input socket to the output socket
        ServletInputStream in = req.getInputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }
}
