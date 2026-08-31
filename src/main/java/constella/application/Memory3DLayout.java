package constella.application;

import constella.model.Memory;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Deterministic bounded spiral layout whose chronological axis provides genuine Z depth. */
public final class Memory3DLayout {
    public static final double MAX_X = 310;
    public static final double MAX_Y = 210;
    public static final double MAX_Z = 250;
    private static final double GOLDEN_ANGLE = Math.PI * (3 - Math.sqrt(5));

    private Memory3DLayout() {
    }

    public static Map<UUID, Vector3> layout(List<Memory> memories) {
        List<Memory> ordered = memories.stream()
                .sorted(Comparator.comparing(Memory::occurredOn).thenComparing(memory -> memory.id().toString()))
                .toList();
        LinkedHashMap<UUID, Vector3> result = new LinkedHashMap<>();
        int size = ordered.size();
        for (int index = 0; index < size; index++) {
            double fraction = size <= 1 ? 0.5 : (double) index / (size - 1);
            double angle = index * GOLDEN_ANGLE;
            double radial = size <= 1 ? 0 : 0.52 + 0.38 * Math.sin(Math.PI * fraction);
            double x = Math.cos(angle) * MAX_X * radial;
            double y = Math.sin(angle) * MAX_Y * radial;
            double z = size <= 1 ? 0 : -MAX_Z + 2 * MAX_Z * fraction;
            result.put(ordered.get(index).id(), new Vector3(x, y, z));
        }
        return Map.copyOf(result);
    }
}
