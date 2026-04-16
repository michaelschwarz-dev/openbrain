package dev.michaelschwarz.knotenhirn.dtos;

import java.util.List;
import java.util.UUID;

public record QueryResponse(
        List<SearchResult> results
) {
    public record SearchResult(
            UUID id,
            String content
    ) {
    }
}
