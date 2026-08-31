package constella.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CameraStateTest {
    @Test
    void rotationZoomAndPanAreBounded() {
        CameraState camera = new CameraState();

        camera.rotateBy(1000, -1000);
        camera.zoomBy(10_000);
        camera.panBy(10_000, -10_000);

        assertEquals(CameraState.MAX_PITCH, camera.pitch());
        assertTrue(camera.yaw() >= -180 && camera.yaw() < 180);
        assertEquals(CameraState.MAX_DISTANCE, camera.distance());
        assertEquals(CameraState.MAX_PAN, camera.panX());
        assertEquals(-CameraState.MAX_PAN, camera.panY());
        camera.zoomBy(-10_000);
        assertEquals(CameraState.MIN_DISTANCE, camera.distance());
    }

    @Test
    void yawWrapsAndContinuesRotatingPastEitherBoundary() {
        CameraState camera = new CameraState();

        camera.rotateBy(0, 200);
        assertEquals(-178, camera.yaw());
        camera.rotateBy(0, 20);
        assertEquals(-158, camera.yaw());
        camera.rotateBy(0, -400);
        assertEquals(162, camera.yaw());
    }

    @Test
    void focusAndResetRemainRecoverable() {
        CameraState camera = new CameraState();
        camera.focus(new Vector3(200, -100, 250));
        assertEquals(200, camera.panX());
        assertEquals(-100, camera.panY());
        assertEquals(-400, camera.distance());

        camera.reset();

        assertEquals(-12, camera.pitch());
        assertEquals(-18, camera.yaw());
        assertEquals(-720, camera.distance());
        assertEquals(0, camera.panX());
        assertThrows(IllegalArgumentException.class, () -> camera.zoomBy(Double.NaN));
    }
}
