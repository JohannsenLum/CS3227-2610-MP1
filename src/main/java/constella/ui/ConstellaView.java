package constella.ui;

import constella.application.JournalSession;
import constella.application.JournalFilter;
import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import constella.persistence.JournalStorageException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;

/** Main application shell and memory CRUD interface. */
final class ConstellaView extends BorderPane {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("d MMM uuuu");

    private final JournalSession session;
    private final StackPane content = new StackPane();
    private final VBox details = new VBox(12);
    private final Label sectionTitle = new Label("My Sky");
    private final Space3DView space3D;
    private final TextField searchField = new TextField();
    private final ComboBox<Mood> moodFilter = new ComboBox<>();
    private final ComboBox<String> tagFilter = new ComboBox<>();
    private final ComboBox<Constellation> constellationFilter = new ComboBox<>();
    private final ComboBox<Integer> yearFilter = new ComboBox<>();
    private Constellation focusedSpaceConstellation;
    private boolean spaceConnectionsVisible = true;
    private boolean spaceAutoRotate = true;
    private boolean spaceMotionEnabled = true;
    private View currentView = View.SPACE_3D;
    private boolean refreshingFilters;

    private enum View { SPACE_3D, TIMELINE, CONSTELLATIONS }

    private record SpaceFocusOption(String label, Constellation constellation) { }

    ConstellaView(JournalSession session) {
        this.session = session;
        this.space3D = new Space3DView(this::showDetails);
        configureFilters();
        getStyleClass().add("app-shell");
        setTop(buildHeader());
        setLeft(buildNavigation());
        setCenter(content);
        setRight(details);
        showSpace3D();
        refresh();
    }

    private Node buildHeader() {
        Label brand = new Label("CONSTELLA");
        brand.getStyleClass().add("brand");
        Label subtitle = new Label("Your memories, written in the stars");
        subtitle.getStyleClass().add("subtitle");
        VBox identity = new VBox(2, brand, subtitle);
        Button newMemory = new Button("＋  New Memory");
        newMemory.getStyleClass().add("primary-button");
        newMemory.setAccessibleText("Create a new memory");
        newMemory.setOnAction(event -> createMemory());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(16, identity, spacer, newMemory);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        return header;
    }

    private Node buildNavigation() {
        VBox navigation = new VBox(8);
        navigation.getStyleClass().add("sidebar");
        navigation.setPadding(new Insets(24, 16, 24, 16));
        navigation.setPrefWidth(190);
        navigation.getChildren().add(new Label("EXPLORE"));
        navigation.getChildren().add(navButton("✦  My Sky", this::showSpace3D));
        navigation.getChildren().add(navButton("◷  Timeline", this::showTimeline));
        navigation.getChildren().add(navButton("⌁  Constellations", this::showConstellations));
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button clearJournal = navButton("Clear Journal", this::confirmClearJournal);
        clearJournal.getStyleClass().add("danger-nav-button");
        clearJournal.setAccessibleText("Clear all demo and personal journal data");
        navigation.getChildren().addAll(spacer, clearJournal);
        return navigation;
    }

