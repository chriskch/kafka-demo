package org.demo;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Forces the presence of CORS headers so the browser demo can talk to the service reliably.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final String FRONTEND_ORIGIN = "http://localhost:8080";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", FRONTEND_ORIGIN);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
    }
}
