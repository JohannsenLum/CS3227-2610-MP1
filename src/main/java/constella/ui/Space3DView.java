package constella.ui;

import constella.application.CameraState;
import constella.application.ConnectionGeometry;
import constella.application.ForceDirected3DLayout;
import constella.application.GraphSelectionState;
import constella.application.GraphFocusVisibility;
import constella.application.MemoryGraph;
import constella.application.MemoryGraphBuilder;
import constella.application.MemoryGraphEdge;
import constella.application.MemoryGraphRenderPlan;
import constella.application.SpaceMotion;
import constella.application.Vector3;
import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/** Genuine JavaFX 3D renderer for exactly the visible memory graph. */
final class Space3DView extends StackPane {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM uuuu");
    private static final List<Color> CONSTELLATION_COLORS = List.of(
            Color.web("#7699e8"), Color.web("#d49a58"), Color.web("#8e72cc"),
            Color.web("#52aa9b"), Color.web("#cf6f83"), Color.web("#9aaa62"));

    private final Consumer<Memory> onSelected;
    private final CameraState cameraState = new CameraState();
    private final GraphSelectionState selection = new GraphSelectionState();
    private final Group starfield = new Group();
    private final Group geometry = new Group();
    private final Rotate pitch = new Rotate(0, Rotate.X_AXIS);
    private final Rotate yaw = new Rotate(0, Rotate.Y_AXIS);
    private final Scale settleScale = new Scale(1, 1, 1);
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Pane labelLayer = new Pane();
    private final Map<UUID, NodeView> nodeViews = new HashMap<>();
    private final List<EdgeView> edgeViews = new ArrayList<>();
    private final List<BackgroundStar> backgroundStars = new ArrayList<>();
    private final Map<UUID, Vector3> positions = new HashMap<>();
    private final Map<UUID, Color> constellationColors = new HashMap<>();
    private MemoryGraph graph = MemoryGraphBuilder.build(List.of(), List.of());
    private Constellation focusedConstellation;
    private SubScene subScene;
    private Timeline settleAnimation;
    private double pointerX;
    private double pointerY;
    private boolean cameraDragging;
    private boolean panning;
    private boolean connectionsVisible = true;
    private boolean motionEnabled = true;
    private long previousMotionNanos;
    private long previousRotationNanos;
    private long previousLabelProjectionNanos;
    private final AnimationTimer autoRotation = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (previousRotationNanos != 0) {
                double elapsedSeconds = Math.min(0.05, (now - previousRotationNanos) / 1_000_000_000.0);
                cameraState.rotateBy(0, elapsedSeconds * 10);
                applyCamera(false);
                if (now - previousLabelProjectionNanos >= 66_000_000) {
                    updateLabels();
                    previousLabelProjectionNanos = now;
                }
            }
            previousRotationNanos = now;
        }
    };
    private final AnimationTimer spaceMotion = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (previousMotionNanos != 0 && now - previousMotionNanos < 33_000_000) {
                return;
            }
            previousMotionNanos = now;
            animateSpace(now / 1_000_000_000.0);
        }
    };

    Space3DView(Consumer<Memory> onSelected) {
        this.onSelected = onSelected;
        getStyleClass().add("space-3d-frame");
        setMinSize(520, 400);
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            showUnsupported("JavaFX 3D is unavailable on this graphics environment. Timeline remains available.");
            return;
        }
        try {
            initialize3D();
        } catch (RuntimeException exception) {
            showUnsupported("JavaFX 3D could not initialize. Use Timeline instead.");
        }
    }

    void show(List<Memory> memories, List<Constellation> constellations, Constellation constellation) {
        focusedConstellation = constellation;
        graph = MemoryGraphBuilder.build(memories, constellations);
        constellationColors.clear();
        List<Constellation> orderedConstellations = constellations.stream()
                .sorted(Comparator.comparing(Constellation::name).thenComparing(Constellation::id)).toList();
        for (int index = 0; index < orderedConstellations.size(); index++) {
            constellationColors.put(orderedConstellations.get(index).id(),
                    CONSTELLATION_COLORS.get(index % CONSTELLATION_COLORS.size()));
        }
        positions.clear();
        positions.putAll(ForceDirected3DLayout.layout(graph));
        if (selection.selectedId().isPresent() && !graph.memories().containsKey(selection.selectedId().orElseThrow())) {
            selection.clearSelection();
            onSelected.accept(null);
        }
        if (subScene != null) {
            rebuildGeometry();
        }
    }

    void focusConstellation(Constellation constellation) {
        selection.clearHover();
        selection.clearSelection();
        onSelected.accept(null);
        focusedConstellation = constellation;
        applyHighlight(null);
    }

    void resetCamera() {
        cameraState.reset();
        applyCamera();
    }

    void focusSelected() {
        selection.selectedId().map(positions::get).ifPresent(position -> {
            cameraState.focus(position);
            applyCamera();
        });
    }

    void setConnectionsVisible(boolean visible) {
        connectionsVisible = visible;
        edgeViews.forEach(edge -> {
            edge.cylinder().setVisible(visible && isEdgeActive(edge.edge()));
            edge.traveler().setVisible(visible && motionEnabled && isEdgeActive(edge.edge()));
        });
    }

    void setMotionEnabled(boolean enabled) {
        motionEnabled = enabled;
        previousMotionNanos = 0;
        if (enabled && subScene != null) {
            spaceMotion.start();
        } else {
            spaceMotion.stop();
            edgeViews.forEach(edge -> edge.traveler().setVisible(false));
            applyHighlight(selection.focusId().orElse(null));
        }
    }

    void setAutoRotate(boolean enabled) {
        if (enabled && subScene != null) {
            previousRotationNanos = 0;
            autoRotation.start();
        } else {
            autoRotation.stop();
            previousRotationNanos = 0;
        }
    }

    void zoomIn() {
        cameraState.zoomBy(110);
        applyCamera();
    }

    void zoomOut() {
        cameraState.zoomBy(-110);
        applyCamera();
    }

    private void initialize3D() {
        geometry.getTransforms().addAll(yaw, pitch, settleScale);
        createStarfield();
        Group root3D = new Group(starfield, geometry);
        AmbientLight ambient = new AmbientLight(Color.rgb(74, 80, 98));
        PointLight key = new PointLight(Color.rgb(190, 204, 226));
        key.setTranslateX(-330);
        key.setTranslateY(-250);
        key.setTranslateZ(-520);
        root3D.getChildren().addAll(ambient, key);

        subScene = new SubScene(root3D, 800, 520, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(3, 5, 11));
        subScene.widthProperty().bind(widthProperty());
        subScene.heightProperty().bind(heightProperty());
        camera.setNearClip(0.5);
        camera.setFarClip(4500);
        camera.setFieldOfView(43);
        subScene.setCamera(camera);
        labelLayer.setMouseTransparent(true);
        labelLayer.getStyleClass().add("space-label-layer");
        labelLayer.prefWidthProperty().bind(widthProperty());
        labelLayer.prefHeightProperty().bind(heightProperty());
        getChildren().addAll(subScene, labelLayer);
        configureCameraInput();
        applyCamera();
    }

    private void showUnsupported(String message) {
        Label fallback = new Label(message);
        fallback.setWrapText(true);
        fallback.getStyleClass().add("space-3d-fallback");
        getChildren().setAll(fallback);
    }

    private void rebuildGeometry() {
        if (settleAnimation != null) {
            settleAnimation.stop();
        }
        geometry.getChildren().clear();
        labelLayer.getChildren().clear();
        nodeViews.clear();
        edgeViews.clear();
        MemoryGraphRenderPlan plan = MemoryGraphRenderPlan.from(graph);
        if (plan.nodes().isEmpty()) {
            Label empty = new Label("No memories match the current filters.");
            empty.getStyleClass().add("space-3d-empty");
            empty.layoutXProperty().bind(labelLayer.widthProperty().subtract(empty.widthProperty()).divide(2));
            empty.layoutYProperty().bind(labelLayer.heightProperty().subtract(empty.heightProperty()).divide(2));
            labelLayer.getChildren().add(empty);
            return;
        }
        for (MemoryGraphEdge edge : plan.edges()) {
            ConnectionGeometry placement = ConnectionGeometry.between(
                    positions.get(edge.firstId()), positions.get(edge.secondId()));
            Cylinder cylinder = new Cylinder(0.24, placement.length(), 6);
            Color base = edgeColor(edge);
            PhongMaterial material = new PhongMaterial(base);
            cylinder.setMaterial(material);
            cylinder.setMouseTransparent(true);
            cylinder.setCullFace(CullFace.NONE);
            placeCylinder(cylinder, placement);
            Sphere traveler = new Sphere(0.72, 6);
            PhongMaterial travelerMaterial = new PhongMaterial(base.brighter());
            travelerMaterial.setSpecularColor(Color.WHITE);
            traveler.setMaterial(travelerMaterial);
            traveler.setMouseTransparent(true);
            traveler.setVisible(connectionsVisible && motionEnabled);
            placeSphere(traveler, positions.get(edge.firstId()));
            edgeViews.add(new EdgeView(edge, cylinder, material, base, traveler));
            cylinder.setVisible(connectionsVisible);
            geometry.getChildren().addAll(cylinder, traveler);
        }
        for (Memory memory : plan.nodes()) {
            Color base = depthAdjusted(memoryColor(memory.mood()), positions.get(memory.id()).z());
            PhongMaterial material = new PhongMaterial(base);
            material.setSpecularColor(base.brighter());
            material.setSpecularPower(11);
            Sphere sphere = new Sphere(2.8 + memory.importance() * 0.55, 16);
            sphere.setMaterial(material);
            placeSphere(sphere, positions.get(memory.id()));
            sphere.setFocusTraversable(true);
            sphere.setAccessibleText(memory.title() + ", memory, " + DATE.format(memory.occurredOn()));
            configureNodeInput(memory, sphere);
            nodeViews.put(memory.id(), new NodeView(memory, sphere, material, base));
            geometry.getChildren().add(sphere);
        }
        applyHighlight(selection.focusId().orElse(null));
        settleScale.setX(0.84);
        settleScale.setY(0.84);
        settleScale.setZ(0.84);
        settleAnimation = new Timeline(new KeyFrame(Duration.millis(320),
                new KeyValue(settleScale.xProperty(), 1), new KeyValue(settleScale.yProperty(), 1),
                new KeyValue(settleScale.zProperty(), 1)));
        settleAnimation.setOnFinished(event -> updateLabels());
        settleAnimation.play();
        setMotionEnabled(motionEnabled);
    }

    private void configureNodeInput(Memory memory, Sphere sphere) {
        sphere.setOnMouseEntered(event -> {
            selection.hover(memory.id());
            applyHighlight(selection.focusId().orElse(null));
            event.consume();
        });
        sphere.setOnMouseExited(event -> {
            selection.clearHover();
            applyHighlight(selection.focusId().orElse(null));
            event.consume();
        });
        sphere.setOnMousePressed(event -> event.consume());
        sphere.setOnMouseDragged(event -> event.consume());
        sphere.setOnMouseReleased(event -> event.consume());
        sphere.setOnMouseClicked(event -> {
            selectMemory(memory);
            event.consume();
        });
        sphere.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                selectMemory(memory);
                event.consume();
            }
        });
    }

    private void selectMemory(Memory memory) {
        selection.select(memory.id());
        onSelected.accept(memory);
        applyHighlight(memory.id());
    }

    private void configureCameraInput() {
        subScene.setOnMousePressed(event -> {
            if (event.getTarget() != subScene) {
                return;
            }
            cameraDragging = event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY;
            panning = event.isShiftDown() || event.getButton() == MouseButton.SECONDARY;
            pointerX = event.getSceneX();
            pointerY = event.getSceneY();
        });
        subScene.setOnMouseDragged(event -> {
            if (!cameraDragging) {
                return;
            }
            double deltaX = event.getSceneX() - pointerX;
            double deltaY = event.getSceneY() - pointerY;
            if (panning) {
                cameraState.panBy(-deltaX * 0.8, -deltaY * 0.8);
            } else {
                cameraState.rotateBy(-deltaY * 0.35, deltaX * 0.35);
            }
            pointerX = event.getSceneX();
            pointerY = event.getSceneY();
            applyCamera();
            event.consume();
        });
        subScene.setOnMouseReleased(event -> cameraDragging = false);
        subScene.setOnScroll(event -> {
            cameraState.zoomBy(event.getDeltaY() * 1.25);
            applyCamera();
            event.consume();
        });
    }

    private void applyCamera() {
        applyCamera(true);
    }

    private void applyCamera(boolean projectLabels) {
        pitch.setAngle(cameraState.pitch());
        yaw.setAngle(cameraState.yaw());
        camera.setTranslateX(cameraState.panX());
        camera.setTranslateY(cameraState.panY());
        camera.setTranslateZ(cameraState.distance());
        if (projectLabels) {
            updateLabels();
        }
    }

    private void applyHighlight(UUID memoryFocus) {
        Set<UUID> neighbors = memoryFocus == null ? Set.of() : graph.neighbors(memoryFocus);
        Set<UUID> constellationMembers = focusedConstellation == null ? Set.of() : focusedConstellation.memoryIds();
        for (Map.Entry<UUID, NodeView> entry : nodeViews.entrySet()) {
            boolean active = memoryFocus != null
                    ? entry.getKey().equals(memoryFocus) || neighbors.contains(entry.getKey())
                    : focusedConstellation == null || constellationMembers.contains(entry.getKey());
            boolean primary = entry.getKey().equals(memoryFocus);
            NodeView view = entry.getValue();
            view.sphere().setOpacity(active ? 1 : 0.07);
            view.material().setDiffuseColor(primary ? view.base().brighter().brighter()
                    : active && memoryFocus != null ? view.base().brighter() : view.base());
            double scale = primary ? 1.28 : active && memoryFocus != null ? 1.1 : 1;
            view.sphere().setScaleX(scale);
            view.sphere().setScaleY(scale);
            view.sphere().setScaleZ(scale);
        }
        for (EdgeView edge : edgeViews) {
            boolean active = isEdgeActive(edge.edge());
            edge.cylinder().setVisible(connectionsVisible && active);
            edge.cylinder().setOpacity(active ? 0.9 : 0);
            edge.material().setDiffuseColor(active && (memoryFocus != null || focusedConstellation != null)
                    ? edge.base().brighter() : edge.base());
            edge.cylinder().setRadius(active && memoryFocus != null ? 0.48 : 0.24);
            edge.traveler().setVisible(connectionsVisible && motionEnabled && active);
        }
        updateLabels();
    }

    private void updateLabels() {
        if (graph.memories().isEmpty()) {
            return;
        }
        labelLayer.getChildren().clear();
        UUID focus = selection.focusId().orElse(null);
        LinkedHashSet<UUID> labelled = new LinkedHashSet<>();
        if (focus != null && nodeViews.containsKey(focus)) {
            labelled.add(focus);
        }
        for (UUID id : labelled) {
            NodeView view = nodeViews.get(id);
            if (view == null || view.sphere().getOpacity() < 0.5) {
                continue;
            }
            Label label = new Label(view.memory().title() + "\n" + DATE.format(view.memory().occurredOn()));
            label.getStyleClass().add("space-node-label");
            Bounds sceneBounds = view.sphere().localToScene(view.sphere().getBoundsInLocal(), true);
            Point2D point = labelLayer.sceneToLocal(sceneBounds.getMaxX(), sceneBounds.getMinY());
            label.relocate(point.getX() + 4, point.getY() - 2);
            labelLayer.getChildren().add(label);
        }
    }

    private static void placeSphere(Sphere sphere, Vector3 position) {
        sphere.setTranslateX(position.x());
        sphere.setTranslateY(position.y());
        sphere.setTranslateZ(position.z());
    }

    private void createStarfield() {
        PhongMaterial material = new PhongMaterial(Color.web("#b9c9ec"));
        for (int index = 0; index < 96; index++) {
            double angle = index * 2.399963229728653;
            double horizontalRadius = 430 + (index * 47 % 520);
            double verticalRadius = 250 + (index * 31 % 330);
            Sphere star = new Sphere(0.48 + (index % 4) * 0.17, 5);
            star.setMaterial(material);
            star.setMouseTransparent(true);
            star.setTranslateX(Math.cos(angle) * horizontalRadius);
            star.setTranslateY(Math.sin(angle * 1.37) * verticalRadius);
            star.setTranslateZ(180 + (index * 83 % 920));
            double phase = (index * 0.618033988749895) % 1;
            star.setOpacity(0.18 + phase * 0.34);
            backgroundStars.add(new BackgroundStar(star, phase));
            starfield.getChildren().add(star);
        }
    }

    private void animateSpace(double elapsedSeconds) {
        for (BackgroundStar background : backgroundStars) {
            double wave = 0.5 + 0.5 * Math.sin((elapsedSeconds / 2.6 + background.phase()) * Math.PI * 2);
            background.sphere().setOpacity(0.14 + wave * 0.42);
        }
        UUID memoryFocus = selection.focusId().orElse(null);
        Set<UUID> neighbors = memoryFocus == null ? Set.of() : graph.neighbors(memoryFocus);
        Set<UUID> constellationMembers = focusedConstellation == null ? Set.of() : focusedConstellation.memoryIds();
        for (Map.Entry<UUID, NodeView> entry : nodeViews.entrySet()) {
            boolean primary = entry.getKey().equals(memoryFocus);
            boolean neighbor = neighbors.contains(entry.getKey());
            boolean constellationMember = focusedConstellation != null && constellationMembers.contains(entry.getKey());
            double baseScale = primary ? 1.28 : neighbor ? 1.1 : 1;
            double amplitude = primary ? 0.08 : constellationMember ? 0.055 : 0.025;
            double scale = baseScale * SpaceMotion.pulse(entry.getKey(), elapsedSeconds, amplitude);
            entry.getValue().sphere().setScaleX(scale);
            entry.getValue().sphere().setScaleY(scale);
            entry.getValue().sphere().setScaleZ(scale);
        }
        for (EdgeView edge : edgeViews) {
            boolean active = isEdgeActive(edge.edge());
            edge.traveler().setVisible(connectionsVisible && active);
            if (!connectionsVisible || !active) {
                continue;
            }
            double progress = SpaceMotion.edgeProgress(
                    edge.edge().firstId(), edge.edge().secondId(), elapsedSeconds);
            Vector3 position = SpaceMotion.interpolate(
                    positions.get(edge.edge().firstId()), positions.get(edge.edge().secondId()), progress);
            placeSphere(edge.traveler(), position);
            edge.traveler().setOpacity(0.58 + 0.38 * Math.sin(progress * Math.PI));
        }
    }

    private boolean isEdgeActive(MemoryGraphEdge edge) {
        UUID memoryFocus = selection.focusId().orElse(null);
        UUID constellationFocus = focusedConstellation == null ? null : focusedConstellation.id();
        return GraphFocusVisibility.shows(edge, memoryFocus, constellationFocus);
    }

    private static void placeCylinder(Cylinder cylinder, ConnectionGeometry placement) {
        cylinder.setTranslateX(placement.midpoint().x());
        cylinder.setTranslateY(placement.midpoint().y());
        cylinder.setTranslateZ(placement.midpoint().z());
        Vector3 axis = placement.rotationAxis();
        cylinder.getTransforms().add(new Rotate(placement.angleDegrees(), new Point3D(axis.x(), axis.y(), axis.z())));
    }

    private static Color memoryColor(Mood mood) {
        return switch (mood) {
            case JOYFUL -> Color.web("#f0c95c");
            case PEACEFUL -> Color.web("#63c7b7");
            case EXCITED -> Color.web("#ed8668");
            case NOSTALGIC -> Color.web("#a68bd5");
            case SAD -> Color.web("#6699d2");
            case ANXIOUS -> Color.web("#d87992");
            case NEUTRAL -> Color.web("#c5cfdd");
        };
    }

    private Color edgeColor(MemoryGraphEdge edge) {
        List<Color> colors = edge.constellationIds().stream().sorted()
                .map(constellationColors::get).filter(java.util.Objects::nonNull).toList();
        if (colors.isEmpty()) {
            return Color.rgb(113, 129, 157, 0.62);
        }
        double red = colors.stream().mapToDouble(Color::getRed).average().orElse(0.5);
        double green = colors.stream().mapToDouble(Color::getGreen).average().orElse(0.5);
        double blue = colors.stream().mapToDouble(Color::getBlue).average().orElse(0.5);
        return new Color(red, green, blue, 0.72);
    }

    private static Color depthAdjusted(Color color, double z) {
        double darkness = 0.07 + 0.18 * ((z + ForceDirected3DLayout.MAX_Z) / (2 * ForceDirected3DLayout.MAX_Z));
        return color.interpolate(Color.BLACK, Math.max(0.04, Math.min(0.24, darkness)));
    }

    private record NodeView(Memory memory, Sphere sphere, PhongMaterial material, Color base) { }
    private record EdgeView(MemoryGraphEdge edge, Cylinder cylinder, PhongMaterial material, Color base,
            Sphere traveler) { }
    private record BackgroundStar(Sphere sphere, double phase) { }
}
