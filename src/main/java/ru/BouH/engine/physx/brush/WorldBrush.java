package ru.BouH.engine.physx.brush;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import org.joml.Vector3d;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.object.CollidableWorldItem;

public abstract class WorldBrush extends CollidableWorldItem {
    private boolean isVisible;

    public WorldBrush(World world, Vector3d pos, Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
        this.isVisible = true;
    }

    public WorldBrush(World world, Vector3d pos, String itemName) {
        this(world, pos, new Vector3d(0.0d), itemName);
    }

    public WorldBrush(World world, String itemName) {
        this(world, new Vector3d(0.0d), itemName);
    }

    protected RigidBody createRigidBody(RigidBodyConstructionInfo rigidBodyConstructionInfo) {
        RigidBody rigidBody = super.createRigidBody(rigidBodyConstructionInfo);
        rigidBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
        return rigidBody;
    }

    public boolean isStatic() {
        return true;
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
