package com.flamingo.qa.tests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@Getter
@RequiredArgsConstructor
public enum TestTag {
    //todo move somewhere else
    API(Names.API);

    private final String value;

    @UtilityClass
    public static class Names {

        public final String API = "api";
    }
}
