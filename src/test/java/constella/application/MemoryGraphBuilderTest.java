package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MemoryGraphBuilderTest {
    @Test
    void emptyAndOneNodeGraphsAreValid() {
        assertTrue(MemoryGraphBuilder.build(List.of(), List.of()).memories().isEmpty());
        Memory only = memory(UUID.randomUUID(), "Only", 2025, 1);

        MemoryGraph graph = MemoryGraphBuilder.build(List.of(only), List.of());

        assertEquals(Vector3.ZERO, graph.positions().get(only.id()));
        assertTrue(graph.edges().isEmpty());
    }

    @Test
    void constellationUsesSparseChronologicalPath() {
        Memory newest = memory(UUID.randomUUID(), "Newest", 2025, 3);
        Memory oldest = memory(UUID.randomUUID(), "Oldest", 2025, 1);
        Memory middle = memory(UUID.randomUUID(), "Middle", 2025, 2);
        Constellation group = new Constellation(UUID.randomUUID(), "Group", null,
                List.of(newest.id(), oldest.id(), middle.id()));

        MemoryGraph graph = MemoryGraphBuilder.build(List.of(newest, oldest, middle), List.of(group));

        assertEquals(2, graph.edges().size());
        assertTrue(hasEdge(graph, oldest.id(), middle.id()));
        assertTrue(hasEdge(graph, middle.id(), newest.id()));
        assertFalse(hasEdge(graph, oldest.id(), newest.id()));
    }

    @Test
    void overlappingConstellationsDeduplicateSameEdgeAndRetainMemberships() {
        Memory first = memory(UUID.randomUUID(), "First", 2025, 1);
        Memory second = memory(UUID.randomUUID(), "Second", 2025, 2);
        UUID firstGroup = UUID.randomUUID();
        UUID secondGroup = UUID.randomUUID();
        List<Constellation> groups = List.of(
                new Constellation(firstGroup, "One", null, List.of(first.id(), second.id())),
                new Constellation(secondGroup, "Two", null, List.of(second.id(), first.id())));

        MemoryGraph graph = MemoryGraphBuilder.build(List.of(first, second), groups);

        assertEquals(1, graph.edges().size());
        assertEquals(Set.of(firstGroup, secondGroup), graph.edges().getFirst().constellationIds());
        assertEquals(Set.of(second.id()), graph.neighbors(first.id()));
    }

    @Test
    void layoutIsDeterministicFiniteBoundedAndHasMeaningfulDepth() {
        List<Memory> memories = IntStream.range(0, 16)
                .mapToObj(index -> memory(new UUID(0, index + 1), "M" + index, 2020 + index / 4, index % 28 + 1))
                .toList();

        MemoryGraph first = MemoryGraphBuilder.build(memories, List.of());
        MemoryGraph second = MemoryGraphBuilder.build(new ArrayList<>(memories.reversed()), List.of());

        assertEquals(first.positions(), second.positions());
        assertTrue(first.positions().values().stream().allMatch(position ->
                Double.isFinite(position.x()) && Double.isFinite(position.y()) && Double.isFinite(position.z())
                        && Math.abs(position.x()) <= Memory3DLayout.MAX_X
                        && Math.abs(position.y()) <= Memory3DLayout.MAX_Y
                        && Math.abs(position.z()) <= Memory3DLayout.MAX_Z));
        assertTrue(first.positions().values().stream().map(Vector3::z).distinct().count() > 2);
    }

    @Test
    void oneHundredNodeGraphStaysSparseAndBounded() {
        List<Memory> memories = IntStream.range(0, 100)
                .mapToObj(index -> memory(new UUID(1, index + 1), "M" + index, 2020 + index / 25, index % 28 + 1))
                .toList();
        Constellation group = new Constellation(UUID.randomUUID(), "All", null,
                memories.stream().map(Memory::id).toList());

        MemoryGraph graph = MemoryGraphBuilder.build(memories, List.of(group));

        assertEquals(100, graph.memories().size());
        assertEquals(99, graph.edges().size());
        assertEquals(100, graph.positions().size());
    }

    private static boolean hasEdge(MemoryGraph graph, UUID first, UUID second) {
        return graph.edges().stream().anyMatch(edge -> edge.touches(first) && edge.touches(second));
    }

    private static Memory memory(UUID id, String title, int year, int day) {
        return new Memory(id, title, LocalDate.of(year, 1, day), null, Mood.NEUTRAL, 3,
                List.of(), List.of(), null);
    }
}
