package ru.BouH.engine.physx.brush;

import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;
import ru.BouH.engine.physx.collision.objects.ConvexShape;
import ru.BouH.engine.physx.world.World;

public class Plane4dBrush extends WorldBrush {
    private final Vector3d[] vertices;

    public Plane4dBrush(World world, Vector3d[] vertices, Vector3d pos, Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
        this.vertices = vertices;
    }

    public Plane4dBrush(World world, Vector3d[] vertices, Vector3d pos, String itemName) {
        this(world, vertices, pos, new Vector3d(0.0d), itemName);
    }

    public Plane4dBrush(World world, Vector3d[] vertices, String itemName) {
        this(world, vertices, new Vector3d(0.0d), itemName);
    }

    public Plane4dBrush(World world, Vector3d[] vertices) {
        this(world, vertices, new Vector3d(0.0d), "plane_4d");
    }

    public Vector3d[] getVertices() {
        return this.vertices;
    }

    @Override
    protected AbstractCollision constructCollision(double size) {
        return new ConvexShape(this.getVertices(), this.defaultMotionState, new BPVector3f(0.0f, 0.0f, 0.0f));
    }
}
