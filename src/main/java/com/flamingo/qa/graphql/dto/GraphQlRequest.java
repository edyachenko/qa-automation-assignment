package com.flamingo.qa.graphql.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GraphQlRequest(String query, Object variables) {

    public static GraphQlRequest of(String query) {
        return new GraphQlRequest(query, null);
    }
}
