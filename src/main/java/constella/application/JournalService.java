package constella.application;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.StarPosition;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** UI-independent journal use cases backed by an in-memory working state. */
public final class JournalService {
    private final Map<UUID, Memory> memories = new LinkedHashMap<>();
    private final Map<UUID, Constellation> constellations = new LinkedHashMap<>();
    private final Map<UUID, StarPosition> starPositions = new LinkedHashMap<>();

    public JournalService() {
        this(JournalSnapshot.empty());
    }

    public JournalService(JournalSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot must not be null");
        for (Memory memory : snapshot.memories()) {
            if (memories.put(memory.id(), memory) != null) {
                throw new IllegalArgumentException("duplicate memory id: " + memory.id());
            }
        }
        for (Constellation constellation : snapshot.constellations()) {
            validateUniqueConstellationName(constellation.name(), null);
            Set<UUID> staleIds = constellation.memoryIds().stream()
                    .filter(id -> !memories.containsKey(id))
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
            Constellation cleaned = constellation;
            for (UUID staleId : staleIds) {
                cleaned = cleaned.withMembership(staleId, false);
            }
            constellations.put(cleaned.id(), cleaned);
        }
        for (Memory memory : memories.values()) {
            starPositions.put(memory.id(), snapshot.starPositions().getOrDefault(memory.id(), positionFor(memory.id())));
        }
    }

    public void addMemory(Memory memory) {
        Objects.requireNonNull(memory, "memory must not be null");
        if (memories.putIfAbsent(memory.id(), memory) != null) {
            throw new IllegalArgumentException("memory already exists: " + memory.id());
        }
        starPositions.put(memory.id(), positionFor(memory.id()));
    }

    public void updateMemory(Memory memory) {
        Objects.requireNonNull(memory, "memory must not be null");
        requireMemory(memory.id());
        memories.put(memory.id(), memory);
    }

    public Memory memory(UUID id) {
        return requireMemory(id);
    }

    public List<Memory> memories() {
        return List.copyOf(memories.values());
    }

    public void deleteMemory(UUID id) {
        requireMemory(id);
        memories.remove(id);
        starPositions.remove(id);
        constellations.replaceAll((constellationId, constellation) -> constellation.withMembership(id, false));
    }

    public Constellation createConstellation(String name, String description) {
        validateUniqueConstellationName(name, null);
        Constellation constellation = Constellation.create(name, description);
        constellations.put(constellation.id(), constellation);
        return constellation;
    }

    public Constellation renameConstellation(UUID id, String name) {
        Constellation existing = requireConstellation(id);
        validateUniqueConstellationName(name, id);
        Constellation renamed = existing.renamed(name);
        constellations.put(id, renamed);
        return renamed;
    }

    public void deleteConstellation(UUID id) {
        requireConstellation(id);
        constellations.remove(id);
    }

    public void setConstellationMembership(UUID constellationId, UUID memoryId, boolean included) {
        Constellation constellation = requireConstellation(constellationId);
        requireMemory(memoryId);
        constellations.put(constellationId, constellation.withMembership(memoryId, included));
    }

    public Constellation constellation(UUID id) {
        return requireConstellation(id);
    }

    public List<Constellation> constellations() {
        return List.copyOf(constellations.values());
    }

    public StarPosition starPosition(UUID memoryId) {
        requireMemory(memoryId);
        return starPositions.get(memoryId);
    }

    public void updateStarPosition(UUID memoryId, StarPosition position) {
        requireMemory(memoryId);
        starPositions.put(memoryId, Objects.requireNonNull(position, "position must not be null"));
    }

    public List<Memory> findMemories(JournalFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");
        Collection<UUID> constellationMembers = filter.requiredConstellationId()
                .map(this::requireConstellation)
                .map(Constellation::memoryIds)
                .orElse(null);
        return memories.values().stream()
                .filter(memory -> matchesSearch(memory, filter.search().orElse(null)))
                .filter(memory -> filter.moods().isEmpty() || filter.moods().contains(memory.mood()))
                .filter(memory -> filter.requiredTag().map(memory.tags()::contains).orElse(true))
                .filter(memory -> constellationMembers == null || constellationMembers.contains(memory.id()))
                .filter(memory -> filter.requiredYear().map(year -> memory.occurredOn().getYear() == year).orElse(true))
                .sorted(Comparator.comparing(Memory::occurredOn).reversed().thenComparing(Memory::title))
                .toList();
    }

    public JournalSnapshot snapshot() {
        return new JournalSnapshot(memories(), constellations(), starPositions);
    }

    public void clear() {
        memories.clear();
        constellations.clear();
        starPositions.clear();
    }

    public static StarPosition positionFor(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        UUID xHash = UUID.nameUUIDFromBytes((id + ":x").getBytes(StandardCharsets.UTF_8));
        UUID yHash = UUID.nameUUIDFromBytes((id + ":y").getBytes(StandardCharsets.UTF_8));
        return new StarPosition(scaleHash(xHash.getMostSignificantBits()), scaleHash(yHash.getMostSignificantBits()));
    }

    private static double scaleHash(long hash) {
        double unit = (hash >>> 11) * 0x1.0p-53;
        return 0.05 + unit * 0.90;
    }

    private static boolean matchesSearch(Memory memory, String query) {
        if (query == null) {
            return true;
        }
        List<String> searchable = new ArrayList<>();
        searchable.add(memory.title());
        memory.description().ifPresent(searchable::add);
        searchable.addAll(memory.tags());
        searchable.addAll(memory.people());
        memory.location().ifPresent(searchable::add);
        return searchable.stream().map(value -> value.toLowerCase(Locale.ROOT)).anyMatch(value -> value.contains(query));
    }

    private Memory requireMemory(UUID id) {
        Objects.requireNonNull(id, "memory id must not be null");
        Memory memory = memories.get(id);
        if (memory == null) {
            throw new NoSuchElementException("memory not found: " + id);
        }
        return memory;
    }

    private Constellation requireConstellation(UUID id) {
        Objects.requireNonNull(id, "constellation id must not be null");
        Constellation constellation = constellations.get(id);
        if (constellation == null) {
            throw new NoSuchElementException("constellation not found: " + id);
        }
        return constellation;
    }

    private void validateUniqueConstellationName(String name, UUID ignoredId) {
        Objects.requireNonNull(name, "name must not be null");
        String normalized = name.strip().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        boolean duplicate = constellations.values().stream()
                .filter(constellation -> !constellation.id().equals(ignoredId))
                .anyMatch(constellation -> constellation.name().equalsIgnoreCase(normalized));
        if (duplicate) {
            throw new IllegalArgumentException("constellation name already exists: " + normalized);
        }
    }
}
