package ru.BouH.engine.game.resource.assets.models.formats;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

public class Format2D implements IFormat {
    private final Vector2d position;
    private final Vector2d rotation;
    private final Vector2d scale;

    public Format2D(@NotNull Vector2d position, Vector2d rotation, Vector2d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Format2D(Vector2d position, Vector2d rotation) {
        this(position, rotation, new Vector2d(1.0d));
    }

    public Format2D(Vector2d position) {
        this(position, new Vector2d(0.0d), new Vector2d(1.0d));
    }

    public Format2D() {
        this(new Vector2d(0.0d), new Vector2d(0.0d), new Vector2d(1.0d));
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public Vector2d getRotation() {
        return this.rotation;
    }

    public Vector2d getScale() {
        return this.scale;
    }
}
