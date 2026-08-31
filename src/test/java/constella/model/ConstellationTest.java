package constella.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConstellationTest {
    @Test
    void creationNormalizesValuesAndStartsEmpty() {
        Constellation constellation = Constellation.create("  Family   Trips ", " Shared   adventures ");

        assertEquals("Family Trips", constellation.name());
        assertEquals("Shared adventures", constellation.description().orElseThrow());
        assertTrue(constellation.memoryIds().isEmpty());
    }

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Constellation.create(" ", null));
    }

    @Test
    void membershipIsImmutable() {
        UUID memoryId = UUID.randomUUID();
        Constellation original = new Constellation(UUID.randomUUID(), "Name", null, List.of());
        Constellation updated = original.withMembership(memoryId, true);

        assertTrue(original.memoryIds().isEmpty());
        assertEquals(java.util.Set.of(memoryId), updated.memoryIds());
        assertThrows(UnsupportedOperationException.class, () -> updated.memoryIds().add(UUID.randomUUID()));
    }
}
