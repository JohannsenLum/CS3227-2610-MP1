package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConnectionGeometryTest {
    @Test
    void calculatesMidpointLengthAndOrientationFromYAxis() {
        ConnectionGeometry alongX = ConnectionGeometry.between(new Vector3(0, 0, 0), new Vector3(10, 0, 0));

        assertEquals(new Vector3(5, 0, 0), alongX.midpoint());
        assertEquals(10, alongX.length());
        assertEquals(90, alongX.angleDegrees(), 0.000001);
        assertEquals(new Vector3(0, 0, -1), alongX.rotationAxis());

        ConnectionGeometry alongY = ConnectionGeometry.between(Vector3.ZERO, new Vector3(0, 5, 0));
        assertEquals(0, alongY.angleDegrees(), 0.000001);
    }

    @Test
    void zeroLengthConnectionHasStableFiniteFallback() {
        Vector3 point = new Vector3(2, 3, 4);

        ConnectionGeometry geometry = ConnectionGeometry.between(point, point);

        assertEquals(point, geometry.midpoint());
        assertEquals(0, geometry.length());
        assertEquals(0, geometry.angleDegrees());
        assertEquals(new Vector3(1, 0, 0), geometry.rotationAxis());
    }
}
