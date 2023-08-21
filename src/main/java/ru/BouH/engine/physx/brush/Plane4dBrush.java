package ru.BouH.engine.physx.brush;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;
import ru.BouH.engine.physx.collision.objects.ConvexShape;
import ru.BouH.engine.physx.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Plane4dBrush extends WorldBrush {
    private final Vector3d[] vertices;

    public Plane4dBrush(World world, Vector3d[] vertices, Vector3d pos, String itemName) {
        super(world, pos, new Vector3d(0.0d), itemName);
        this.vertices = vertices;
    }

    public Plane4dBrush(World world, Vector3d[] vertices, String itemName) {
        this(world, vertices, new Vector3d(0.0d), itemName);
    }

    private Vector3d[] reorderVertices(Vector3d[] v) {
        Vector3d min = Stream.of(v).min((a, b) -> (a.x < b.x && a.y < b.y && a.z < b.z) ? 1 : (a.x == b.x && a.y == b.y && a.z == b.z) ? 0 : -1).get();
        Vector3d max = Stream.of(v).max((a, b) -> (a.x < b.x && a.y < b.y && a.z < b.z) ? 1 : (a.x == b.x && a.y == b.y && a.z == b.z) ? 0 : -1).get();
        double minX = min.x;
        double minY = min.y;
        double minZ = min.z;
        double maxX = max.x;
        double maxY = max.y;
        double maxZ = max.z;
        System.out.println(this.getItemId() + " " + min + " " + new Vector3d(maxX, minY, minZ) + " " + new Vector3d(minX, maxY, maxZ) + " " + max);
        return new Vector3d[] {min, new Vector3d(minX, minY, maxZ), new Vector3d(maxX, maxY, minZ), max};
    }

    public Plane4dBrush(World world, Vector3d[] vertices) {
        this(world, vertices, new Vector3d(0.0d), "plane_4d");
    }

    public Vector3d[] getVertices() {
        return this.vertices;
    }

    protected void onRigidBodyCreated(RigidBody rigidBody) {
        super.onRigidBodyCreated(rigidBody);
        rigidBody.setFriction(6.8f);
    }

    @Override
    protected AbstractCollision constructCollision(double size) {
        return new ConvexShape(this.getVertices(), this.defaultMotionState, new BPVector3f(0.0f, 0.0f, 0.0f));
    }
}
