package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemoryGraphRenderPlanTest {
    @Test
    void renderPlanExactlyMatchesVisibleMemoryGraphWithoutExtraNodesOrEdges() {
        Memory first = memory("First", 1);
        Memory second = memory("Second", 2);
        Memory hidden = memory("Hidden", 3);
        Constellation constellation = new Constellation(UUID.randomUUID(), "Path", null,
                List.of(first.id(), second.id(), hidden.id()));
        MemoryGraph graph = MemoryGraphBuilder.build(List.of(first, second), List.of(constellation));

        MemoryGraphRenderPlan plan = MemoryGraphRenderPlan.from(graph);

        assertEquals(graph.memories().keySet(), plan.nodes().stream().map(Memory::id)
                .collect(java.util.stream.Collectors.toSet()));
        assertEquals(graph.edges(), plan.edges());
        assertEquals(2, plan.nodes().size());
        assertEquals(1, plan.edges().size());
        assertTrue(plan.edges().stream().allMatch(edge -> graph.memories().containsKey(edge.firstId())
                && graph.memories().containsKey(edge.secondId())));
    }

    @Test
    void invalidEndpointCannotEnterRenderPlan() {
        Memory memory = memory("Only", 1);
        MemoryGraphEdge invalid = new MemoryGraphEdge(memory.id(), UUID.randomUUID(), Set.of());

        assertThrows(IllegalArgumentException.class,
                () -> new MemoryGraphRenderPlan(List.of(memory), List.of(invalid)));
    }

    private static Memory memory(String title, int day) {
        return new Memory(UUID.randomUUID(), title, LocalDate.of(2025, 1, day), null,
                Mood.NEUTRAL, 3, List.of(), List.of(), null);
    }
}
