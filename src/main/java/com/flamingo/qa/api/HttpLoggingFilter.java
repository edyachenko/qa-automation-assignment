package com.flamingo.qa.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpLoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext context) {
        long startedAt = System.currentTimeMillis();
        Response response = context.next(request, responseSpecification);
        log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getURI(), response.getStatusCode(),
                System.currentTimeMillis() - startedAt);
        if (log.isDebugEnabled() && !request.getURI().endsWith(Endpoints.AUTH)) {
            if (request.getBody() != null) {
                log.debug("Request body: {}", request.<Object>getBody());
            }
            log.debug("Response body: {}", response.asString());
        }
        return response;
    }
}
