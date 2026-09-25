package com.flamingo.qa.report;

import com.flamingo.qa.config.Config;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.qameta.allure.util.ResultsUtils.PARENT_SUITE_LABEL_NAME;

public class AllureReportExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final AtomicBoolean ENVIRONMENT_WRITTEN = new AtomicBoolean(false);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (ENVIRONMENT_WRITTEN.compareAndSet(false, true)) {
            writeEnvironment();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getRequiredTestInstances().getAllInstances().stream()
                .map(instance -> AnnotationSupport.findAnnotation(instance.getClass(), ParentSuite.class))
                .flatMap(Optional::stream)
                .findFirst()
                .ifPresent(parentSuite -> Allure.label(PARENT_SUITE_LABEL_NAME, parentSuite.value()));
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
        environment.setProperty("REST URL", Config.baseUrl());
        environment.setProperty("GraphQL URL", Config.graphqlUrl());
        environment.setProperty("UI URL", Config.uiUrl());
        environment.setProperty("Services", "Restful Booker, Hygraph, DemoQA");
        environment.setProperty("Environment", Config.env());
        environment.setProperty("User", Config.usernameIfSet().orElse("not set"));
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
