package constella.application;

/** UI-independent placement and orientation for a cylinder whose local axis is positive Y. */
public record ConnectionGeometry(Vector3 midpoint, double length, Vector3 rotationAxis, double angleDegrees) {
    private static final double EPSILON = 1.0e-9;

    public ConnectionGeometry {
        if (length < 0 || !Double.isFinite(length) || !Double.isFinite(angleDegrees)) {
            throw new IllegalArgumentException("connection length and angle must be finite and non-negative");
        }
    }

    public static ConnectionGeometry between(Vector3 from, Vector3 to) {
        Vector3 direction = to.subtract(from);
        double length = direction.length();
        if (length < EPSILON) {
            return new ConnectionGeometry(from.midpoint(to), 0, new Vector3(1, 0, 0), 0);
        }
        Vector3 unit = direction.scale(1 / length);
        double cosine = Math.max(-1, Math.min(1, Vector3.UNIT_Y.dot(unit)));
        Vector3 axis = Vector3.UNIT_Y.cross(unit);
        if (axis.length() < EPSILON) {
            axis = new Vector3(1, 0, 0);
        } else {
            axis = axis.normalized();
        }
        return new ConnectionGeometry(from.midpoint(to), length, axis, Math.toDegrees(Math.acos(cosine)));
    }
}
