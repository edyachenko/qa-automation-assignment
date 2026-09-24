package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.graphql.client.GraphQLClient;
import com.flamingo.qa.report.AllureReportExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureReportExtension.class)
public abstract class BaseGraphql {

    protected final GraphQLClient graphqlClient = new GraphQLClient();
}
