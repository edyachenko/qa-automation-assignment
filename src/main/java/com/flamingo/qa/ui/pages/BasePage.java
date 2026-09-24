package com.flamingo.qa.ui.pages;

public abstract class BasePage<P extends BasePage<P>> {

    public P open() {
        throw new UnsupportedOperationException();
    }
}
