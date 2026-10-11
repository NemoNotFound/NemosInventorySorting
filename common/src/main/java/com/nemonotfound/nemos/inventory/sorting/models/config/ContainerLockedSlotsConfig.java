package com.nemonotfound.nemos.inventory.sorting.models.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Locked slots of storage containers (chests, barrels, shulker boxes, ...).
 * <p>
 * Unlike the player inventory, a container has no fixed identity, so the locks are stored per container key
 * (see {@code AbstractContainerScreenMixin#nemosInventorySorting$resolveContainerKey}).
 * The slot index is the index inside the container, starting at 0.
 */
public class ContainerLockedSlotsConfig {

    public static ContainerLockedSlotsConfig INSTANCE = new ContainerLockedSlotsConfig();

    private final Map<String, Set<Integer>> lockedSlots = new HashMap<>();

    /**
     * Key of the container that is currently open. Not persisted.
     */
    private transient String currentContainerKey;

    private ContainerLockedSlotsConfig() {
    }

    public void setCurrentContainerKey(String containerKey) {
        this.currentContainerKey = containerKey;
    }

    public boolean hasCurrentContainer() {
        return currentContainerKey != null;
    }

    public boolean isLocked(int slotIndex) {
        if (currentContainerKey == null) {
            return false;
        }

        var slots = lockedSlots.get(currentContainerKey);

        return slots != null && slots.contains(slotIndex);
    }

    public Set<Integer> getLockedSlots() {
        if (currentContainerKey == null) {
            return Set.of();
        }

        var slots = lockedSlots.get(currentContainerKey);

        return slots == null ? Set.of() : Set.copyOf(slots);
    }

    /**
     * Locks the slot if it is unlocked and unlocks it otherwise.
     *
     * @return true if the slot is locked afterwards
     */
    public boolean toggle(int slotIndex) {
        if (currentContainerKey == null) {
            return false;
        }

        var slots = lockedSlots.computeIfAbsent(currentContainerKey, key -> new TreeSet<>());

        if (slots.remove(slotIndex)) {
            if (slots.isEmpty()) {
                lockedSlots.remove(currentContainerKey);
            }

            return false;
        }

        slots.add(slotIndex);

        return true;
    }
}
