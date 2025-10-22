package org.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.Objects;

/**
 * Forces the presence of CORS headers so the browser demo can talk to the service reliably.
 */
@Provider
@ApplicationScoped
public class CorsFilter implements ContainerResponseFilter {

    private final String frontendOrigin;

    @Inject
    public CorsFilter(@ConfigProperty(name = "demo.frontend.origin", defaultValue = "http://localhost:8080") String frontendOrigin) {
        this.frontendOrigin = Objects.requireNonNull(frontendOrigin, "demo.frontend.origin must not be null");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", frontendOrigin);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(Response.Status.NO_CONTENT.getStatusCode());
        }
    }
}
