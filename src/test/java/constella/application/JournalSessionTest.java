package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import constella.model.StarPosition;
import constella.persistence.JournalStorage;
import constella.persistence.JournalStorageException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JournalSessionTest {
    @Test
    void mutationsSaveUpdatedSnapshotAndMemberships() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        JournalSession session = JournalSession.load(storage);
        Constellation group = session.journal().createConstellation("Highlights", null);
        Memory memory = Memory.create("Day", LocalDate.now(), null, Mood.JOYFUL, 4, List.of(), List.of(), null);

        session.addMemory(memory, Set.of(group.id()));

        assertEquals(1, storage.saves);
        assertTrue(storage.snapshot.constellations().getFirst().memoryIds().contains(memory.id()));
    }

    @Test
    void constellationOperationsArePersisted() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        JournalSession session = JournalSession.load(storage);

        Constellation constellation = session.createConstellation("Family", null);
        session.renameConstellation(constellation.id(), "Family stories");
        session.deleteConstellation(constellation.id());

        assertEquals(3, storage.saves);
        assertTrue(storage.snapshot.constellations().isEmpty());
    }

    @Test
    void movedStarPositionIsPersisted() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        JournalSession session = JournalSession.load(storage);
        Memory memory = Memory.create("Day", LocalDate.now(), null, Mood.JOYFUL, 4, List.of(), List.of(), null);
        session.addMemory(memory, Set.of());

        session.updateStarPosition(memory.id(), new StarPosition(0.25, 0.75));

        assertEquals(2, storage.saves);
        assertEquals(new StarPosition(0.25, 0.75), storage.snapshot.starPositions().get(memory.id()));
    }

    @Test
    void missingStorageIsSeededOnce() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        storage.exists = false;
        JournalSnapshot seed = DemoJournalSeeder.create();

        JournalSession first = JournalSession.loadOrSeed(storage, () -> seed);
        JournalSession second = JournalSession.loadOrSeed(storage, () -> {
            throw new AssertionError("existing journal must not be reseeded");
        });

        assertEquals(24, first.journal().memories().size());
        assertEquals(24, second.journal().memories().size());
        assertEquals(1, storage.saves);
    }

    @Test
    void clearedJournalIsPersistedAndDoesNotReseed() throws Exception {
        RecordingStorage storage = new RecordingStorage();
        storage.exists = false;
        JournalSession session = JournalSession.loadOrSeed(storage, DemoJournalSeeder::create);

        session.clearJournal();
        JournalSession reopened = JournalSession.loadOrSeed(storage, () -> {
            throw new AssertionError("cleared journal must not be reseeded");
        });

        assertTrue(reopened.journal().memories().isEmpty());
        assertTrue(reopened.journal().constellations().isEmpty());
        assertEquals(2, storage.saves);
    }

    private static final class RecordingStorage implements JournalStorage {
        private JournalSnapshot snapshot = JournalSnapshot.empty();
        private int saves;
        private boolean exists = true;

        @Override
        public boolean exists() {
            return exists;
        }

        @Override
        public JournalSnapshot load() {
            return snapshot;
        }

        @Override
        public void save(JournalSnapshot snapshot) throws JournalStorageException {
            this.snapshot = snapshot;
            saves++;
            exists = true;
        }
    }
}
