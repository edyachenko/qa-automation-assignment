package com.flamingo.qa.graphql.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamingo.qa.graphql.dto.GraphQlRequest;
import graphql.language.AstPrinter;
import graphql.parser.InvalidSyntaxException;
import graphql.parser.Parser;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphQlAllureAttachments {

    private final ObjectMapper JSON = new ObjectMapper();

    @SneakyThrows
    public void attach(GraphQlRequest request) {
        Allure.addAttachment("GraphQL query", "text/plain", prettyQuery(request.query()), ".graphql");
        if (request.variables() != null) {
            Allure.addAttachment("GraphQL variables", "application/json",
                    JSON.writerWithDefaultPrettyPrinter().writeValueAsString(request.variables()), ".json");
        }
    }

    private String prettyQuery(String query) {
        try {
            return AstPrinter.printAst(Parser.parse(query));
        } catch (InvalidSyntaxException e) {
            return query;
        }
    }
}
