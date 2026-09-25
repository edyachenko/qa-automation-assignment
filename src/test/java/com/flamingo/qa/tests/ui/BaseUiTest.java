package com.flamingo.qa.tests.ui;

import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.report.AllureReportExtension;
import com.flamingo.qa.ui.extension.BrowserExtension;
import com.flamingo.qa.ui.extension.PageObjectsExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.qameta.allure.util.ResultsUtils.PARENT_SUITE_LABEL_NAME;

@Tag(TestTag.UI)
@ExtendWith({AllureReportExtension.class, BrowserExtension.class, PageObjectsExtension.class})
public abstract class BaseUiTest {

    @BeforeEach
    void labelParentSuite() {
        Allure.label(PARENT_SUITE_LABEL_NAME, "UI: DemoQA");
    }
}
