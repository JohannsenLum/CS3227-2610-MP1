package constella.application;

/** An immutable, UI-independent finite vector in three-dimensional space. */
public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);
    public static final Vector3 UNIT_Y = new Vector3(0, 1, 0);

    public Vector3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("vector coordinates must be finite");
        }
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 scale(double factor) {
        if (!Double.isFinite(factor)) {
            throw new IllegalArgumentException("scale factor must be finite");
        }
        return new Vector3(x * factor, y * factor, z * factor);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    public Vector3 normalized() {
        double magnitude = length();
        return magnitude == 0 ? ZERO : scale(1 / magnitude);
    }

    public Vector3 midpoint(Vector3 other) {
        return add(other).scale(0.5);
    }
}
