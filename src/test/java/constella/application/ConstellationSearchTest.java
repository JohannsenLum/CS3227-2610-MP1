package constella.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConstellationSearchTest {
    private static final Constellation ACADEMIC = new Constellation(
            UUID.randomUUID(), "Academic Milestones", null, Set.of());

    @Test
    void blankAndCaseInsensitiveSubstringQueriesMatch() {
        assertTrue(ConstellationSearch.matches(ACADEMIC, null));
        assertTrue(ConstellationSearch.matches(ACADEMIC, "  "));
        assertTrue(ConstellationSearch.matches(ACADEMIC, "MILEStone"));
    }

    @Test
    void unrelatedQueryDoesNotMatch() {
        assertFalse(ConstellationSearch.matches(ACADEMIC, "exchange"));
    }
}
