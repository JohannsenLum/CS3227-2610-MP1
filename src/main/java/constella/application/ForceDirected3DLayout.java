package constella.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Deterministic fixed-step 3D force solver with no ongoing simulation state. */
public final class ForceDirected3DLayout {
    public static final double MAX_X = 390;
    public static final double MAX_Y = 270;
    public static final double MAX_Z = 310;
    public static final int MAX_ITERATIONS = 260;
    private static final double REPULSION = 5_800;
    private static final double EDGE_LENGTH = 74;
    private static final double ATTRACTION = 0.012;
    private static final double GRAVITY = 0.006;
    private static final double DAMPING = 0.82;
    private static final double MAX_VELOCITY = 7.0;
    private static final double MIN_SEPARATION = 18;

    private ForceDirected3DLayout() {
    }

    public static Map<UUID, Vector3> layout(MemoryGraph graph) {
        List<UUID> nodeIds = graph.memories().keySet().stream()
                .sorted(Comparator.comparing(UUID::toString)).toList();
        int size = nodeIds.size();
        if (size == 0) {
            return Map.of();
        }
        double[][] position = new double[size][3];
        double[][] velocity = new double[size][3];
        Map<UUID, Integer> indices = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            indices.put(nodeIds.get(index), index);
            position[index] = initialPosition(nodeIds.get(index), size);
        }
        List<int[]> edges = new ArrayList<>();
        for (MemoryGraphEdge edge : graph.edges()) {
            edges.add(new int[] {indices.get(edge.firstId()), indices.get(edge.secondId())});
        }
        double[][] anchors = componentAnchors(size, edges);
        for (int index = 0; index < size; index++) {
            for (int axis = 0; axis < 3; axis++) {
                position[index][axis] += anchors[index][axis] * 0.65;
            }
        }

