package com.flamingo.qa.report;

import com.flamingo.qa.config.Config;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllureReportExtension implements BeforeAllCallback, AfterEachCallback {

    private static final AtomicBoolean ENVIRONMENT_WRITTEN = new AtomicBoolean(false);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (ENVIRONMENT_WRITTEN.compareAndSet(false, true)) {
            writeEnvironment();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        String log = AllureLogAppender.drain();
        if (!log.isBlank()) {
            Allure.addAttachment("Test log", "text/plain", log, ".log");
        }
    }

    private void writeEnvironment() {
        Properties environment = new Properties();
        environment.setProperty("Base URL", Config.baseUrl());
        environment.setProperty("Service", "Restful Booker");
        environment.setProperty("Environment", System.getProperty("env", "public"));
        environment.setProperty("User", Config.username());
        Path directory = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        try {
            Files.createDirectories(directory);
            try (var out = Files.newOutputStream(directory.resolve("environment.properties"))) {
                environment.store(out, "Allure environment");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write Allure environment.properties", e);
        }
    }
}
