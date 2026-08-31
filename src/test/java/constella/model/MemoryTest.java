package constella.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemoryTest {
    private static final LocalDate DATE = LocalDate.of(2026, 8, 26);

    @Test
    void createWithCompleteDataNormalizesAndRetainsValues() {
        Memory memory = Memory.create(
                "  Trip   to Kyoto ",
                DATE,
                "  Saw   the temples ",
                Mood.PEACEFUL,
                5,
                List.of(" Travel ", "Japan"),
                List.of(" Alice Smith ", "Bob"),
                " Kyoto,   Japan ");

        assertNotNull(memory.id());
        assertEquals("Trip to Kyoto", memory.title());
        assertEquals(DATE, memory.occurredOn());
        assertEquals("Saw the temples", memory.description().orElseThrow());
        assertEquals(Mood.PEACEFUL, memory.mood());
        assertEquals(5, memory.importance());
        assertEquals(Set.of("travel", "japan"), memory.tags());
        assertEquals(Set.of("Alice Smith", "Bob"), memory.people());
        assertEquals("Kyoto, Japan", memory.location().orElseThrow());
    }

    @Test
    void createWithMinimalDataUsesEmptyOptionalValuesAndCollections() {
        Memory memory = Memory.create("Quiet morning", DATE, null, Mood.NEUTRAL, 1, List.of(), List.of(), null);

        assertTrue(memory.description().isEmpty());
        assertTrue(memory.tags().isEmpty());
        assertTrue(memory.people().isEmpty());
        assertTrue(memory.location().isEmpty());
    }

    @Test
    void createGeneratesDifferentIds() {
        Memory first = minimalMemory("First");
        Memory second = minimalMemory("Second");

        assertNotEquals(first.id(), second.id());
    }

    @Test
    void constructorReusesExistingId() {
        UUID id = UUID.randomUUID();
        Memory memory = memoryWithId(id, "Remembered");

        assertEquals(id, memory.id());
    }

    @Test
    void nullIdIsRejectedWithMeaningfulMessage() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> memoryWithId(null, "Title"));

        assertEquals("id must not be null", exception.getMessage());
    }

    @Test
    void nullTitleIsRejected() {
        assertThrows(NullPointerException.class, () -> minimalMemory(null));
    }

    @Test
    void blankTitleIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> minimalMemory("  \t "));
    }

    @Test
    void nullDateIsRejected() {
        assertThrows(NullPointerException.class,
                () -> Memory.create("Title", null, null, Mood.NEUTRAL, 3, List.of(), List.of(), null));
    }

    @Test
    void nullMoodIsRejected() {
        assertThrows(NullPointerException.class,
                () -> Memory.create("Title", DATE, null, null, 3, List.of(), List.of(), null));
    }

    @Test
    void boundaryImportanceValuesAreAccepted() {
        assertEquals(1, memoryWithImportance(1).importance());
        assertEquals(5, memoryWithImportance(5).importance());
    }

    @Test
    void importanceBelowRangeIsRejected() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> memoryWithImportance(0));

        assertEquals("importance must be between 1 and 5", exception.getMessage());
    }

    @Test
    void importanceAboveRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> memoryWithImportance(6));
    }

    @Test
    void blankDescriptionIsTreatedAsAbsent() {
        Memory memory = Memory.create("Title", DATE, " \t ", Mood.NEUTRAL, 3, List.of(), List.of(), null);

        assertTrue(memory.description().isEmpty());
    }

    @Test
    void blankLocationIsTreatedAsAbsent() {
        Memory memory = Memory.create("Title", DATE, null, Mood.NEUTRAL, 3, List.of(), List.of(), "  ");

        assertTrue(memory.location().isEmpty());
    }

    @Test
    void tagsAreNormalizedAndDuplicatesAreCoalesced() {
        Memory memory = memoryWithCollections(List.of(" Family Time ", "FAMILY   TIME", "Travel"), List.of());

        assertEquals(List.of("family time", "travel"), List.copyOf(memory.tags()));
    }

    @Test
    void blankTagIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> memoryWithCollections(List.of("valid", " "), List.of()));
    }

    @Test
    void peopleAreNormalizedAndDuplicatesAreCoalescedCaseInsensitively() {
        Memory memory = memoryWithCollections(List.of(), List.of(" Alice   Smith ", "alice smith", " Bob "));

        assertEquals(List.of("Alice Smith", "Bob"), List.copyOf(memory.people()));
    }

    @Test
    void blankPersonIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> memoryWithCollections(List.of(), List.of("Alice", "")));
    }

    @Test
    void collectionsAreDefensivelyCopiedAndUnmodifiable() {
        List<String> sourceTags = new ArrayList<>(List.of("travel"));
        List<String> sourcePeople = new ArrayList<>(List.of("Alice"));
        Memory memory = memoryWithCollections(sourceTags, sourcePeople);
        sourceTags.add("later");
        sourcePeople.add("Bob");

        assertEquals(Set.of("travel"), memory.tags());
        assertEquals(Set.of("Alice"), memory.people());
        assertThrows(UnsupportedOperationException.class, () -> memory.tags().add("other"));
        assertThrows(UnsupportedOperationException.class, () -> memory.people().add("Other"));
    }

    @Test
    void memoriesWithSameIdAreEqualRegardlessOfOtherValues() {
        UUID id = UUID.randomUUID();
        Memory first = memoryWithId(id, "First title");
        Memory second = memoryWithId(id, "Different title");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void memoriesWithDifferentIdsAreNotEqual() {
        assertFalse(minimalMemory("Title").equals(minimalMemory("Title")));
    }

    private static Memory minimalMemory(String title) {
        return Memory.create(title, DATE, null, Mood.NEUTRAL, 3, List.of(), List.of(), null);
    }

    private static Memory memoryWithId(UUID id, String title) {
        return new Memory(id, title, DATE, null, Mood.NEUTRAL, 3, List.of(), List.of(), null);
    }

    private static Memory memoryWithImportance(int importance) {
        return Memory.create("Title", DATE, null, Mood.NEUTRAL, importance, List.of(), List.of(), null);
    }

    private static Memory memoryWithCollections(List<String> tags, List<String> people) {
        return Memory.create("Title", DATE, null, Mood.NEUTRAL, 3, tags, people, null);
    }
}
