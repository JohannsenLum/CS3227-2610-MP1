package constella.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** An immutable journal memory whose identity is its UUID. */
public final class Memory {
    private static final int MIN_IMPORTANCE = 1;
    private static final int MAX_IMPORTANCE = 5;

    private final UUID id;
    private final String title;
    private final LocalDate occurredOn;
    private final String description;
    private final Mood mood;
    private final int importance;
    private final Set<String> tags;
    private final Set<String> people;
    private final String location;

    public Memory(
            UUID id,
            String title,
            LocalDate occurredOn,
            String description,
            Mood mood,
            int importance,
            Collection<String> tags,
            Collection<String> people,
            String location) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = normalizeRequiredText(title, "title");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
        this.description = normalizeOptionalText(description);
        this.mood = Objects.requireNonNull(mood, "mood must not be null");
        this.importance = validateImportance(importance);
        this.tags = normalizeTags(tags);
        this.people = normalizePeople(people);
        this.location = normalizeOptionalText(location);
    }

    public static Memory create(
            String title,
            LocalDate occurredOn,
            String description,
            Mood mood,
            int importance,
            Collection<String> tags,
            Collection<String> people,
            String location) {
        return new Memory(
                UUID.randomUUID(), title, occurredOn, description, mood, importance, tags, people, location);
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public LocalDate occurredOn() {
        return occurredOn;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Mood mood() {
        return mood;
    }

    public int importance() {
        return importance;
    }

    public Set<String> tags() {
        return tags;
    }

    public Set<String> people() {
        return people;
    }

    public Optional<String> location() {
        return Optional.ofNullable(location);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Memory memory && id.equals(memory.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static int validateImportance(int importance) {
        if (importance < MIN_IMPORTANCE || importance > MAX_IMPORTANCE) {
            throw new IllegalArgumentException("importance must be between 1 and 5");
        }
        return importance;
    }

    private static Set<String> normalizeTags(Collection<String> tags) {
        Objects.requireNonNull(tags, "tags must not be null");
        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            normalized.add(normalizeRequiredText(tag, "tag").toLowerCase(Locale.ROOT));
        }
        return Collections.unmodifiableSet(normalized);
    }

    private static Set<String> normalizePeople(Collection<String> people) {
        Objects.requireNonNull(people, "people must not be null");
        Map<String, String> normalizedByKey = new LinkedHashMap<>();
        for (String person : people) {
            String normalized = normalizeRequiredText(person, "person name");
            normalizedByKey.putIfAbsent(normalized.toLowerCase(Locale.ROOT), normalized);
        }
        return Collections.unmodifiableSet(new LinkedHashSet<>(normalizedByKey.values()));
    }

    private static String normalizeRequiredText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String normalized = normalizeWhitespace(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = normalizeWhitespace(value);
        return normalized.isEmpty() ? null : normalized;
    }

    private static String normalizeWhitespace(String value) {
        return value.strip().replaceAll("\\s+", " ");
    }
}
