package com.flamingo.qa.config;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class Config {

    private final Properties PROPERTIES = load();

    public String baseUrl() {
        return get("base.url");
    }

    public String username() {
        return get("auth.username");
    }

    public String password() {
        return get("auth.password");
    }

    private String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing config value: " + key);
        }
        return value;
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
