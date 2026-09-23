package com.flamingo.qa.assertions;

import io.restassured.response.Response;

public class StatusResponseAssert extends ResponseAssert<StatusResponseAssert> {

    public StatusResponseAssert(Response response) {
        super(response);
    }
}
