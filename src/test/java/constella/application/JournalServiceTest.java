package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import constella.model.StarPosition;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JournalServiceTest {
    @Test
    void addRetrieveUpdateAndDeleteMemory() {
        JournalService service = new JournalService();
        Memory original = memory("Original", 2025, Mood.JOYFUL, List.of("travel"), List.of("Alice"), "Paris");
        service.addMemory(original);

        assertEquals(original, service.memory(original.id()));
        Memory edited = new Memory(original.id(), "Edited", original.occurredOn(), "Changed", Mood.PEACEFUL,
                4, original.tags(), original.people(), original.location().orElse(null));
        service.updateMemory(edited);
        assertEquals("Edited", service.memory(original.id()).title());

        service.deleteMemory(original.id());
        assertThrows(NoSuchElementException.class, () -> service.memory(original.id()));
    }

    @Test
    void duplicateMemoryIdentityIsRejected() {
        JournalService service = new JournalService();
        Memory memory = memory("One", 2025, Mood.NEUTRAL, List.of(), List.of(), null);
        service.addMemory(memory);

        assertThrows(IllegalArgumentException.class, () -> service.addMemory(memory));
    }

    @Test
    void createsRenamesAndDeletesConstellation() {
        JournalService service = new JournalService();
        Constellation constellation = service.createConstellation("Travels", "Places visited");

        assertEquals("Travels", service.constellation(constellation.id()).name());
        service.renameConstellation(constellation.id(), "Adventures");
        assertEquals("Adventures", service.constellation(constellation.id()).name());
        service.deleteConstellation(constellation.id());
        assertTrue(service.constellations().isEmpty());
    }

    @Test
    void duplicateConstellationNamesAreCaseInsensitive() {
        JournalService service = new JournalService();
        Constellation first = service.createConstellation("Family Trips", null);

        assertThrows(IllegalArgumentException.class, () -> service.createConstellation(" family   trips ", null));
        Constellation second = service.createConstellation("Friends", null);
        assertThrows(IllegalArgumentException.class, () -> service.renameConstellation(second.id(), first.name()));
    }

    @Test
    void memoryCanBelongToMultipleConstellationsAndBeRemoved() {
        JournalService service = new JournalService();
        Memory memory = memory("Trip", 2025, Mood.EXCITED, List.of(), List.of(), null);
        service.addMemory(memory);
        Constellation first = service.createConstellation("Travel", null);
        Constellation second = service.createConstellation("Highlights", null);

        service.setConstellationMembership(first.id(), memory.id(), true);
        service.setConstellationMembership(second.id(), memory.id(), true);
        assertTrue(service.constellation(first.id()).memoryIds().contains(memory.id()));
        assertTrue(service.constellation(second.id()).memoryIds().contains(memory.id()));

        service.setConstellationMembership(first.id(), memory.id(), false);
        assertFalse(service.constellation(first.id()).memoryIds().contains(memory.id()));
    }

    @Test
    void deletingMemoryCleansAllMembershipsAndPosition() {
        JournalService service = new JournalService();
        Memory memory = memory("Trip", 2025, Mood.EXCITED, List.of(), List.of(), null);
        service.addMemory(memory);
        Constellation constellation = service.createConstellation("Travel", null);
        service.setConstellationMembership(constellation.id(), memory.id(), true);

        service.deleteMemory(memory.id());

        assertTrue(service.constellation(constellation.id()).memoryIds().isEmpty());
        assertFalse(service.snapshot().starPositions().containsKey(memory.id()));
    }

    @Test
    void deletingConstellationLeavesMemoryIntact() {
        JournalService service = new JournalService();
        Memory memory = memory("Trip", 2025, Mood.EXCITED, List.of(), List.of(), null);
        service.addMemory(memory);
        Constellation constellation = service.createConstellation("Travel", null);
        service.setConstellationMembership(constellation.id(), memory.id(), true);

        service.deleteConstellation(constellation.id());

        assertEquals(memory, service.memory(memory.id()));
    }

    @Test
    void searchIsCaseInsensitiveAcrossRequiredFields() {
        JournalService service = populatedService();

        assertEquals("Kyoto", service.findMemories(filter("TEMPLES", Set.of(), null, null, null)).getFirst().title());
        assertEquals("Kyoto", service.findMemories(filter("family", Set.of(), null, null, null)).getFirst().title());
        assertEquals("Kyoto", service.findMemories(filter("alice", Set.of(), null, null, null)).getFirst().title());
        assertEquals("Kyoto", service.findMemories(filter("japan", Set.of(), null, null, null)).getFirst().title());
    }

    @Test
    void combinedFiltersUseAndSemantics() {
        JournalService service = populatedService();
        Memory kyoto = service.memories().stream().filter(memory -> memory.title().equals("Kyoto")).findFirst().orElseThrow();
        Constellation travel = service.createConstellation("Travel", null);
        service.setConstellationMembership(travel.id(), kyoto.id(), true);

        JournalFilter matching = filter("temple", Set.of(Mood.PEACEFUL), "family", travel.id(), 2025);
        assertEquals(List.of(kyoto), service.findMemories(matching));
        assertTrue(service.findMemories(filter("temple", Set.of(Mood.SAD), "family", travel.id(), 2025)).isEmpty());
    }

    @Test
    void resultsAreNewestFirst() {
        JournalService service = populatedService();

        assertEquals(List.of("Home", "Kyoto"), service.findMemories(JournalFilter.none()).stream()
                .map(Memory::title).toList());
    }

    @Test
    void deterministicPositionIsStableAndDependsOnIdentity() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        StarPosition first = JournalService.positionFor(firstId);
        assertEquals(first, JournalService.positionFor(firstId));
        assertNotEquals(first, JournalService.positionFor(secondId));
        assertTrue(first.x() >= 0.05 && first.x() <= 0.95);
        assertTrue(first.y() >= 0.05 && first.y() <= 0.95);
    }

    @Test
    void starPositionCanBeMovedWithoutChangingMemory() {
        JournalService service = new JournalService();
        Memory memory = memory("Movable", 2025, Mood.NEUTRAL, List.of(), List.of(), null);
        service.addMemory(memory);
        StarPosition moved = new StarPosition(0.22, 0.78);

        service.updateStarPosition(memory.id(), moved);

        assertEquals(moved, service.starPosition(memory.id()));
        assertEquals(memory, service.memory(memory.id()));
        assertThrows(NullPointerException.class, () -> service.updateStarPosition(memory.id(), null));
    }

    @Test
    void staleMembershipsAreRemovedWhenLoadingSnapshot() {
        UUID staleId = UUID.randomUUID();
        Constellation constellation = new Constellation(UUID.randomUUID(), "Group", null, Set.of(staleId));

        JournalService service = new JournalService(new JournalSnapshot(List.of(), List.of(constellation), java.util.Map.of()));

        assertTrue(service.constellation(constellation.id()).memoryIds().isEmpty());
    }

    private static JournalService populatedService() {
        JournalService service = new JournalService();
        service.addMemory(new Memory(UUID.randomUUID(), "Kyoto", LocalDate.of(2025, 4, 2), "Ancient temples",
                Mood.PEACEFUL, 5, List.of("family", "travel"), List.of("Alice"), "Japan"));
        service.addMemory(memory("Home", 2026, Mood.JOYFUL, List.of("quiet"), List.of("Bob"), "Singapore"));
        return service;
    }

    private static Memory memory(String title, int year, Mood mood, List<String> tags, List<String> people,
            String location) {
        return Memory.create(title, LocalDate.of(year, 1, 2), null, mood, 3, tags, people, location);
    }

    private static JournalFilter filter(String search, Set<Mood> moods, String tag, UUID constellationId,
            Integer year) {
        return new JournalFilter(search, moods, tag, constellationId, year);
    }
}
