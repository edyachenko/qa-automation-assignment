package com.flamingo.qa.extension;

import com.flamingo.qa.config.Config;
import com.flamingo.qa.dto.request.AuthRequest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class AuthDataExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
        return parameter.getParameter().getType() == AuthRequest.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
        return AuthRequest.builder()
                .username(Config.username())
                .password(Config.password())
                .build();
    }
}
