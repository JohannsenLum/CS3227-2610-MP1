package constella.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApplicationDataPathTest {
    @Test
    void resolvesMacPath() {
        assertEquals(Path.of("/home/me/Library/Application Support/Constella/journal.json"),
                ApplicationDataPath.resolve("Mac OS X", "/home/me", Map.of()));
    }

    @Test
    void resolvesWindowsAppDataPath() {
        assertEquals(Path.of("C:/Data/Constella/journal.json"),
                ApplicationDataPath.resolve("Windows 11", "C:/Users/me", Map.of("APPDATA", "C:/Data")));
    }

    @Test
    void resolvesLinuxXdgPath() {
        assertEquals(Path.of("/data/constella/journal.json"),
                ApplicationDataPath.resolve("Linux", "/home/me", Map.of("XDG_DATA_HOME", "/data")));
    }
}
