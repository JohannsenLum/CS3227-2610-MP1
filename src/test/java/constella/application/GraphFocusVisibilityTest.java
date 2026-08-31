package constella.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GraphFocusVisibilityTest {
    private static final UUID FIRST = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SECOND = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID THIRD = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ALPHA = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID BETA = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final MemoryGraphEdge EDGE = new MemoryGraphEdge(FIRST, SECOND, Set.of(ALPHA, BETA));

    @Test
    void allFocusShowsEveryEdgeAndConstellationFocusIsExact() {
        assertTrue(GraphFocusVisibility.shows(EDGE, null, null));
        assertTrue(GraphFocusVisibility.shows(EDGE, null, ALPHA));
        assertTrue(GraphFocusVisibility.shows(EDGE, null, BETA));
        assertFalse(GraphFocusVisibility.shows(EDGE, null, THIRD));
    }

    @Test
    void memoryFocusShowsOnlyDirectlyAttachedEdgesAndTakesPrecedence() {
        assertTrue(GraphFocusVisibility.shows(EDGE, FIRST, THIRD));
        assertTrue(GraphFocusVisibility.shows(EDGE, SECOND, ALPHA));
        assertFalse(GraphFocusVisibility.shows(EDGE, THIRD, ALPHA));
    }
}
