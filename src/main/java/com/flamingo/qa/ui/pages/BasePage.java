package com.flamingo.qa.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BasePage<P extends BasePage<P>> {

    protected final Page page;
    private final String path;

    protected abstract Locator readyMarker();

    public P open() {
        Allure.step("Open %s (%s)".formatted(getClass().getSimpleName(), path), () -> {
            page.navigate(path, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            assertThat(readyMarker()).isVisible();
        });
        return self();
    }

    @SuppressWarnings("unchecked")
    protected P self() {
        return (P) this;
    }
}
