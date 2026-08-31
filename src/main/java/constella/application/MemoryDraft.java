package constella.application;

import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/** UI-independent input for creating or reconstructing a validated memory. */
public record MemoryDraft(
        String title,
        LocalDate occurredOn,
        String description,
        Mood mood,
        int importance,
        String tags,
        String people,
        String location) {

    public Memory createMemory() {
        return toMemory(UUID.randomUUID());
    }

    public Memory updateMemory(UUID id) {
        return toMemory(id);
    }

    private Memory toMemory(UUID id) {
        return new Memory(id, title, occurredOn, description, mood, importance,
                commaSeparated(tags), commaSeparated(people), location);
    }

    private static List<String> commaSeparated(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(",", -1)).map(String::strip).toList();
    }
}
