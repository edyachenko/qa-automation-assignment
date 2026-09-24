package com.flamingo.qa.ui.extension;

import com.flamingo.qa.config.Config;
import com.flamingo.qa.ui.browser.AdBlocker;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class BrowserExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(BrowserExtension.class);
    private static final int ACTION_TIMEOUT_MS = 15_000;
    private static final int ASSERTION_TIMEOUT_MS = 10_000;

    public static Page page(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(Page.class, Page.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        BrowserContext browserContext = threadBrowser(context).newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.uiUrl())
                .setViewportSize(1920, 1080));
        browserContext.setDefaultTimeout(ACTION_TIMEOUT_MS);
        AdBlocker.install(browserContext);
        context.getStore(NAMESPACE).put(Page.class, browserContext.newPage());
        log.info("Opened a fresh browser context for {}", context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        page(context).context().close();
    }

    private Browser threadBrowser(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE)
                .getOrComputeIfAbsent(Thread.currentThread().getId(), id -> new ThreadBrowser(), ThreadBrowser.class)
                .browser;
    }

    private static final class ThreadBrowser implements ExtensionContext.Store.CloseableResource {

        private final Playwright playwright = Playwright.create();
        private final Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(Config.uiHeadless()));

        private ThreadBrowser() {
            PlaywrightAssertions.setDefaultAssertionTimeout(ASSERTION_TIMEOUT_MS);
            log.info("Launched Chromium {} for thread {}", browser.version(), Thread.currentThread().getName());
        }

        @Override
        public void close() {
            browser.close();
            playwright.close();
        }
    }
}
