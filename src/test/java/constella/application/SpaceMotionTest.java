package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SpaceMotionTest {
    private static final UUID FIRST = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SECOND = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    void phasePulseAndEdgeProgressAreDeterministicAndBounded() {
        assertEquals(SpaceMotion.phase(FIRST), SpaceMotion.phase(FIRST));
        assertTrue(SpaceMotion.phase(FIRST) >= 0 && SpaceMotion.phase(FIRST) <= 1);
        for (int second = 0; second < 30; second++) {
            double pulse = SpaceMotion.pulse(FIRST, second, 0.08);
            assertTrue(pulse >= 0.92 && pulse <= 1.08);
            double progress = SpaceMotion.edgeProgress(FIRST, SECOND, second);
            assertTrue(progress >= 0 && progress < 1);
        }
    }

    @Test
    void interpolationFollowsConnectionAndRejectsInvalidProgress() {
        Vector3 start = new Vector3(-10, 20, -30);
        Vector3 end = new Vector3(30, -20, 10);

        assertEquals(start, SpaceMotion.interpolate(start, end, 0));
        assertEquals(end, SpaceMotion.interpolate(start, end, 1));
        assertEquals(new Vector3(10, 0, -10), SpaceMotion.interpolate(start, end, 0.5));
        assertThrows(IllegalArgumentException.class, () -> SpaceMotion.interpolate(start, end, 1.01));
    }
}
