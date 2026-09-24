package com.flamingo.qa.api;

import com.flamingo.qa.assertions.AuthResponseAssert;
import com.flamingo.qa.config.Config;
import com.flamingo.qa.dto.request.AuthRequest;

import static org.apache.http.HttpStatus.SC_OK;

public class AuthClient extends ApiClient {

    private static String adminToken;

    public AuthResponseAssert createToken(AuthRequest request) {
        return new AuthResponseAssert(request().body(request).post(Endpoints.AUTH));
    }

    public synchronized String adminToken() {
        if (adminToken == null) {
            adminToken = createToken(AuthRequest.builder()
                            .username(Config.username())
                            .password(Config.password())
                            .build())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveToken()
                    .token();
        }
        return adminToken;
    }
}
