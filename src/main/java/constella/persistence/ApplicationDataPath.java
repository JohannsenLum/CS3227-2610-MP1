package constella.persistence;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/** Resolves the per-user journal path without accessing the network. */
public final class ApplicationDataPath {
    private ApplicationDataPath() {
    }

    public static Path journalFile() {
        String override = System.getenv("CONSTELLA_DATA_FILE");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        return resolve(System.getProperty("os.name"), System.getProperty("user.home"), System.getenv());
    }

    static Path resolve(String osName, String userHome, Map<String, String> environment) {
        String operatingSystem = osName.toLowerCase(Locale.ROOT);
        Path home = Path.of(userHome);
        if (operatingSystem.contains("mac")) {
            return home.resolve("Library/Application Support/Constella/journal.json");
        }
        if (operatingSystem.contains("win")) {
            String appData = environment.get("APPDATA");
            Path base = appData == null || appData.isBlank() ? home.resolve("AppData/Roaming") : Path.of(appData);
            return base.resolve("Constella/journal.json");
        }
        String xdgDataHome = environment.get("XDG_DATA_HOME");
        Path base = xdgDataHome == null || xdgDataHome.isBlank() ? home.resolve(".local/share") : Path.of(xdgDataHome);
        return base.resolve("constella/journal.json");
    }
}
