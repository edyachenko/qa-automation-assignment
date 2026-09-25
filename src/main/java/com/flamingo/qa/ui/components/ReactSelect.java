package com.flamingo.qa.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactSelect {

    private final Locator root;

    public void choose(String option) {
        root.locator("input").pressSequentially(option);
        root.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(option).setExact(true)).click();
    }

    public Locator selectedValue() {
        return root.locator("[class*='singleValue']");
    }
}
