package com.kosprov.http.echo.server;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kosprov on 13/11/19.
 */
class InstanceIdFilter implements Filter {

    private String instanceId;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        instanceId = filterConfig.getInitParameter("instanceId");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (instanceId != null && !"".equals(instanceId.trim())) {
            ((HttpServletResponse) response).setHeader("X-Http-Echo-Server-Id", instanceId);
        }
    }

    @Override
    public void destroy() { /* nothing to do */ }
}
