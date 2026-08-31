package constella.application;

import constella.model.Constellation;
import java.util.Locale;
import java.util.Objects;

/** UI-independent name matching for the memory editor's constellation list. */
public final class ConstellationSearch {
    private ConstellationSearch() { }

    public static boolean matches(Constellation constellation, String query) {
        Objects.requireNonNull(constellation, "constellation must not be null");
        String normalizedQuery = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        return normalizedQuery.isEmpty()
                || constellation.name().toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
