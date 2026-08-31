package constella.ui;

import constella.application.MemoryDraft;
import constella.application.ConstellationSearch;
import constella.model.Constellation;
import constella.model.Memory;
import constella.model.Mood;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/** Create/edit dialog that returns validated domain input and selected memberships. */
final class MemoryEditorDialog extends Dialog<MemoryEditorDialog.Result> {
    record Result(MemoryDraft draft, Set<UUID> constellationIds) {
    }

    private final TextField title = new TextField();
    private final DatePicker date = new DatePicker(LocalDate.now());
    private final TextArea description = new TextArea();
    private final ComboBox<Mood> mood = new ComboBox<>(FXCollections.observableArrayList(Mood.values()));
    private final Spinner<Integer> importance = new Spinner<>(1, 5, 3);
    private final TextField tags = new TextField();
    private final TextField people = new TextField();
    private final TextField location = new TextField();
    private final TextField constellationSearch = new TextField();
    private final ListView<Constellation> constellations = new ListView<>();
    private final Map<UUID, BooleanProperty> constellationSelections = new LinkedHashMap<>();
    private final Label error = new Label();

    MemoryEditorDialog(Memory existing, List<Constellation> availableConstellations) {
        setTitle(existing == null ? "New Memory" : "Edit Memory");
        setHeaderText(existing == null ? "Record a memory" : "Update this memory");
        getDialogPane().getStylesheets().add(getClass().getResource("constella.css").toExternalForm());
        getDialogPane().getButtonTypes().addAll(
                new ButtonType(existing == null ? "Create" : "Save", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);
        getDialogPane().setContent(buildForm(availableConstellations));
        getDialogPane().setPrefWidth(700);
        populate(existing, availableConstellations);

        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().getFirst());
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                createResult();
                error.setText("");
            } catch (RuntimeException exception) {
                error.setText(friendlyMessage(exception));
                event.consume();
            }
        });
        setResultConverter(button -> button.getButtonData() == ButtonBar.ButtonData.OK_DONE ? createResult() : null);
    }

    private Node buildForm(List<Constellation> available) {
        title.setPromptText("e.g. Evening at East Coast Park");
        description.setPrefRowCount(2);
        mood.getSelectionModel().select(Mood.NEUTRAL);
        importance.setEditable(false);
        tags.setPromptText("travel, family, celebration");
        people.setPromptText("Alice, Bob");
        available.forEach(constellation -> constellationSelections.put(
                constellation.id(), new SimpleBooleanProperty(false)));
        FilteredList<Constellation> filteredConstellations = new FilteredList<>(
                FXCollections.observableArrayList(available));
        constellations.setItems(filteredConstellations);
        constellations.setFixedCellSize(32);
        constellations.setPrefHeight(32 * 7 + 2);
        constellations.setMinHeight(32 * 7 + 2);
        constellations.setMaxHeight(32 * 7 + 2);
        constellations.getStyleClass().add("constellation-check-list");
        constellations.setCellFactory(ignored -> new ConstellationCheckCell());
        constellationSearch.setPromptText("Search constellations");
        constellationSearch.setAccessibleText("Search available constellations by name");
        constellationSearch.textProperty().addListener((observable, oldValue, query) ->
                filteredConstellations.setPredicate(constellation -> ConstellationSearch.matches(constellation, query)));
        VBox constellationPicker = new VBox(7, constellationSearch, constellations);
        error.getStyleClass().add("validation-error");
        error.setWrapText(true);

        GridPane grid = new GridPane(12, 10);
        grid.setPadding(new Insets(4));
        ColumnConstraints labels = new ColumnConstraints(130, 130, 130);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);
        grid.getColumnConstraints().addAll(labels, fields);
        int row = 0;
        addRow(grid, row++, "Title *", title);
        addRow(grid, row++, "Date *", date);
        addRow(grid, row++, "Description", description);
        addRow(grid, row++, "Mood *", mood);
        addRow(grid, row++, "Importance *", importance);
        addRow(grid, row++, "Tags", tags);
        addRow(grid, row++, "People", people);
        addRow(grid, row++, "Location", location);
        addRow(grid, row++, "Constellations", constellationPicker);
        grid.add(error, 1, row);
        return grid;
    }

    private static void addRow(GridPane grid, int row, String label, Node control) {
        Label fieldLabel = new Label(label);
        fieldLabel.setMinWidth(Region.USE_PREF_SIZE);
        grid.add(fieldLabel, 0, row);
        grid.add(control, 1, row);
        GridPane.setHgrow(control, Priority.ALWAYS);
    }

    private void populate(Memory existing, List<Constellation> available) {
        if (existing == null) {
            return;
        }
        title.setText(existing.title());
        date.setValue(existing.occurredOn());
        description.setText(existing.description().orElse(""));
        mood.setValue(existing.mood());
        importance.getValueFactory().setValue(existing.importance());
        tags.setText(String.join(", ", existing.tags()));
        people.setText(String.join(", ", existing.people()));
        location.setText(existing.location().orElse(""));
        available.stream().filter(constellation -> constellation.memoryIds().contains(existing.id()))
                .forEach(constellation -> constellationSelections.get(constellation.id()).set(true));
    }

    private Result createResult() {
        MemoryDraft draft = new MemoryDraft(title.getText(), date.getValue(), description.getText(), mood.getValue(),
                importance.getValue(), tags.getText(), people.getText(), location.getText());
        draft.createMemory();
        Set<UUID> selected = new LinkedHashSet<>();
        constellationSelections.forEach((id, selectedProperty) -> {
            if (selectedProperty.get()) {
                selected.add(id);
            }
        });
        return new Result(draft, Set.copyOf(selected));
    }

    private static String friendlyMessage(RuntimeException exception) {
        return exception.getMessage() == null ? "Please check the entered values." : exception.getMessage();
    }

    private final class ConstellationCheckCell extends ListCell<Constellation> {
        private final CheckBox selected = new CheckBox();
        private final Label name = new Label();
        private final HBox row = new HBox(10, selected, name);
        private BooleanProperty boundSelection;

        private ConstellationCheckCell() {
            getStyleClass().add("constellation-check-cell");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(name, Priority.ALWAYS);
            name.getStyleClass().add("constellation-check-name");
            setFocusTraversable(true);
            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1
                        && boundSelection != null && !originatedInsideCheckbox(event.getTarget())) {
                    boundSelection.set(!boundSelection.get());
                    event.consume();
                }
            });
            setOnKeyPressed(event -> {
                if (boundSelection != null
                        && (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER)) {
                    boundSelection.set(!boundSelection.get());
                    event.consume();
                }
            });
        }

        @Override
        protected void updateItem(Constellation constellation, boolean empty) {
            if (boundSelection != null) {
                selected.selectedProperty().unbindBidirectional(boundSelection);
                boundSelection = null;
            }
            super.updateItem(constellation, empty);
            setText(null);
            if (empty || constellation == null) {
                name.setText("");
                selected.setAccessibleText(null);
                setAccessibleText(null);
                setGraphic(null);
                return;
            }
            boundSelection = constellationSelections.get(constellation.id());
            selected.setSelected(boundSelection.get());
            selected.selectedProperty().bindBidirectional(boundSelection);
            name.setText(constellation.name());
            selected.setAccessibleText("Include " + constellation.name());
            setAccessibleText(constellation.name() + ", toggle constellation membership");
            setGraphic(row);
        }

        private boolean originatedInsideCheckbox(Object eventTarget) {
            if (!(eventTarget instanceof Node node)) {
                return false;
            }
            while (node != null && node != this) {
                if (node == selected) {
                    return true;
                }
                node = node.getParent();
            }
            return false;
        }
    }
}
