package com.flamingo.qa.tests.ui;

import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.report.ParentSuite;
import com.flamingo.qa.ui.extension.BrowserExtension;
import com.flamingo.qa.ui.extension.PageObjectsExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag(TestTag.UI)
@ParentSuite("UI: DemoQA")
@ExtendWith({BrowserExtension.class, PageObjectsExtension.class})
public abstract class BaseUiTest {
}
