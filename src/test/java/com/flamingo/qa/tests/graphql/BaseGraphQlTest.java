package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.graphql.client.GraphQlClient;
import com.flamingo.qa.report.AllureReportExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.qameta.allure.util.ResultsUtils.PARENT_SUITE_LABEL_NAME;

@ExtendWith(AllureReportExtension.class)
public abstract class BaseGraphQlTest {

    protected final GraphQlClient graphqlClient = new GraphQlClient();

    @BeforeEach
    void labelParentSuite() {
        Allure.label(PARENT_SUITE_LABEL_NAME, "GraphQL: Hygraph");
    }
}
