package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemoryDraftTest {
    @Test
    void convertsCommaSeparatedEditorInputIntoValidatedMemory() {
        MemoryDraft draft = new MemoryDraft(" Evening walk ", LocalDate.of(2026, 8, 26), " Calm ", Mood.PEACEFUL,
                4, " coast, sunset ", " Alice, Bob ", " East Coast Park ");

        Memory memory = draft.createMemory();

        assertEquals("Evening walk", memory.title());
        assertEquals(java.util.Set.of("coast", "sunset"), memory.tags());
        assertEquals(java.util.Set.of("Alice", "Bob"), memory.people());
    }

    @Test
    void updatePreservesProvidedIdentity() {
        UUID id = UUID.randomUUID();
        MemoryDraft draft = new MemoryDraft("Updated", LocalDate.now(), null, Mood.NEUTRAL, 3, "", "", "");

        assertEquals(id, draft.updateMemory(id).id());
    }

    @Test
    void blankCommaSeparatedItemIsRejected() {
        MemoryDraft draft = new MemoryDraft("Title", LocalDate.now(), null, Mood.NEUTRAL, 3, "valid, ", "", null);

        assertThrows(IllegalArgumentException.class, draft::createMemory);
    }
}
