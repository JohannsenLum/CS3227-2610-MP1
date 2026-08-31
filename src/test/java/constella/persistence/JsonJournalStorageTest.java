package constella.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.application.JournalService;
import constella.application.JournalSnapshot;
import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonJournalStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void missingFileLoadsEmptyJournal() throws Exception {
        JsonJournalStorage storage = storage();

        assertEquals(JournalSnapshot.empty(), storage.load());
        assertFalse(Files.exists(storage.file()));
    }

    @Test
    void completeJournalRoundTripsWithStableIdsMembershipAndPosition() throws Exception {
        JournalService service = new JournalService();
        Memory memory = Memory.create("Kyoto", LocalDate.of(2025, 4, 2), "Temples", Mood.PEACEFUL, 5,
                List.of("travel"), List.of("Alice"), "Japan");
        service.addMemory(memory);
        Constellation constellation = service.createConstellation("Adventures", "Shared journeys");
        service.setConstellationMembership(constellation.id(), memory.id(), true);
        JsonJournalStorage storage = storage();

        storage.save(service.snapshot());
        JournalSnapshot loaded = storage.load();

        Memory loadedMemory = loaded.memories().getFirst();
        Constellation loadedConstellation = loaded.constellations().getFirst();
        assertEquals(memory.id(), loadedMemory.id());
        assertEquals("Kyoto", loadedMemory.title());
        assertEquals("Temples", loadedMemory.description().orElseThrow());
        assertEquals(constellation.id(), loadedConstellation.id());
        assertEquals(java.util.Set.of(memory.id()), loadedConstellation.memoryIds());
        assertEquals(service.starPosition(memory.id()), loaded.starPositions().get(memory.id()));
    }

    @Test
    void utf8ContentRoundTripsAndFileIsHumanReadable() throws Exception {
        JournalService service = new JournalService();
        Memory memory = Memory.create("京都の思い出 ✨", LocalDate.of(2025, 4, 2), "静かな午後", Mood.PEACEFUL,
                4, List.of("旅行"), List.of("美咲"), "京都");
        service.addMemory(memory);
        JsonJournalStorage storage = storage();

        storage.save(service.snapshot());

        String json = Files.readString(storage.file(), StandardCharsets.UTF_8);
        assertTrue(json.contains("京都の思い出 ✨"));
        assertEquals("京都の思い出 ✨", storage.load().memories().getFirst().title());
    }

    @Test
    void malformedFileReportsErrorAndRemainsUnchanged() throws Exception {
        JsonJournalStorage storage = storage();
        Files.createDirectories(storage.file().getParent());
        String malformed = "{ definitely not json";
        Files.writeString(storage.file(), malformed, StandardCharsets.UTF_8);

        JournalStorageException loadError = assertThrows(JournalStorageException.class, storage::load);
        assertTrue(loadError.getMessage().contains("left unchanged"));
        assertThrows(JournalStorageException.class, () -> storage.save(JournalSnapshot.empty()));
        assertEquals(malformed, Files.readString(storage.file(), StandardCharsets.UTF_8));
    }

    @Test
    void invalidDomainDataReportsLoadError() throws Exception {
        JsonJournalStorage storage = storage();
        Files.createDirectories(storage.file().getParent());
        Files.writeString(storage.file(), """
                {"version":1,"memories":[{"id":"not-a-uuid"}],"constellations":[]}
                """, StandardCharsets.UTF_8);

        assertThrows(JournalStorageException.class, storage::load);
    }

    @Test
    void legacyMemoryWithoutPositionGetsDeterministicPosition() throws Exception {
        JsonJournalStorage storage = storage();
        Files.createDirectories(storage.file().getParent());
        java.util.UUID id = java.util.UUID.randomUUID();
        Files.writeString(storage.file(), """
                {"version":1,"memories":[{
                  "id":"%s","title":"Legacy","occurredOn":"2024-01-02",
                  "mood":"NEUTRAL","importance":3,"tags":[],"people":[]
                }],"constellations":[]}
                """.formatted(id), StandardCharsets.UTF_8);

        JournalSnapshot loaded = storage.load();

        assertEquals(JournalService.positionFor(id), loaded.starPositions().get(id));
    }

    @Test
    void staleMembershipIsRemovedDuringLoad() throws Exception {
        JsonJournalStorage storage = storage();
        Files.createDirectories(storage.file().getParent());
        String constellationId = java.util.UUID.randomUUID().toString();
        String staleMemoryId = java.util.UUID.randomUUID().toString();
        Files.writeString(storage.file(), """
                {"version":1,"memories":[],"constellations":[
                  {"id":"%s","name":"Old links","memoryIds":["%s"]}
                ]}
                """.formatted(constellationId, staleMemoryId), StandardCharsets.UTF_8);

        JournalSnapshot loaded = storage.load();

        assertTrue(loaded.constellations().getFirst().memoryIds().isEmpty());
    }

    @Test
    void saveUsesReplacementWithoutLeavingTemporaryFiles() throws Exception {
        JsonJournalStorage storage = storage();
        storage.save(JournalSnapshot.empty());
        JournalService service = new JournalService();
        service.addMemory(Memory.create("Second state", LocalDate.now(), null, Mood.NEUTRAL, 1,
                List.of(), List.of(), null));

        storage.save(service.snapshot());

        assertEquals(1, storage.load().memories().size());
        try (var files = Files.list(storage.file().getParent())) {
            assertEquals(List.of("journal.json"), files.map(path -> path.getFileName().toString()).sorted().toList());
        }
    }

    @Test
    void failedSaveReportsTargetContext() throws Exception {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "block");
        JsonJournalStorage storage = new JsonJournalStorage(blockingFile.resolve("journal.json"));

        JournalStorageException error = assertThrows(
                JournalStorageException.class, () -> storage.save(JournalSnapshot.empty()));

        assertTrue(error.getMessage().contains("Could not save journal data"));
    }

    private JsonJournalStorage storage() {
        return new JsonJournalStorage(temporaryDirectory.resolve("constella/journal.json"));
    }
}
