package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.graphql.client.GraphQlClient;
import com.flamingo.qa.report.ParentSuite;

@ParentSuite("GraphQL: Hygraph")
public abstract class BaseGraphQlTest {

    protected final GraphQlClient graphqlClient = new GraphQlClient();
}
