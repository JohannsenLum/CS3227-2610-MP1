package constella.application;

import java.util.Objects;
import java.util.UUID;

/** Deterministic, bounded motion mathematics for the 3D space renderer. */
public final class SpaceMotion {
    private static final double TWO_PI = Math.PI * 2;

    private SpaceMotion() { }

    public static double phase(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        long mixed = id.getMostSignificantBits() ^ Long.rotateLeft(id.getLeastSignificantBits(), 21);
        return (mixed & Long.MAX_VALUE) / (double) Long.MAX_VALUE;
    }

    public static double pulse(UUID id, double elapsedSeconds, double amplitude) {
        if (!Double.isFinite(elapsedSeconds)) {
            throw new IllegalArgumentException("elapsedSeconds must be finite");
        }
        if (!Double.isFinite(amplitude) || amplitude < 0 || amplitude > 0.25) {
            throw new IllegalArgumentException("amplitude must be between 0 and 0.25");
        }
        return 1 + amplitude * Math.sin(TWO_PI * (phase(id) + elapsedSeconds / 3.8));
    }

    public static double edgeProgress(UUID firstId, UUID secondId, double elapsedSeconds) {
        if (!Double.isFinite(elapsedSeconds)) {
            throw new IllegalArgumentException("elapsedSeconds must be finite");
        }
        double offset = (phase(firstId) + phase(secondId)) * 0.5;
        double progress = offset + elapsedSeconds / 7.5;
        return progress - Math.floor(progress);
    }

    public static Vector3 interpolate(Vector3 start, Vector3 end, double progress) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!Double.isFinite(progress) || progress < 0 || progress > 1) {
            throw new IllegalArgumentException("progress must be between 0 and 1");
        }
        return start.scale(1 - progress).add(end.scale(progress));
    }
}
