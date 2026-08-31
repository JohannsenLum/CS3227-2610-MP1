package constella.application;

import constella.model.Mood;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Search and filter criteria; populated categories combine with AND semantics. */
public record JournalFilter(
        String searchText, Set<Mood> moods, String tag, UUID constellationId, Integer year) {
    public JournalFilter {
        searchText = normalizeOptional(searchText);
        moods = Set.copyOf(Objects.requireNonNull(moods, "moods must not be null"));
        tag = normalizeOptional(tag);
        if (year != null && year < 1) {
            throw new IllegalArgumentException("year must be positive");
        }
    }

    public static JournalFilter none() {
        return new JournalFilter(null, Set.of(), null, null, null);
    }

    public Optional<String> search() {
        return Optional.ofNullable(searchText);
    }

    public Optional<String> requiredTag() {
        return Optional.ofNullable(tag);
    }

    public Optional<UUID> requiredConstellationId() {
        return Optional.ofNullable(constellationId);
    }

    public Optional<Integer> requiredYear() {
        return Optional.ofNullable(year);
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip().replaceAll("\\s+", " ").toLowerCase(java.util.Locale.ROOT);
    }
}
