package ru.BouH.engine.physx.world;

public class WorldItem {
    private static int globalId;
    private final String itemName;
    private final int itemId;

    public WorldItem(String itemName) {
        this.itemName = itemName;
        this.itemId = globalId++;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getItemName() {
        return this.itemName;
    }
}
