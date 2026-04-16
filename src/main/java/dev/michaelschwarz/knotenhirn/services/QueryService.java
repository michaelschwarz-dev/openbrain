package dev.michaelschwarz.knotenhirn.services;

import dev.michaelschwarz.knotenhirn.repository.Neo4jClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class QueryService {

    @Inject
    Neo4jClient neo4jClient;

    public String search(String query) {
        try {
            return neo4jClient.runQuery(query, Map.of()).stream().map(String::valueOf).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
