package constella.ui;

import constella.application.JournalSession;
import constella.application.DemoJournalSeeder;
import constella.persistence.JsonJournalStorage;
import constella.persistence.JournalStorageException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/** JavaFX entry point for Constella. */
public final class ConstellaApplication extends Application {
    private static final double WINDOW_WIDTH = 1180;
    private static final double WINDOW_HEIGHT = 760;

    @Override
    public void start(Stage stage) {
        JsonJournalStorage storage = JsonJournalStorage.forCurrentUser();
        JournalSession session;
        String loadError = null;
        try {
            session = JournalSession.loadOrSeed(storage, DemoJournalSeeder::create);
        } catch (JournalStorageException exception) {
            session = JournalSession.empty(storage);
            loadError = exception.getMessage();
        }

        ConstellaView root = new ConstellaView(session);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("constella.css").toExternalForm());
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setTitle("Constella");
        stage.setScene(scene);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), root::createMemory);
        stage.show();

        if (loadError != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Journal could not be loaded");
            alert.setHeaderText("Constella opened an empty working journal");
            alert.setContentText(loadError + "\n\nYour existing file was not changed. Resolve or back up the file before saving.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
