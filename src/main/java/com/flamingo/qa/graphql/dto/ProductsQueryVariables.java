package com.flamingo.qa.graphql.dto;

public record ProductsQueryVariables(int first, int skip) {

    public ProductsQueryVariables(int first) {
        this(first, 0);
    }
}
