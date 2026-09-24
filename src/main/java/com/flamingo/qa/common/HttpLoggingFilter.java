package com.flamingo.qa.common;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpLoggingFilter implements Filter {

    private final boolean logBodies;

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext context) {
        long startedAt = System.currentTimeMillis();
        Response response = context.next(request, responseSpecification);
        log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getURI(), response.getStatusCode(),
                System.currentTimeMillis() - startedAt);
        if (logBodies && log.isDebugEnabled()) {
            if (request.getBody() != null) {
                log.debug("Request body: {}", request.<Object>getBody());
            }
            log.debug("Response body: {}", response.asString());
        }
        return response;
    }
}
