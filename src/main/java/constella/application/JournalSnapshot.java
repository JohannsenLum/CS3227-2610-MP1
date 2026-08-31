package constella.application;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.StarPosition;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Immutable complete journal state used at application boundaries. */
public record JournalSnapshot(
        List<Memory> memories,
        List<Constellation> constellations,
        Map<UUID, StarPosition> starPositions) {
    public JournalSnapshot {
        memories = List.copyOf(Objects.requireNonNull(memories, "memories must not be null"));
        constellations = List.copyOf(Objects.requireNonNull(constellations, "constellations must not be null"));
        starPositions = Map.copyOf(Objects.requireNonNull(starPositions, "starPositions must not be null"));
    }

    public static JournalSnapshot empty() {
        return new JournalSnapshot(List.of(), List.of(), Map.of());
    }
}
