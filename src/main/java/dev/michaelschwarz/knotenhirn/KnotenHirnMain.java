package dev.michaelschwarz.knotenhirn;

import dev.michaelschwarz.knotenhirn.dtos.CreateNodeDTO;
import dev.michaelschwarz.knotenhirn.services.QueryService;
import dev.michaelschwarz.knotenhirn.services.StoreService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Slf4j
public class KnotenHirnMain {

    @Inject
    StoreService storeService;

    @Inject
    QueryService queryService;

    @Tool(name = "storeRecords", title = "Stores records", description = """
            Use this tool to store data or records. Before saving any data, use the queryMemory tool to load related or relevant data.
            Each record to be created consists of a topic it belongs to and the data itself. Additionally, every record must have a type.
            
            Record Types:
            - FACT: Use this when the data is indisputable. (e.g., quotes, log files, file snippets, etc.)
            - PRINCIPLE: Use this for storing guidelines, specifications, rules, instructions, recipes, etc.
            - EXPERIENCE: Use this when you have resolved a difficult problem. Store the solution to the problem here.
            
            Every record can be linked to other data points.
            """)
    List<String> storeData(List<CreateNodeDTO> nodes) {
        return storeService.storeNodes(nodes).stream().map(UUID::toString).toList();
    }

    @Tool(name = "queryMemory", description = """
            CRITICAL: Call this tool FIRST before answering any user query if you lack context about their project,\s
            architecture, preferences, past decisions, or general knowledge. This searches the knowledge graph by executing the given cypher query.\s
            NOTE: Pay close attention to the Node IDs returned in the results; you will need to provide these IDs to the store tools if you ever need\s
            to update or supersede that specific information.
            """)
    String queryMemory(@ToolArg(name = "query", description = "The cypher query") String query) {
        return queryService.search(query);
    }
}