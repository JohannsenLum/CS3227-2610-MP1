package constella.model;

/** A UI-independent normalized position in the sky, where each coordinate is from 0 to 1. */
public record StarPosition(double x, double y) {
    public StarPosition {
        if (!Double.isFinite(x) || x < 0 || x > 1) {
            throw new IllegalArgumentException("x must be a finite value between 0 and 1");
        }
        if (!Double.isFinite(y) || y < 0 || y > 1) {
            throw new IllegalArgumentException("y must be a finite value between 0 and 1");
        }
    }
}
