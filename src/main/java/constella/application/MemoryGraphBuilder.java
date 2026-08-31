package constella.application;

import constella.model.Constellation;
import constella.model.Memory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Builds sparse chronological constellation paths and merges overlapping edges. */
public final class MemoryGraphBuilder {
    private MemoryGraphBuilder() {
    }

    public static MemoryGraph build(List<Memory> visibleMemories, List<Constellation> constellations) {
        Map<UUID, Memory> memories = visibleMemories.stream()
                .collect(Collectors.toMap(Memory::id, memory -> memory, (first, ignored) -> first, LinkedHashMap::new));
        Comparator<UUID> chronological = Comparator.comparing((UUID id) -> memories.get(id).occurredOn())
                .thenComparing(UUID::toString);
        Map<EdgeKey, LinkedHashSet<UUID>> memberships = new LinkedHashMap<>();
        for (Constellation constellation : constellations) {
            List<UUID> ordered = constellation.memoryIds().stream().filter(memories::containsKey)
                    .sorted(chronological).toList();
            for (int index = 1; index < ordered.size(); index++) {
                EdgeKey key = EdgeKey.of(ordered.get(index - 1), ordered.get(index));
                memberships.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(constellation.id());
            }
        }
        List<MemoryGraphEdge> edges = new ArrayList<>();
        memberships.forEach((key, ids) -> edges.add(new MemoryGraphEdge(key.first, key.second, Set.copyOf(ids))));
        assert edges.stream().allMatch(edge -> memories.containsKey(edge.firstId())
                && memories.containsKey(edge.secondId()));
        return new MemoryGraph(memories, Memory3DLayout.layout(List.copyOf(memories.values())), edges);
    }

    private record EdgeKey(UUID first, UUID second) {
        private static EdgeKey of(UUID first, UUID second) {
            return first.toString().compareTo(second.toString()) <= 0
                    ? new EdgeKey(first, second) : new EdgeKey(second, first);
        }
    }
}
