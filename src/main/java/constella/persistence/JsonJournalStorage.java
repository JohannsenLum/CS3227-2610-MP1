package constella.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import constella.application.JournalService;
import constella.application.JournalSnapshot;
import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import constella.model.StarPosition;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/** UTF-8 JSON storage using validated DTOs and atomic replacement where supported. */
public final class JsonJournalStorage implements JournalStorage {
    private static final int FORMAT_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;

    public JsonJournalStorage(Path file) {
        this.file = Objects.requireNonNull(file, "file must not be null").toAbsolutePath();
    }

    public static JsonJournalStorage forCurrentUser() {
        return new JsonJournalStorage(ApplicationDataPath.journalFile());
    }

    public Path file() {
        return file;
    }

    @Override
    public boolean exists() {
        return Files.exists(file);
    }

    @Override
    public JournalSnapshot load() throws JournalStorageException {
        if (Files.notExists(file)) {
            return JournalSnapshot.empty();
        }
        return readAndValidate(file);
    }

    @Override
    public void save(JournalSnapshot snapshot) throws JournalStorageException {
        Objects.requireNonNull(snapshot, "snapshot must not be null");
        if (Files.exists(file)) {
            readAndValidate(file);
        }

        Path parent = file.getParent();
        Path temporary = null;
        try {
            Files.createDirectories(parent);
            temporary = Files.createTempFile(parent, "journal-", ".tmp");
            String json = GSON.toJson(toData(snapshot));
            Files.writeString(temporary, json, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException exception) {
            throw new JournalStorageException("Could not save journal data to " + file, exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // The primary save result is more useful than a temporary-file cleanup failure.
                }
            }
        }
    }

    private JournalSnapshot readAndValidate(Path source) throws JournalStorageException {
        try {
            String json = Files.readString(source, StandardCharsets.UTF_8);
            JournalData data = GSON.fromJson(json, JournalData.class);
            if (data == null) {
                throw new IllegalArgumentException("journal data must be a JSON object");
            }
            return fromData(data);
        } catch (IOException | JsonParseException | DateTimeException | IllegalArgumentException | NullPointerException exception) {
            throw new JournalStorageException(
                    "Could not load journal data from " + source + ". The file was left unchanged.", exception);
        }
    }

    private static JournalData toData(JournalSnapshot snapshot) {
        List<MemoryData> memoryData = snapshot.memories().stream()
                .map(memory -> new MemoryData(
                        memory.id().toString(), memory.title(), memory.occurredOn().toString(),
                        memory.description().orElse(null), memory.mood().name(), memory.importance(),
                        List.copyOf(memory.tags()), List.copyOf(memory.people()), memory.location().orElse(null),
                        PositionData.from(snapshot.starPositions().getOrDefault(
                                memory.id(), JournalService.positionFor(memory.id())))))
                .toList();
        List<ConstellationData> constellationData = snapshot.constellations().stream()
                .map(constellation -> new ConstellationData(
                        constellation.id().toString(), constellation.name(), constellation.description().orElse(null),
                        constellation.memoryIds().stream().map(UUID::toString).toList()))
                .toList();
        return new JournalData(FORMAT_VERSION, memoryData, constellationData);
    }

    private static JournalSnapshot fromData(JournalData data) {
        if (data.version() != FORMAT_VERSION) {
            throw new IllegalArgumentException("unsupported journal format version: " + data.version());
        }
        Objects.requireNonNull(data.memories(), "memories must not be null");
        Objects.requireNonNull(data.constellations(), "constellations must not be null");

        List<Memory> memories = new ArrayList<>();
        java.util.Map<UUID, StarPosition> positions = new java.util.LinkedHashMap<>();
        for (MemoryData saved : data.memories()) {
            Objects.requireNonNull(saved, "memory entry must not be null");
            UUID id = UUID.fromString(saved.id());
            Memory memory = new Memory(id, saved.title(), LocalDate.parse(saved.occurredOn()), saved.description(),
                    Mood.valueOf(saved.mood()), saved.importance(), saved.tags(), saved.people(), saved.location());
            memories.add(memory);
            PositionData position = saved.position();
            positions.put(id, position == null
                    ? JournalService.positionFor(id)
                    : new StarPosition(position.x(), position.y()));
        }
        Set<UUID> validMemoryIds = memories.stream().map(Memory::id).collect(java.util.stream.Collectors.toSet());
        List<Constellation> constellations = new ArrayList<>();
        for (ConstellationData saved : data.constellations()) {
            Objects.requireNonNull(saved, "constellation entry must not be null");
            Objects.requireNonNull(saved.memoryIds(), "constellation memoryIds must not be null");
            List<UUID> validMemberships = saved.memoryIds().stream()
                    .map(UUID::fromString)
                    .filter(validMemoryIds::contains)
                    .toList();
            constellations.add(new Constellation(
                    UUID.fromString(saved.id()), saved.name(), saved.description(), validMemberships));
        }
        return new JournalService(new JournalSnapshot(memories, constellations, positions)).snapshot();
    }

    private record JournalData(int version, List<MemoryData> memories, List<ConstellationData> constellations) {
    }

    private record MemoryData(
            String id,
            String title,
            String occurredOn,
            String description,
            String mood,
            int importance,
            List<String> tags,
            List<String> people,
            String location,
            PositionData position) {
    }

    private record ConstellationData(String id, String name, String description, List<String> memoryIds) {
    }

    private record PositionData(double x, double y) {
        private static PositionData from(StarPosition position) {
            return new PositionData(position.x(), position.y());
        }
    }
}
