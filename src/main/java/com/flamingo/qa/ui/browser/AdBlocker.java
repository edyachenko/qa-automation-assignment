package com.flamingo.qa.ui.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Route;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class AdBlocker {

    private final List<String> AD_HOSTS = List.of(
            "googlesyndication", "doubleclick", "googletagmanager", "googletagservices",
            "google-analytics", "adtrafficquality", "adsrvr.org", "criteo", "adform", "openx", "id5-sync");

    public void install(BrowserContext context) {
        context.route(AdBlocker::isAd, Route::abort);
    }

    private boolean isAd(String url) {
        String host = URI.create(url).getHost();
        return host != null && AD_HOSTS.stream().anyMatch(host.toLowerCase(Locale.ROOT)::contains);
    }
}
