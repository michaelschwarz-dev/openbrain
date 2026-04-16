package dev.michaelschwarz.knotenhirn.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class Neo4jClient {

    @Inject
    Driver driver;

    public List<Record> runQuery(String cypherQuery, Map<String, Object> parameters) {
        try (var session = driver.session()) {
            return session.run(cypherQuery, parameters).list();
        }
    }
}
