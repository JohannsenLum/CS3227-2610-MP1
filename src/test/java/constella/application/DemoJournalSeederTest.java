package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class DemoJournalSeederTest {
    @Test
    void createsFourYearNusJournalWithTripsAndExchange() {
        JournalSnapshot snapshot = DemoJournalSeeder.create();

        assertEquals(24, snapshot.memories().size());
        assertEquals(5, snapshot.constellations().size());
        assertEquals(Set.of(2022, 2023, 2024, 2025, 2026), snapshot.memories().stream()
                .map(memory -> memory.occurredOn().getYear()).collect(java.util.stream.Collectors.toSet()));
        assertTrue(snapshot.memories().stream().anyMatch(memory -> memory.tags().contains("nus")));
        assertTrue(snapshot.memories().stream().anyMatch(memory -> memory.tags().contains("exchange")));
        assertTrue(snapshot.memories().stream().anyMatch(memory -> memory.tags().contains("holiday")));
        assertTrue(snapshot.memories().stream().anyMatch(memory -> memory.tags().contains("cs3227")));
    }

    @Test
    void deterministicIdsAndPositionsAreStable() {
        JournalSnapshot first = DemoJournalSeeder.create();
        JournalSnapshot second = DemoJournalSeeder.create();

        assertEquals(first.memories().stream().map(memory -> memory.id()).toList(),
                second.memories().stream().map(memory -> memory.id()).toList());
        assertEquals(first.starPositions(), second.starPositions());
    }

    @Test
    void everyConstellationMembershipReferencesSeededMemory() {
        JournalSnapshot snapshot = DemoJournalSeeder.create();
        Set<java.util.UUID> memoryIds = snapshot.memories().stream()
                .map(memory -> memory.id()).collect(java.util.stream.Collectors.toSet());

        assertTrue(snapshot.constellations().stream()
                .flatMap(constellation -> constellation.memoryIds().stream()).allMatch(memoryIds::contains));
    }

    @Test
    void includesIntentionalDisconnectedMemoriesForGraphExploration() {
        JournalSnapshot snapshot = DemoJournalSeeder.create();
        Set<java.util.UUID> connected = snapshot.constellations().stream()
                .flatMap(constellation -> constellation.memoryIds().stream())
                .collect(java.util.stream.Collectors.toSet());

        assertEquals(3, snapshot.memories().stream().filter(memory -> !connected.contains(memory.id())).count());
    }
}
