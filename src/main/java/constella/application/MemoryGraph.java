package constella.application;

import constella.model.Memory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Immutable UI-independent graph content and deterministic 3D coordinates. */
public record MemoryGraph(Map<UUID, Memory> memories, Map<UUID, Vector3> positions, List<MemoryGraphEdge> edges) {
    public MemoryGraph {
        memories = Map.copyOf(Objects.requireNonNull(memories, "memories must not be null"));
        positions = Map.copyOf(Objects.requireNonNull(positions, "positions must not be null"));
        edges = List.copyOf(Objects.requireNonNull(edges, "edges must not be null"));
        if (!positions.keySet().equals(memories.keySet())) {
            throw new IllegalArgumentException("every graph memory must have exactly one position");
        }
    }

    public Set<UUID> neighbors(UUID memoryId) {
        return edges.stream().filter(edge -> edge.touches(memoryId)).map(edge -> edge.other(memoryId))
                .collect(Collectors.toUnmodifiableSet());
    }
}
