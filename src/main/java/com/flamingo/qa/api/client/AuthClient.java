package com.flamingo.qa.api.client;

import com.flamingo.qa.common.ApiClient;
import com.flamingo.qa.config.Config;
import com.flamingo.qa.api.assertions.AuthResponseAssert;
import com.flamingo.qa.api.dto.request.AuthRequest;
import io.qameta.allure.Step;

public class AuthClient extends ApiClient {

    public AuthClient() {
        super(Config.baseUrl(), false);
    }

    @Step("Create auth token")
    public AuthResponseAssert createToken(AuthRequest request) {
        return new AuthResponseAssert(request().body(request).post(Endpoints.AUTH));
    }
}
