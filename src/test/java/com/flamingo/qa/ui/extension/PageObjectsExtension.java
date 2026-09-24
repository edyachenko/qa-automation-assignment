package com.flamingo.qa.ui.extension;

import com.flamingo.qa.ui.pages.BasePage;
import com.microsoft.playwright.Page;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PageObjectsExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        Page page = BrowserExtension.page(context);
        for (Object testInstance : context.getRequiredTestInstances().getAllInstances()) {
            for (Class<?> type = testInstance.getClass(); type != Object.class; type = type.getSuperclass()) {
                for (Field field : type.getDeclaredFields()) {
                    if (isPageObject(field)) {
                        inject(testInstance, field, page);
                    }
                }
            }
        }
    }

    private boolean isPageObject(Field field) {
        return BasePage.class.isAssignableFrom(field.getType())
                && !Modifier.isStatic(field.getModifiers())
                && !Modifier.isFinal(field.getModifiers());
    }

    @SneakyThrows
    private void inject(Object testInstance, Field field, Page page) {
        field.setAccessible(true);
        field.set(testInstance, field.getType().getConstructor(Page.class).newInstance(page));
    }
}
