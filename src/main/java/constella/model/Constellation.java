package constella.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** An immutable named grouping of memory identities. */
public final class Constellation {
    private final UUID id;
    private final String name;
    private final String description;
    private final Set<UUID> memoryIds;

    public Constellation(UUID id, String name, String description, Collection<UUID> memoryIds) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = normalizeRequired(name, "name");
        this.description = normalizeOptional(description);
        Objects.requireNonNull(memoryIds, "memoryIds must not be null");
        LinkedHashSet<UUID> copiedIds = new LinkedHashSet<>();
        for (UUID memoryId : memoryIds) {
            copiedIds.add(Objects.requireNonNull(memoryId, "memoryId must not be null"));
        }
        this.memoryIds = Set.copyOf(copiedIds);
    }

    public static Constellation create(String name, String description) {
        return new Constellation(UUID.randomUUID(), name, description, Set.of());
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Set<UUID> memoryIds() {
        return memoryIds;
    }

    public Constellation renamed(String newName) {
        return new Constellation(id, newName, description, memoryIds);
    }

    public Constellation withMembership(UUID memoryId, boolean included) {
        Objects.requireNonNull(memoryId, "memoryId must not be null");
        LinkedHashSet<UUID> updated = new LinkedHashSet<>(memoryIds);
        if (included) {
            updated.add(memoryId);
        } else {
            updated.remove(memoryId);
        }
        return new Constellation(id, name, description, updated);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Constellation constellation && id.equals(constellation.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static String normalizeRequired(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        String normalized = normalizeWhitespace(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = normalizeWhitespace(value);
        return normalized.isEmpty() ? null : normalized;
    }

    private static String normalizeWhitespace(String value) {
        return value.strip().replaceAll("\\s+", " ");
    }
}
