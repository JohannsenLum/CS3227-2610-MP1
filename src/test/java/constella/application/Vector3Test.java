package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class Vector3Test {
    @Test
    void supportsCoreVectorOperations() {
        Vector3 first = new Vector3(1, 2, 3);
        Vector3 second = new Vector3(4, 6, 3);

        assertEquals(new Vector3(5, 8, 6), first.add(second));
        assertEquals(new Vector3(-3, -4, 0), first.subtract(second));
        assertEquals(new Vector3(2, 4, 6), first.scale(2));
        assertEquals(5, first.subtract(second).length());
        assertEquals(new Vector3(2.5, 4, 3), first.midpoint(second));
        assertEquals(1, first.normalized().length(), 0.000001);
        assertEquals(Vector3.ZERO, Vector3.ZERO.normalized());
    }

    @Test
    void rejectsNonFiniteCoordinatesAndScale() {
        assertThrows(IllegalArgumentException.class, () -> new Vector3(Double.NaN, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Vector3.ZERO.scale(Double.POSITIVE_INFINITY));
    }
}
