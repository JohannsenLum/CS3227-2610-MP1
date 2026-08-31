package constella.persistence;

/** A contextual, user-presentable storage failure without implementation details. */
public final class JournalStorageException extends Exception {
    public JournalStorageException(String message) {
        super(message);
    }

    public JournalStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