        int stableIterations = 0;
        for (int iteration = 0; iteration < MAX_ITERATIONS && stableIterations < 14; iteration++) {
            double[][] force = new double[size][3];
            applyPairForces(position, force);
            applyEdgeForces(position, force, edges);
            double maxMovement = integrate(position, velocity, force, anchors);
            stableIterations = maxMovement < 0.018 ? stableIterations + 1 : 0;
        }
        LinkedHashMap<UUID, Vector3> result = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            result.put(nodeIds.get(index), new Vector3(position[index][0], position[index][1], position[index][2]));
        }
        assert result.values().stream().allMatch(ForceDirected3DLayout::isFiniteAndBounded);
        return Map.copyOf(result);
    }

    private static boolean isFiniteAndBounded(Vector3 position) {
        return Double.isFinite(position.x()) && Double.isFinite(position.y()) && Double.isFinite(position.z())
                && Math.abs(position.x()) <= MAX_X && Math.abs(position.y()) <= MAX_Y
                && Math.abs(position.z()) <= MAX_Z;
    }

    private static void applyPairForces(double[][] positions, double[][] forces) {
        for (int first = 0; first < positions.length; first++) {
            for (int second = first + 1; second < positions.length; second++) {
                double dx = positions[first][0] - positions[second][0];
                double dy = positions[first][1] - positions[second][1];
                double dz = positions[first][2] - positions[second][2];
                double distanceSquared = dx * dx + dy * dy + dz * dz;
                if (distanceSquared < 1.0e-8) {
                    dx = 0.01 * (first + 1);
                    dy = -0.013 * (second + 1);
                    dz = 0.017;
                    distanceSquared = dx * dx + dy * dy + dz * dz;
                }
                double distance = Math.sqrt(distanceSquared);
                double magnitude = REPULSION / distanceSquared;
                if (distance < MIN_SEPARATION) {
                    magnitude += (MIN_SEPARATION - distance) * 1.8;
                }
                double scale = magnitude / distance;
                addOpposite(forces, first, second, dx * scale, dy * scale, dz * scale);
            }
        }
    }

    private static void applyEdgeForces(double[][] positions, double[][] forces, List<int[]> edges) {
        for (int[] edge : edges) {
            int first = edge[0];
            int second = edge[1];
            double dx = positions[second][0] - positions[first][0];
            double dy = positions[second][1] - positions[first][1];
            double dz = positions[second][2] - positions[first][2];
            double distance = Math.max(0.0001, Math.sqrt(dx * dx + dy * dy + dz * dz));
            double scale = ATTRACTION * (distance - EDGE_LENGTH) / distance;
            addOpposite(forces, first, second, dx * scale, dy * scale, dz * scale);
        }
    }

    private static double integrate(double[][] positions, double[][] velocities, double[][] forces, double[][] anchors) {
        double maxMovement = 0;
        for (int index = 0; index < positions.length; index++) {
            for (int axis = 0; axis < 3; axis++) {
                forces[index][axis] -= (positions[index][axis] - anchors[index][axis]) * GRAVITY;
                velocities[index][axis] = (velocities[index][axis] + forces[index][axis]) * DAMPING;
            }
            double speed = magnitude(velocities[index]);
            if (speed > MAX_VELOCITY) {
                double scale = MAX_VELOCITY / speed;
                for (int axis = 0; axis < 3; axis++) {
                    velocities[index][axis] *= scale;
                }
                speed = MAX_VELOCITY;
            }
            maxMovement = Math.max(maxMovement, speed);
            positions[index][0] = bounded(positions[index][0] + velocities[index][0], MAX_X);
            positions[index][1] = bounded(positions[index][1] + velocities[index][1], MAX_Y);
            positions[index][2] = bounded(positions[index][2] + velocities[index][2], MAX_Z);
        }
        return maxMovement;
    }

    private static double[][] componentAnchors(int size, List<int[]> edges) {
        int[] parent = new int[size];
        for (int index = 0; index < size; index++) {
            parent[index] = index;
        }
        for (int[] edge : edges) {
            union(parent, edge[0], edge[1]);
        }
        Map<Integer, Integer> componentOrder = new LinkedHashMap<>();
        for (int index = 0; index < size; index++) {
            componentOrder.putIfAbsent(find(parent, index), componentOrder.size());
        }
        double[][] anchors = new double[size][3];
        int components = componentOrder.size();
        if (components == 1) {
            return anchors;
        }
        double radius = Math.min(220, 75 + components * 18);
        for (int index = 0; index < size; index++) {
            int component = componentOrder.get(find(parent, index));
            double angle = 2 * Math.PI * component / components;
            anchors[index][0] = Math.cos(angle) * radius;
            anchors[index][1] = Math.sin(angle) * radius * 0.7;
            anchors[index][2] = ((component % 3) - 1) * Math.min(90, radius * 0.45);
        }
        return anchors;
    }

    private static int find(int[] parent, int value) {
        while (parent[value] != value) {
            parent[value] = parent[parent[value]];
            value = parent[value];
        }
        return value;
    }

    private static void union(int[] parent, int first, int second) {
        int firstRoot = find(parent, first);
        int secondRoot = find(parent, second);
        if (firstRoot == secondRoot) {
            return;
        }
        if (firstRoot < secondRoot) {
            parent[secondRoot] = firstRoot;
        } else {
            parent[firstRoot] = secondRoot;
        }
    }

    private static void addOpposite(double[][] forces, int positive, int negative,
            double x, double y, double z) {
        forces[positive][0] += x;
        forces[positive][1] += y;
        forces[positive][2] += z;
        forces[negative][0] -= x;
        forces[negative][1] -= y;
        forces[negative][2] -= z;
    }

    private static double[] initialPosition(UUID id, int size) {
        long seed = mix(id.getMostSignificantBits() ^ Long.rotateLeft(id.getLeastSignificantBits(), 23));
        double spread = Math.min(230, 75 + Math.cbrt(size) * 31);
        return new double[] {
            signed(seed) * spread,
            signed(mix(seed)) * spread * 0.72,
            signed(mix(mix(seed))) * spread * 0.82
        };
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xbf58476d1ce4e5b9L;
        value ^= value >>> 27;
        value *= 0x94d049bb133111ebL;
        return value ^ value >>> 31;
    }

    private static double signed(long value) {
        return ((value >>> 11) * 0x1.0p-53) * 2 - 1;
    }

    private static double magnitude(double[] vector) {
        return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    }

    private static double bounded(double value, double limit) {
        return Math.max(-limit, Math.min(limit, value));
    }
}
