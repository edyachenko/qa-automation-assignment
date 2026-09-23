package com.flamingo.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final Properties PROPERTIES = load();

    private Config() {
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String username() {
        return get("auth.username");
    }

    public static String password() {
        return get("auth.password");
    }

    private static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null) {
            throw new IllegalStateException("Missing config value: " + key);
        }
        return value;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return properties;
    }
}
