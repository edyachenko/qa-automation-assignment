package com.flamingo.qa.api.assertions;

import com.flamingo.qa.common.ResponseAssert;
import io.restassured.response.Response;

public class StatusResponseAssert extends ResponseAssert<StatusResponseAssert> {

    public StatusResponseAssert(Response response) {
        super(response);
    }
}
