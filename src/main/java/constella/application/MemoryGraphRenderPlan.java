package constella.application;

import constella.model.Memory;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Exact UI-independent geometry inputs for rendering one node per graph memory and no extra edges. */
public record MemoryGraphRenderPlan(List<Memory> nodes, List<MemoryGraphEdge> edges) {
    public MemoryGraphRenderPlan {
        nodes = List.copyOf(Objects.requireNonNull(nodes, "nodes must not be null"));
        edges = List.copyOf(Objects.requireNonNull(edges, "edges must not be null"));
        Set<UUID> ids = nodes.stream().map(Memory::id).collect(Collectors.toUnmodifiableSet());
        if (ids.size() != nodes.size()) {
            throw new IllegalArgumentException("render nodes must have unique memory identities");
        }
        if (edges.stream().anyMatch(edge -> !ids.contains(edge.firstId()) || !ids.contains(edge.secondId()))) {
            throw new IllegalArgumentException("every rendered edge endpoint must be a rendered memory");
        }
    }

    public static MemoryGraphRenderPlan from(MemoryGraph graph) {
        Objects.requireNonNull(graph, "graph must not be null");
        List<Memory> nodes = graph.memories().values().stream()
                .sorted(Comparator.comparing(memory -> memory.id().toString())).toList();
        return new MemoryGraphRenderPlan(nodes, graph.edges());
    }
}
