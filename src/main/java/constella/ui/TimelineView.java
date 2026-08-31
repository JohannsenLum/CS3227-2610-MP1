package constella.ui;

import constella.model.Memory;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/** Alternating chronological timeline with a visible central time axis. */
final class TimelineView extends ScrollPane {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM uuuu");

    TimelineView(List<Memory> memories, Consumer<Memory> onSelected) {
        VBox track = new VBox(0);
        track.getStyleClass().add("timeline-track");
        track.setFillWidth(true);
        Integer previousYear = null;
        for (int index = 0; index < memories.size(); index++) {
            Memory memory = memories.get(index);
            int year = memory.occurredOn().getYear();
            if (!Integer.valueOf(year).equals(previousYear)) {
                Label yearLabel = new Label(Integer.toString(year));
                yearLabel.getStyleClass().add("timeline-year");
                track.getChildren().add(yearLabel);
                previousYear = year;
            }
            track.getChildren().add(createRow(memory, index % 2 == 0, onSelected));
        }
        setContent(track);
        setFitToWidth(true);
        setPannable(true);
        getStyleClass().add("timeline-scroll");
    }

    private static GridPane createRow(Memory memory, boolean left, Consumer<Memory> onSelected) {
        GridPane row = new GridPane();
        row.getStyleClass().add("timeline-row");
        row.setPadding(new Insets(0, 12, 0, 12));
        row.setMinHeight(104);
        row.setPrefHeight(104);
        row.setMaxHeight(104);
        ColumnConstraints side = new ColumnConstraints();
        side.setPercentWidth(46);
        side.setHgrow(Priority.ALWAYS);
        ColumnConstraints axis = new ColumnConstraints();
        axis.setPercentWidth(8);
        ColumnConstraints otherSide = new ColumnConstraints();
        otherSide.setPercentWidth(46);
        otherSide.setHgrow(Priority.ALWAYS);
        row.getColumnConstraints().addAll(side, axis, otherSide);

        Button card = createCard(memory, onSelected);
        GridPane.setHalignment(card, left ? HPos.RIGHT : HPos.LEFT);
        row.add(card, left ? 0 : 2, 0);
        Region verticalLine = new Region();
        verticalLine.getStyleClass().add("timeline-axis-line");
        verticalLine.setMaxHeight(Double.MAX_VALUE);
        Circle marker = new Circle(6);
        marker.getStyleClass().add("timeline-marker");
        StackPane axisCell = new StackPane(verticalLine, marker);
        axisCell.getStyleClass().add("timeline-axis-cell");
        axisCell.setAlignment(Pos.CENTER);
        row.add(axisCell, 1, 0);
        GridPane.setVgrow(axisCell, Priority.ALWAYS);
        return row;
    }

    private static Button createCard(Memory memory, Consumer<Memory> onSelected) {
        Label date = new Label(DATE.format(memory.occurredOn()));
        date.getStyleClass().add("timeline-date");
        Label title = new Label(memory.title());
        title.getStyleClass().add("memory-title");
        title.setWrapText(true);
        String description = memory.description().orElse("A " + memory.mood().name().toLowerCase() + " memory");
        Label summary = new Label(description.length() > 105 ? description.substring(0, 102) + "…" : description);
        summary.getStyleClass().add("memory-summary");
        summary.setWrapText(true);
        summary.setMaxHeight(34);
        VBox content = new VBox(5, date, title, summary);
        content.setAlignment(Pos.CENTER_LEFT);
        Button card = new Button();
        card.getStyleClass().add("timeline-card");
        card.setGraphic(content);
        card.setPrefWidth(340);
        card.setMaxWidth(360);
        card.setMinHeight(84);
        card.setPrefHeight(84);
        card.setMaxHeight(84);
        card.setAccessibleText(DATE.format(memory.occurredOn()) + ", " + memory.title());
        card.setOnAction(event -> onSelected.accept(memory));
        return card;
    }
}
