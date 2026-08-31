package constella.application;

import constella.model.Memory;
import constella.model.Constellation;
import constella.model.StarPosition;
import constella.persistence.JournalStorage;
import constella.persistence.JournalStorageException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/** Coordinates journal mutations with durable storage for the JavaFX interface. */
public final class JournalSession {
    private final JournalStorage storage;
    private final JournalService service;

    private JournalSession(JournalStorage storage, JournalService service) {
        this.storage = storage;
        this.service = service;
    }

    public static JournalSession load(JournalStorage storage) throws JournalStorageException {
        Objects.requireNonNull(storage, "storage must not be null");
        return new JournalSession(storage, new JournalService(storage.load()));
    }

    public static JournalSession loadOrSeed(JournalStorage storage, Supplier<JournalSnapshot> seed)
            throws JournalStorageException {
        Objects.requireNonNull(storage, "storage must not be null");
        Objects.requireNonNull(seed, "seed must not be null");
        if (storage.exists()) {
            return load(storage);
        }
        JournalSnapshot initial = Objects.requireNonNull(seed.get(), "seed snapshot must not be null");
        storage.save(initial);
        return new JournalSession(storage, new JournalService(initial));
    }

    public static JournalSession empty(JournalStorage storage) {
        return new JournalSession(Objects.requireNonNull(storage, "storage must not be null"), new JournalService());
    }

    public JournalService journal() {
        return service;
    }

    public void addMemory(Memory memory, Set<UUID> constellationIds) throws JournalStorageException {
        service.addMemory(memory);
        updateMemberships(memory.id(), constellationIds);
        save();
    }

    public void updateMemory(Memory memory, Set<UUID> constellationIds) throws JournalStorageException {
        service.updateMemory(memory);
        updateMemberships(memory.id(), constellationIds);
        save();
    }

    public void deleteMemory(UUID memoryId) throws JournalStorageException {
        service.deleteMemory(memoryId);
        save();
    }

    public Constellation createConstellation(String name, String description) throws JournalStorageException {
        Constellation constellation = service.createConstellation(name, description);
        save();
        return constellation;
    }

    public Constellation renameConstellation(UUID id, String name) throws JournalStorageException {
        Constellation constellation = service.renameConstellation(id, name);
        save();
        return constellation;
    }

    public void deleteConstellation(UUID id) throws JournalStorageException {
        service.deleteConstellation(id);
        save();
    }

    public void setConstellationMembership(UUID constellationId, UUID memoryId, boolean included)
            throws JournalStorageException {
        service.setConstellationMembership(constellationId, memoryId, included);
        save();
    }

    public void save() throws JournalStorageException {
        storage.save(service.snapshot());
    }

    public void clearJournal() throws JournalStorageException {
        service.clear();
        save();
    }

    public void updateStarPosition(UUID memoryId, StarPosition position) throws JournalStorageException {
        service.updateStarPosition(memoryId, position);
        save();
    }

    private void updateMemberships(UUID memoryId, Set<UUID> selectedIds) {
        Objects.requireNonNull(selectedIds, "constellationIds must not be null");
        service.constellations().forEach(constellation -> service.setConstellationMembership(
                constellation.id(), memoryId, selectedIds.contains(constellation.id())));
    }
}
