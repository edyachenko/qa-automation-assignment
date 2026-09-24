package com.flamingo.qa.dto.graphql;

public record ProductsQueryVariables(int first, int skip) {

    public ProductsQueryVariables(int first) {
        this(first, 0);
    }
}
