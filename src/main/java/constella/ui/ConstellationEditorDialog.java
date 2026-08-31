package constella.ui;

import constella.model.Constellation;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Small create/rename dialog for a constellation. */
final class ConstellationEditorDialog extends Dialog<ConstellationEditorDialog.Result> {
    record Result(String name, String description) {
    }

    ConstellationEditorDialog(Constellation existing) {
        setTitle(existing == null ? "New Constellation" : "Rename Constellation");
        setHeaderText(existing == null ? "Group related memories" : "Update the constellation name");
        getDialogPane().getStylesheets().add(getClass().getResource("constella.css").toExternalForm());
        TextField name = new TextField(existing == null ? "" : existing.name());
        TextArea description = new TextArea(existing == null ? "" : existing.description().orElse(""));
        description.setPrefRowCount(3);
        description.setDisable(existing != null);
        GridPane form = new GridPane(10, 10);
        form.setPadding(new Insets(4));
        form.addRow(0, new Label("Name *"), name);
        form.addRow(1, new Label("Description"), description);
        getDialogPane().setContent(form);
        getDialogPane().getButtonTypes().addAll(
                new ButtonType(existing == null ? "Create" : "Rename", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);
        setResultConverter(button -> button.getButtonData() == ButtonBar.ButtonData.OK_DONE
                ? new Result(name.getText(), description.getText()) : null);
    }
}
