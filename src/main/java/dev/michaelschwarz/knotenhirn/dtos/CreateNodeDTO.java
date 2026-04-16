package dev.michaelschwarz.knotenhirn.dtos;

import java.util.List;
import java.util.UUID;

public record CreateNodeDTO(
        String topic,
        DataType nodeType,
        String content,
        List<UUID> relatedNodes,
        List<UUID> supersededNodes
) {
}
