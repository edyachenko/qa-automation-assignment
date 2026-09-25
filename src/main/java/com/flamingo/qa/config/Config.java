package com.flamingo.qa.config;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@UtilityClass
public class Config {

    private final Properties PROPERTIES = load();

    public String env() {
        return find("env").orElse("prod");
    }

    public String baseUrl() {
        return get("base.url");
    }

    public String graphqlUrl() {
        return get("graphql.url");
    }

    public String uiUrl() {
        return get("ui.url");
    }

    public boolean uiHeadless() {
        return Boolean.parseBoolean(find("ui.headless").orElse("true"));
    }

    public String username() {
        return get("auth.username");
    }

    public Optional<String> usernameIfSet() {
        return find("auth.username");
    }

    public String password() {
        return get("auth.password");
    }

    private String get(String key) {
        return find(key).orElseThrow(() -> new IllegalStateException("Missing config value: " + key));
    }

    private Optional<String> find(String key) {
        return nonBlank(System.getProperty(key)).or(() -> nonBlank(PROPERTIES.getProperty(key)));
    }

    private Optional<String> nonBlank(String value) {
        return Optional.ofNullable(value).filter(v -> !v.isBlank());
    }

    @SneakyThrows
    private Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the classpath");
            }
            properties.load(in);
        }
        return properties;
    }
}
