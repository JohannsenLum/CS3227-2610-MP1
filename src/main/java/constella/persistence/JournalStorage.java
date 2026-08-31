package constella.persistence;

import constella.application.JournalSnapshot;

/** Storage boundary for complete journal snapshots. */
public interface JournalStorage {
    default boolean exists() {
        return true;
    }

    JournalSnapshot load() throws JournalStorageException;

    void save(JournalSnapshot snapshot) throws JournalStorageException;
}
