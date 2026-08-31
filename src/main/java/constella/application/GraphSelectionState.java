package constella.application;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** UI-independent hover/selection precedence used by graph renderers. */
public final class GraphSelectionState {
    private UUID selectedId;
    private UUID hoveredId;

    public Optional<UUID> selectedId() {
        return Optional.ofNullable(selectedId);
    }

    public Optional<UUID> focusId() {
        return Optional.ofNullable(hoveredId != null ? hoveredId : selectedId);
    }

    public void hover(UUID memoryId) {
        hoveredId = Objects.requireNonNull(memoryId, "memoryId must not be null");
    }

    public void clearHover() {
        hoveredId = null;
    }

    public void select(UUID memoryId) {
        selectedId = Objects.requireNonNull(memoryId, "memoryId must not be null");
    }

    public void clearSelection() {
        selectedId = null;
    }
}
