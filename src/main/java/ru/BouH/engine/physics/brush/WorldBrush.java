package ru.BouH.engine.physics.brush;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;

public abstract class WorldBrush extends PhysEntity {
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

    protected btRigidBody createRigidBody(btRigidBody.btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
        btRigidBody rigidBody = super.createRigidBody(rigidBodyConstructionInfo);
        rigidBody.setCollisionFlags(btCollisionObject.CF_STATIC_OBJECT);
        return rigidBody;
    }

    public void setRigidBodyProperties(PhysicsProperties physicsProperties, @NotNull btRigidBody rigidBody) {
        rigidBody.setAnisotropicFriction(MathHelper.convert(physicsProperties.getFrictionAxes()), btCollisionObject.CF_ANISOTROPIC_FRICTION);
        rigidBody.setFriction(physicsProperties.getFriction());
        rigidBody.setContactStiffnessAndDamping(physicsProperties.getStiffness(), physicsProperties.getDamping());
        rigidBody.setDamping(physicsProperties.getLDamping(), physicsProperties.getADamping());
        rigidBody.setRestitution(physicsProperties.getRestitution());
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
