/*
 * Copyright 2026 Bren.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.api.resources.services.graphql;

import java.util.List;
import java.util.Map;


/**
 * Helper class to build GraphQL queries
 * 
 * @author Bren
 */
public class GraphqlQueryBuilder {
    
    
    /**
     * Build GraphQL query
     * 
     * @param operationName
     * @param queryName
     * @param variables
     * @param returnFields
     * 
     * @return String
     */
    public static String buildQuery(
        String operationName,
        String queryName,
        Map<String, Object> variables,
        List<String> returnFields
    ) {

        String variableSection = populateConstraints(variables);
        String returnSection = populateReturnFields(returnFields);

        String query = """
        query %s {
            %s(query: {%s
            }) {%s
            }
        }
        """.formatted(
                operationName,
                queryName,
                variableSection,
                returnSection
        );

        String escaped = query
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", " ")
        .replace("\t", " ");

        return """
        {
            "query": "%s"
        }
        """.formatted(escaped);
    }


    /**
     * Populate constraint component
     * 
     * @param fieldVars
     * 
     * @return String
     */
    public static String populateConstraints(Map<String, Object> fieldVars) {

        // Add each field and value pair to string
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : fieldVars.entrySet()) {

            // Fetch key-value
            String key = entry.getKey();
            Object value = entry.getValue();

            // Define indented key
            stringBuilder.append("\n\t\t")
              .append(key)
              .append(": ");

            // Handle value
            if (value instanceof String) {
                stringBuilder.append("\"").append(value).append("\"");
            } else {
                stringBuilder.append(value);
            }
        }

        // Build and return string
        return stringBuilder.toString();
    }


    /**
     * Populate the return fields component
     * 
     * @param fields
     * 
     * @return String
     */
    public static String populateReturnFields(List<String> fields) {

        // Apply fields to return
        StringBuilder stringBuilder = new StringBuilder();
        for (String field : fields) {
            stringBuilder.append("\n\t\t")
              .append(field);
        }

        // Build and return fields
        return stringBuilder.toString();
    }



    
}