    private void confirmClearJournal() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(getScene().getWindow());
        confirmation.setTitle("Clear journal");
        confirmation.setHeaderText("Remove every memory and constellation?");
        confirmation.setContentText(
                "This permanently clears the demo journal and anything you added. The demo will not return after restart.");
        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }
        try {
            session.clearJournal();
            searchField.clear();
            moodFilter.setValue(null);
            tagFilter.setValue(null);
            constellationFilter.setValue(null);
            yearFilter.setValue(null);
            showDetails(null);
            showSpace3D();
        } catch (JournalStorageException exception) {
            showError("Journal could not be cleared", exception.getMessage());
        }
    }

    private Button navButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void showSpace3D() {
        currentView = View.SPACE_3D;
        sectionTitle.setText("My Sky");
        ComboBox<SpaceFocusOption> focusedConstellation = new ComboBox<>();
        List<SpaceFocusOption> focusOptions = new ArrayList<>();
        focusOptions.add(new SpaceFocusOption("All memories", null));
        session.journal().constellations().stream()
                .map(constellation -> new SpaceFocusOption(constellation.name(), constellation))
                .forEach(focusOptions::add);
        focusedConstellation.setItems(FXCollections.observableArrayList(focusOptions));
        focusedConstellation.setCellFactory(ignored -> spaceFocusCell());
        focusedConstellation.setButtonCell(spaceFocusCell());
        SpaceFocusOption selectedOption = focusOptions.stream()
                .filter(option -> Objects.equals(option.constellation(), focusedSpaceConstellation))
                .findFirst().orElse(focusOptions.getFirst());
        if (selectedOption.constellation() == null) {
            focusedSpaceConstellation = null;
        }
        focusedConstellation.setValue(selectedOption);
        focusedConstellation.valueProperty().addListener((observable, oldValue, newValue) -> {
            focusedSpaceConstellation = newValue == null ? null : newValue.constellation();
            space3D.focusConstellation(focusedSpaceConstellation);
        });
        Button clearFocus = new Button("Clear focus");
        clearFocus.setOnAction(event -> focusedConstellation.setValue(focusOptions.getFirst()));
        Button resetCamera = new Button("Reset Camera");
        resetCamera.setAccessibleText("Reset the 3D camera rotation, zoom, and pan");
        resetCamera.setOnAction(event -> space3D.resetCamera());
        Button focusSelected = new Button("Focus Selected");
        focusSelected.setAccessibleText("Move the camera toward the selected memory");
        focusSelected.setOnAction(event -> space3D.focusSelected());
        CheckBox connections = new CheckBox("Connections");
        connections.setSelected(spaceConnectionsVisible);
        connections.setAccessibleText("Show or hide 3D connection lines");
        connections.selectedProperty().addListener((observable, oldValue, visible) -> {
            spaceConnectionsVisible = visible;
            space3D.setConnectionsVisible(visible);
        });
        CheckBox autoRotate = new CheckBox("Auto rotate");
        autoRotate.setSelected(spaceAutoRotate);
        autoRotate.setAccessibleText("Automatically rotate the 3D graph");
        autoRotate.selectedProperty().addListener((observable, oldValue, enabled) -> {
            spaceAutoRotate = enabled;
            space3D.setAutoRotate(enabled);
        });
        CheckBox motion = new CheckBox("Motion");
        motion.setSelected(spaceMotionEnabled);
        motion.setAccessibleText("Animate stars and constellation light trails");
        motion.selectedProperty().addListener((observable, oldValue, enabled) -> {
            spaceMotionEnabled = enabled;
            space3D.setMotionEnabled(enabled);
        });
        Button zoomOut = new Button("−");
        zoomOut.setAccessibleText("Zoom out of the 3D graph");
        zoomOut.setOnAction(event -> space3D.zoomOut());
        Button zoomIn = new Button("+");
        zoomIn.setAccessibleText("Zoom into the 3D graph");
        zoomIn.setOnAction(event -> space3D.zoomIn());
        Label hint = new Label("Ball = mood • line = constellation • drag rotate • scroll zoom");
        hint.getStyleClass().add("graph-hint");
        FlowPane controls = new FlowPane(8, 8, focusedConstellation, clearFocus, connections, motion, autoRotate,
                zoomOut, zoomIn, resetCamera, focusSelected, hint);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getStyleClass().add("graph-toolbar");
        space3D.show(filteredMemories(), session.journal().constellations(), focusedSpaceConstellation);
        space3D.setConnectionsVisible(spaceConnectionsVisible);
        space3D.setMotionEnabled(spaceMotionEnabled);
        space3D.setAutoRotate(spaceAutoRotate);
        VBox view = new VBox(9, sectionTitle, buildFilterBar(), controls, space3D);
        view.setPadding(new Insets(24));
        VBox.setVgrow(space3D, Priority.ALWAYS);
        content.getChildren().setAll(view);
    }

    private void configureFilters() {
        searchField.setPromptText("Search memories");
        searchField.setAccessibleText("Search title, description, tags, people, and location");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshCurrentView());
        moodFilter.setPromptText("Any mood");
        tagFilter.setPromptText("Any tag");
        constellationFilter.setPromptText("Any constellation");
        constellationFilter.setCellFactory(ignored -> constellationCell());
        constellationFilter.setButtonCell(constellationCell());
        yearFilter.setPromptText("Any year");
        moodFilter.setOnAction(event -> refreshFiltersIfReady());
        tagFilter.setOnAction(event -> refreshFiltersIfReady());
        constellationFilter.setOnAction(event -> refreshFiltersIfReady());
        yearFilter.setOnAction(event -> refreshFiltersIfReady());
        refreshFilterChoices();
    }

    private Node buildFilterBar() {
        refreshFilterChoices();
        Button reset = new Button("Reset filters");
        reset.setOnAction(event -> {
            searchField.clear();
            moodFilter.setValue(null);
            tagFilter.setValue(null);
            constellationFilter.setValue(null);
            yearFilter.setValue(null);
            refreshCurrentView();
        });
        Label label = new Label("FILTER");
        label.getStyleClass().add("filter-label");
        FlowPane filters = new FlowPane(8, 8, label, searchField, moodFilter, tagFilter, constellationFilter,
                yearFilter, reset);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.getStyleClass().add("filter-bar");
        return filters;
    }

    private void refreshFilterChoices() {
        refreshingFilters = true;
        String selectedTag = tagFilter.getValue();
        Constellation selectedConstellation = constellationFilter.getValue();
        Integer selectedYear = yearFilter.getValue();
        moodFilter.setItems(FXCollections.observableArrayList(Mood.values()));
        tagFilter.setItems(FXCollections.observableArrayList(session.journal().memories().stream()
                .flatMap(memory -> memory.tags().stream()).distinct().sorted().toList()));
        constellationFilter.setItems(FXCollections.observableArrayList(session.journal().constellations()));
        yearFilter.setItems(FXCollections.observableArrayList(session.journal().memories().stream()
                .map(memory -> memory.occurredOn().getYear()).distinct().sorted(java.util.Comparator.reverseOrder()).toList()));
        tagFilter.setValue(tagFilter.getItems().contains(selectedTag) ? selectedTag : null);
        constellationFilter.setValue(constellationFilter.getItems().contains(selectedConstellation)
                ? selectedConstellation : null);
        yearFilter.setValue(yearFilter.getItems().contains(selectedYear) ? selectedYear : null);
        refreshingFilters = false;
    }

    private void refreshFiltersIfReady() {
        if (!refreshingFilters) {
            refreshCurrentView();
        }
    }

    private JournalFilter activeFilter() {
        return new JournalFilter(searchField.getText(), moodFilter.getValue() == null ? Set.of() : Set.of(moodFilter.getValue()),
                tagFilter.getValue(), constellationFilter.getValue() == null ? null : constellationFilter.getValue().id(),
                yearFilter.getValue());
    }

    private java.util.List<Memory> filteredMemories() {
        return session.journal().findMemories(activeFilter());
    }

    private void refreshCurrentView() {
        if (getScene() == null && content.getChildren().isEmpty()) {
            return;
        }
        if (currentView == View.SPACE_3D) {
            showSpace3D();
        } else if (currentView == View.TIMELINE) {
            showTimeline();
        }
    }

    private void showTimeline() {
        space3D.setAutoRotate(false);
        space3D.setMotionEnabled(false);
        currentView = View.TIMELINE;
        sectionTitle.setText("Timeline");
        java.util.List<Memory> visible = filteredMemories();
        Node timeline;
        if (visible.isEmpty()) {
            timeline = emptyState("No memories found",
                    session.journal().memories().isEmpty()
                            ? "Create a memory to begin your timeline."
                            : "Try resetting search or filters.");
        } else {
            timeline = new TimelineView(visible, this::showDetails);
        }
        VBox view = new VBox(14, sectionTitle, buildFilterBar(), timeline);
        view.setPadding(new Insets(28));
        VBox.setVgrow(timeline, Priority.ALWAYS);
        content.getChildren().setAll(view);
        showDetails(null);
    }

    private ListCell<Constellation> constellationCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Constellation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        };
    }

    private ListCell<SpaceFocusOption> spaceFocusCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(SpaceFocusOption item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.label());
            }
        };
    }

    private void showConstellations() {
        space3D.setAutoRotate(false);
        space3D.setMotionEnabled(false);
        currentView = View.CONSTELLATIONS;
        sectionTitle.setText("Constellations");
        ListView<Constellation> list = new ListView<>(FXCollections.observableArrayList(session.journal().constellations()));
        list.setCellFactory(ignored -> constellationCell());
        list.setPlaceholder(emptyState("No constellations yet", "Create one to connect related memories."));
        VBox membership = new VBox(8);
        Label help = new Label("Select a constellation to manage its memories.");
        help.getStyleClass().add("subtitle");
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            membership.getChildren().clear();
            if (selected == null) {
                membership.getChildren().add(help);
                return;
            }
            Label name = new Label(selected.name());
            name.getStyleClass().add("detail-title");
            membership.getChildren().add(name);
            selected.description().ifPresent(value -> membership.getChildren().add(detailText(value)));
            if (session.journal().memories().isEmpty()) {
                membership.getChildren().add(new Label("Create memories before assigning them."));
            }
            for (Memory memory : session.journal().memories()) {
                CheckBox included = new CheckBox(memory.title());
                included.setSelected(selected.memoryIds().contains(memory.id()));
                included.setOnAction(event -> {
                    try {
                        session.setConstellationMembership(selected.id(), memory.id(), included.isSelected());
                        showConstellations();
                    } catch (JournalStorageException exception) {
                        showError("Membership could not be saved", exception.getMessage());
                    }
                });
                membership.getChildren().add(included);
            }
        });
        membership.getChildren().add(help);

        Button create = new Button("New Constellation");
        create.getStyleClass().add("primary-button");
        create.setOnAction(event -> editConstellation(null));
        Button rename = new Button("Rename");
        rename.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());
        rename.setOnAction(event -> editConstellation(list.getSelectionModel().getSelectedItem()));
        Button delete = new Button("Delete");
        delete.getStyleClass().add("danger-button");
        delete.disableProperty().bind(list.getSelectionModel().selectedItemProperty().isNull());
        delete.setOnAction(event -> confirmDeleteConstellation(list.getSelectionModel().getSelectedItem()));
        HBox actions = new HBox(8, create, rename, delete);
        HBox body = new HBox(18, list, membership);
        HBox.setHgrow(list, Priority.ALWAYS);
        HBox.setHgrow(membership, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);
        VBox view = new VBox(14, sectionTitle, actions, body);
        view.setPadding(new Insets(28));
        content.getChildren().setAll(view);
        showDetails(null);
    }

    private void editConstellation(Constellation existing) {
        ConstellationEditorDialog dialog = new ConstellationEditorDialog(existing);
        dialog.initOwner(getScene().getWindow());
        dialog.showAndWait().ifPresent(result -> {
            try {
                if (existing == null) {
                    session.createConstellation(result.name(), result.description());
                } else {
                    session.renameConstellation(existing.id(), result.name());
                }
                showConstellations();
            } catch (IllegalArgumentException exception) {
                showError("Constellation is invalid", exception.getMessage());
            } catch (JournalStorageException exception) {
                showError("Constellation could not be saved", exception.getMessage());
            }
        });
    }

    private void confirmDeleteConstellation(Constellation constellation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(getScene().getWindow());
        confirmation.setTitle("Delete constellation");
        confirmation.setHeaderText("Delete “" + constellation.name() + "”?");
        confirmation.setContentText("Its memories remain in your journal; only this grouping and its lines are removed.");
        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }
        try {
            session.deleteConstellation(constellation.id());
            showConstellations();
        } catch (JournalStorageException exception) {
            showError("Constellation could not be deleted", exception.getMessage());
        }
    }

    private VBox emptyState(String title, String message) {
        Label heading = new Label(title);
        heading.getStyleClass().add("empty-title");
        Label explanation = new Label(message);
        explanation.getStyleClass().add("subtitle");
        explanation.setWrapText(true);
        VBox box = new VBox(8, heading, explanation);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("empty-state");
        return box;
    }

    private void refresh() {
        refreshFilterChoices();
        if (currentView == View.SPACE_3D) {
            showSpace3D();
        } else if (currentView == View.CONSTELLATIONS) {
            showConstellations();
        } else {
            showTimeline();
        }
    }

    void createMemory() {
        openEditor(null);
    }

    private void showDetails(Memory memory) {
        details.getChildren().clear();
        details.getStyleClass().add("detail-panel");
        details.setPrefWidth(300);
        details.setPadding(new Insets(28, 22, 28, 22));
        if (memory == null) {
            details.getChildren().addAll(new Label("Memory details"), new Label("Select a memory to see its story."));
            return;
        }

        Label title = new Label(memory.title());
        title.getStyleClass().add("detail-title");
        title.setWrapText(true);
        Label metadata = new Label(memory.occurredOn().format(DISPLAY_DATE) + "\n" + displayMood(memory)
                + " • Importance " + memory.importance());
        metadata.getStyleClass().add("memory-summary");
        Label description = detailText(memory.description().orElse("No description"));
        Label tags = detailText(memory.tags().isEmpty() ? "No tags" : "#" + String.join("  #", memory.tags()));
        Label people = detailText(memory.people().isEmpty() ? "No people recorded" : "With " + String.join(", ", memory.people()));
        Label location = detailText(memory.location().map(value -> "At " + value).orElse("No location"));
        Button edit = new Button("Edit");
        edit.setOnAction(event -> openEditor(memory));
        Button delete = new Button("Delete");
        delete.getStyleClass().add("danger-button");
        delete.setOnAction(event -> confirmDelete(memory));
        details.getChildren().addAll(title, metadata, new Separator(), description, tags, people, location,
                new HBox(8, edit, delete));
    }

    private static Label detailText(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private void openEditor(Memory existing) {
        MemoryEditorDialog dialog = new MemoryEditorDialog(existing, session.journal().constellations());
        dialog.initOwner(getScene().getWindow());
        Optional<MemoryEditorDialog.Result> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        try {
            if (existing == null) {
                session.addMemory(result.get().draft().createMemory(), result.get().constellationIds());
            } else {
                session.updateMemory(result.get().draft().updateMemory(existing.id()), result.get().constellationIds());
            }
            refresh();
        } catch (JournalStorageException exception) {
            showError("Memory could not be saved", exception.getMessage());
            refresh();
        }
    }

    private void confirmDelete(Memory memory) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(getScene().getWindow());
        confirmation.setTitle("Delete memory");
        confirmation.setHeaderText("Delete “" + memory.title() + "”?");
        confirmation.setContentText("This removes the memory from every constellation and cannot be undone.");
        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }
        try {
            session.deleteMemory(memory.id());
            refresh();
        } catch (JournalStorageException exception) {
            showError("Memory could not be deleted", exception.getMessage());
            refresh();
        }
    }

    private void showError(String heading, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(getScene().getWindow());
        alert.setTitle("Constella error");
        alert.setHeaderText(heading);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String displayMood(Memory memory) {
        String name = memory.mood().name().toLowerCase(java.util.Locale.ROOT);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
