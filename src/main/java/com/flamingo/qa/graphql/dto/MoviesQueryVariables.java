package com.flamingo.qa.graphql.dto;

public record MoviesQueryVariables(int first, int skip) {

    public MoviesQueryVariables(int first) {
        this(first, 0);
    }

    @Override
    public String toString() {
        return "first=%d, skip=%d".formatted(first, skip);
    }
}
