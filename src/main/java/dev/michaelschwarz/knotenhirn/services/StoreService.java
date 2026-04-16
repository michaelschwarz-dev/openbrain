package dev.michaelschwarz.knotenhirn.services;

import dev.michaelschwarz.knotenhirn.dtos.CreateNodeDTO;
import dev.michaelschwarz.knotenhirn.repository.Neo4jClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class StoreService {

    @Inject
    Neo4jClient neo4jClient;

    public List<UUID> storeNodes(List<CreateNodeDTO> nodes) {
        return nodes.stream()
                .map(this::createNode)
                .toList();
    }

    private UUID createNode(CreateNodeDTO node) {
        var newNodeId = UUID.randomUUID();

        neo4jClient.runQuery("""
                            MERGE (t:Topic {name: $topicName})
                            CREATE (t)<-[:BELONGS_TO]-(f {content: $content, id: $newNodeId})
                            SET f:$($nodeType)
                            RETURN f.id
                        """
                , Map.of(
                        "nodeType", node.nodeType().label,
                        "topicName", node.topic(),
                        "content", node.content(),
                        "newNodeId", newNodeId.toString()
                ));

        if (node.relatedNodes() != null) {
            node.relatedNodes().forEach(nodeId -> {
                neo4jClient.runQuery("""
                                MATCH (n {id: $newNodeId})
                                MATCH (r {id: $nodeId})
                                MERGE (n)-[:RELATED_TO]->(r)
                                """
                        , Map.of(
                                "newNodeId", newNodeId.toString(),
                                "nodeId", nodeId.toString()
                        ));
            });
        }

        return newNodeId;
    }
}
