package com.flamingo.qa.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import lombok.RequiredArgsConstructor;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RequiredArgsConstructor
public class ReactSelect {

    private final Locator root;

    public void choose(String option) {
        Locator input = root.locator("input");
        input.click();
        assertThat(input).isFocused();
        input.pressSequentially(option);
        assertThat(input).hasValue(option);
        root.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(option).setExact(true)).click();
    }

    public Locator selectedValue() {
        return root.locator("[class*='singleValue']");
    }

    public Locator selectedValues() {
        return root.locator("[class*='multiValue'] > div:first-child");
    }
}
