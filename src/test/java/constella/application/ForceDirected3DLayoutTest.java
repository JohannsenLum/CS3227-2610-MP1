package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class ForceDirected3DLayoutTest {
    @Test
    void realMemoryGraphLayoutIsDeterministicFiniteBoundedSeparatedAndDeep() {
        List<Memory> memories = IntStream.range(0, 18).mapToObj(ForceDirected3DLayoutTest::memory).toList();
        Constellation path = new Constellation(UUID.randomUUID(), "Path", null,
                memories.stream().map(Memory::id).toList());
        MemoryGraph graph = MemoryGraphBuilder.build(memories, List.of(path));

        Map<UUID, Vector3> first = ForceDirected3DLayout.layout(graph);
        Map<UUID, Vector3> second = ForceDirected3DLayout.layout(graph);

        assertEquals(first, second);
        assertTrue(first.values().stream().allMatch(position -> Double.isFinite(position.x())
                && Double.isFinite(position.y()) && Double.isFinite(position.z())
                && Math.abs(position.x()) <= ForceDirected3DLayout.MAX_X
                && Math.abs(position.y()) <= ForceDirected3DLayout.MAX_Y
                && Math.abs(position.z()) <= ForceDirected3DLayout.MAX_Z));
        assertTrue(depth(first) > 70);
        assertTrue(minimumDistance(first) > 5);
    }

    @Test
    void emptyAndOneMemoryGraphsRemainValid() {
        assertTrue(ForceDirected3DLayout.layout(MemoryGraphBuilder.build(List.of(), List.of())).isEmpty());
        Memory only = memory(0);

        Map<UUID, Vector3> positions = ForceDirected3DLayout.layout(
                MemoryGraphBuilder.build(List.of(only), List.of()));

        assertEquals(Set.of(only.id()), positions.keySet());
        assertTrue(positions.get(only.id()).length() < 600);
    }

    @Test
    void disconnectedMemoriesRemainSeparatedInsideInitialViewBounds() {
        List<Memory> memories = List.of(memory(1), memory(2), memory(3), memory(4));
        MemoryGraph graph = MemoryGraphBuilder.build(memories, List.of());

        Map<UUID, Vector3> positions = ForceDirected3DLayout.layout(graph);

        assertTrue(minimumDistance(positions) > 20);
        assertTrue(positions.values().stream().allMatch(position -> position.length() < 600));
    }

    @Test
    void oneHundredMemoryGraphSettlesWithOnlyRealNodes() {
        List<Memory> memories = IntStream.range(0, 100).mapToObj(ForceDirected3DLayoutTest::memory).toList();
        Constellation path = new Constellation(UUID.randomUUID(), "All", null,
                memories.stream().map(Memory::id).toList());
        MemoryGraph graph = MemoryGraphBuilder.build(memories, List.of(path));

        Map<UUID, Vector3> positions = ForceDirected3DLayout.layout(graph);

        assertEquals(100, positions.size());
        assertEquals(99, graph.edges().size());
        assertTrue(positions.values().stream().allMatch(position -> Math.abs(position.x()) <= ForceDirected3DLayout.MAX_X
                && Math.abs(position.y()) <= ForceDirected3DLayout.MAX_Y
                && Math.abs(position.z()) <= ForceDirected3DLayout.MAX_Z));
    }

    private static double depth(Map<UUID, Vector3> positions) {
        return positions.values().stream().map(Vector3::z).max(Double::compare).orElseThrow()
                - positions.values().stream().map(Vector3::z).min(Double::compare).orElseThrow();
    }

    private static double minimumDistance(Map<UUID, Vector3> positions) {
        List<Vector3> values = new ArrayList<>(positions.values());
        double minimum = Double.POSITIVE_INFINITY;
        for (int first = 0; first < values.size(); first++) {
            for (int second = first + 1; second < values.size(); second++) {
                minimum = Math.min(minimum, values.get(first).subtract(values.get(second)).length());
            }
        }
        return minimum;
    }

    private static Memory memory(int index) {
        return new Memory(new UUID(42, index + 1), "Memory " + index,
                LocalDate.of(2025, 1, index % 28 + 1), null, Mood.NEUTRAL, index % 5 + 1,
                List.of(), List.of(), null);
    }
}
