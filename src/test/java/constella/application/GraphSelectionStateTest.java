package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GraphSelectionStateTest {
    @Test
    void hoverTemporarilyTakesPrecedenceOverDurableSelection() {
        GraphSelectionState state = new GraphSelectionState();
        UUID selected = UUID.randomUUID();
        UUID hovered = UUID.randomUUID();

        state.select(selected);
        state.hover(hovered);
        assertEquals(hovered, state.focusId().orElseThrow());
        assertEquals(selected, state.selectedId().orElseThrow());

        state.clearHover();
        assertEquals(selected, state.focusId().orElseThrow());
        state.clearSelection();
        assertTrue(state.focusId().isEmpty());
    }
}
