package constella.application;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** One deduplicated sparse edge and every constellation that contributed it. */
public record MemoryGraphEdge(UUID firstId, UUID secondId, Set<UUID> constellationIds) {
    public MemoryGraphEdge {
        Objects.requireNonNull(firstId, "firstId must not be null");
        Objects.requireNonNull(secondId, "secondId must not be null");
        if (firstId.equals(secondId)) {
            throw new IllegalArgumentException("an edge must connect two different memories");
        }
        constellationIds = Set.copyOf(Objects.requireNonNull(constellationIds, "constellationIds must not be null"));
    }

    public boolean touches(UUID memoryId) {
        return firstId.equals(memoryId) || secondId.equals(memoryId);
    }

    public UUID other(UUID memoryId) {
        if (firstId.equals(memoryId)) {
            return secondId;
        }
        if (secondId.equals(memoryId)) {
            return firstId;
        }
        throw new IllegalArgumentException("memory is not an endpoint of this edge");
    }
}
