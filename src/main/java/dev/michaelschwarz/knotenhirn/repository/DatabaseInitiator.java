package dev.michaelschwarz.knotenhirn.repository;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class DatabaseInitiator {

    @Inject
    Neo4jClient neo4jClient;

    @Startup
    public void createIndexes() {
        neo4jClient.runQuery("CREATE INDEX IF NOT EXISTS FOR (t:Topic) ON (t.name)", Map.of());
        neo4jClient.runQuery("CREATE INDEX IF NOT EXISTS FOR (t:Topic) ON (t.id)", Map.of());

        neo4jClient.runQuery("CREATE INDEX IF NOT EXISTS FOR (p:Principle) ON (p.id)", Map.of());
        neo4jClient.runQuery("CREATE INDEX IF NOT EXISTS FOR (e:Experience) ON (e.id)", Map.of());
        neo4jClient.runQuery("CREATE INDEX IF NOT EXISTS FOR (f:Fact) ON (f.id)", Map.of());

        neo4jClient.runQuery("CREATE FULLTEXT INDEX content IF NOT EXISTS FOR (n:Principle|Experience|Fact) ON EACH [n.content]", Map.of());
        neo4jClient.runQuery("CREATE FULLTEXT INDEX topic IF NOT EXISTS FOR (n:Topic) ON EACH [n.name]", Map.of());
    }
}
