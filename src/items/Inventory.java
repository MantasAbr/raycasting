package items;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Fixed slots in screen order, with selection independent of traversal. */
public class Inventory {
    private final List<InventorySlot> slots;
    private int selectedIndex;

    public Inventory(InventorySlot... slots) {
        this.slots = Collections.unmodifiableList(Arrays.asList(slots.clone()));
    }

    public List<InventorySlot> getSlots() {
        return slots;
    }

    public InventorySlot getCurrentItem() {
        return slots.isEmpty() ? null : slots.get(selectedIndex);
    }

    public void selectRelative(int steps) {
        if (!slots.isEmpty())
            selectedIndex = (int) Math.floorMod((long) selectedIndex + steps, slots.size());
    }

    public InventorySlot getSlotAt(Point point) {
        for (InventorySlot slot : slots) {
            if (slot.getBounds().contains(point))
                return slot;
        }
        return null;
    }

    /** Move contents, keeping slot identity, layout, and selection stable. */
    public void swapItems(InventorySlot from, InventorySlot to) {
        if (!slots.contains(from) || !slots.contains(to))
            throw new IllegalArgumentException("Both slots must belong to this inventory");
        Item item = from.getItem();
        from.setItem(to.getItem());
        to.setItem(item);
    }
}
