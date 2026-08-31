package constella.application;

/** Bounded, UI-independent orbit-camera state. */
public final class CameraState {
    public static final double MIN_PITCH = -75;
    public static final double MAX_PITCH = 75;
    public static final double MIN_DISTANCE = -1800;
    public static final double MAX_DISTANCE = -380;
    public static final double MAX_PAN = 420;
    private double pitch = -12;
    private double yaw = -18;
    private double distance = -720;
    private double panX;
    private double panY;

    public double pitch() {
        return pitch;
    }

    public double yaw() {
        return yaw;
    }

    public double distance() {
        return distance;
    }

    public double panX() {
        return panX;
    }

    public double panY() {
        return panY;
    }

    public void rotateBy(double pitchDelta, double yawDelta) {
        requireFinite(pitchDelta, yawDelta);
        pitch = clamp(pitch + pitchDelta, MIN_PITCH, MAX_PITCH);
        yaw = normalizeYaw(yaw + yawDelta);
    }

    public void zoomBy(double delta) {
        requireFinite(delta);
        distance = clamp(distance + delta, MIN_DISTANCE, MAX_DISTANCE);
    }

    public void panBy(double deltaX, double deltaY) {
        requireFinite(deltaX, deltaY);
        panX = clamp(panX + deltaX, -MAX_PAN, MAX_PAN);
        panY = clamp(panY + deltaY, -MAX_PAN, MAX_PAN);
    }

    public void focus(Vector3 position) {
        panX = clamp(position.x(), -MAX_PAN, MAX_PAN);
        panY = clamp(position.y(), -MAX_PAN, MAX_PAN);
        distance = clamp(position.z() - 650, MIN_DISTANCE, MAX_DISTANCE);
    }

    public void reset() {
        pitch = -12;
        yaw = -18;
        distance = -720;
        panX = 0;
        panY = 0;
    }

    private static void requireFinite(double... values) {
        for (double value : values) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("camera values must be finite");
            }
        }
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static double normalizeYaw(double value) {
        double normalized = (value + 180) % 360;
        if (normalized < 0) {
            normalized += 360;
        }
        return normalized - 180;
    }
}
