package ru.BouH.engine.game.resources.assets.shaders;

public class Uniform {
    private final String id;
    private final int arraySize;

    public Uniform(String id, int arraySize) {
        this.id = id;
        this.arraySize = Math.max(arraySize, 1);
    }

    public Uniform(String id) {
        this(id, 1);
    }

    public String getId() {
        return this.id;
    }

    public int getArraySize() {
        return this.arraySize;
    }
}
