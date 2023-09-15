package ru.BouH.engine.physics.brush;

import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.collision.objects.ConvexShape;
import ru.BouH.engine.physics.world.World;

public class Plane4dBrush extends WorldBrush {
    private final Vector3d[] vertices;

    public Plane4dBrush(World world, Vector3d[] vertices, Vector3d pos, String itemName) {
        super(world, pos, new Vector3d(0.0d), itemName);
        this.vertices = vertices;
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

    protected void onRigidBodyCreated(btRigidBody rigidBody) {
        super.onRigidBodyCreated(rigidBody);
        rigidBody.setFriction(6.8f);
    }

    @Override
    protected AbstractCollision constructCollision(double size) {
        return new ConvexShape(this.getVertices(), this.defaultMotionState, new btVector3(0.0f, 0.0f, 0.0f));
    }
}
