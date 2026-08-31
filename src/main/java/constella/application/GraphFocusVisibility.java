package constella.application;

import java.util.UUID;

/** Exact edge-visibility rule shared by 3D focus and its motion layer. */
public final class GraphFocusVisibility {
    private GraphFocusVisibility() { }

    public static boolean shows(MemoryGraphEdge edge, UUID memoryFocus, UUID constellationFocus) {
        if (memoryFocus != null) {
            return edge.touches(memoryFocus);
        }
        return constellationFocus == null || edge.constellationIds().contains(constellationFocus);
    }
}
