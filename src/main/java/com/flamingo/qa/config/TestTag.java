package com.flamingo.qa.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@Getter
@RequiredArgsConstructor
public enum TestTag {

    API(Names.API),
    GRAPHQL(Names.GRAPHQL),
    UI(Names.UI);

    private final String value;

    @UtilityClass
    public static class Names {

        public final String API = "api";
        public final String GRAPHQL = "graphql";
        public final String UI = "ui";
    }
}
