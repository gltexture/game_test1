package ru.BouH.engine.physics.world.object;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btDefaultMotionState;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

public abstract class CollidableWorldItem extends WorldItem implements JBulletEntity {
    private RigidBodyObject rigidBodyObject;
    private final Vector3d startTranslation;
    private final Vector3d startRotation;
    private RigidBodyConstructor rigidBodyConstructor;
    private final RigidBodyObject.PhysProperties properties;

    public CollidableWorldItem(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d startTranslation, @NotNull Vector3d startRotation, String itemName) {
        super(world, scale, startTranslation, startRotation, itemName);
        this.properties = properties;
        this.startTranslation = startTranslation;
        this.startRotation = startRotation;
    }

    protected abstract AbstractCollision constructCollision();

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.constructRigidBody();
    }

    protected void constructRigidBody() {
        this.createRigidBody(this.getWorld(), this.startTranslation, this.startRotation, this.getScale(), this.properties);
        this.addCallBacks(this.getRigidBodyObject());
    }

    protected RigidBodyConstructor getRigidBodyConstructor() {
        return this.rigidBodyConstructor;
    }

    private void createRigidBody(World world, @NotNull Vector3d position, @NotNull Vector3d rotation, double scaling, RigidBodyObject.PhysProperties properties) {
        this.rigidBodyConstructor = new RigidBodyConstructor(world, startTranslation, startRotation, scaling, this.constructCollision());
        this.rigidBodyObject = this.getRigidBodyConstructor().buildRigidBody(properties);
        world.getBulletTimer().addRigidBodyInWorld(this.getRigidBodyObject());
        this.getRigidBodyObject().setTranslation(position);
        this.getRigidBodyObject().setRotation(rotation);
        this.getRigidBodyObject().updateCollisionObjectState();
    }

    protected void addCallBacks(RigidBodyObject rigidBodyObject) {
    }

    public Vector3d getPosition() {
        return new Vector3d(this.getRigidBodyObject().getTranslation());
    }

    public Vector3d getRotation() {
        return new Vector3d(this.getRigidBodyObject().getRotation());
    }

    public void setPosition(Vector3d vector3d) {
        this.getRigidBodyObject().setTranslation(vector3d);
    }
    public void setRotation(Vector3d vector3d) {
        this.getRigidBodyObject().setRotation(vector3d);
    }

    public RigidBodyObject getRigidBodyObject() {
        return this.rigidBodyObject;
    }

    public void applyCentralForce(Vector3d vector3d) {
        this.getRigidBodyObject().applyCentralForce(vector3d);
    }

    public double getObjectSpeed() {
        return this.getObjectVelocity().length();
    }

    public Vector3d getObjectVelocity() {
        return this.getRigidBodyObject().getObjectLinearVelocity();
    }

    public void setObjectVelocity(Vector3d vector3d) {
        this.getRigidBodyObject().setObjectLinearVelocity(vector3d);
    }

    public void addObjectVelocity(Vector3d vector3d) {
        this.getRigidBodyObject().addObjectLinearVelocity(vector3d);
    }

    public void setScale(double scale) {
        super.setScale(scale);
        if (this.isValid()) {
            this.getRigidBodyObject().setScaling(scale);
        }
    }

    public static class RigidBodyConstructor {
        private final btCollisionShape btCollisionShape;
        private final btMotionState motionState;
        private btRigidBody.btRigidBodyConstructionInfo btRigidBodyConstructionInfo;
        private final World world;

        public RigidBodyConstructor(World world, @NotNull Vector3d position, @NotNull Vector3d rotation, double scaling, AbstractCollision abstractCollision) {
            this.btCollisionShape = abstractCollision.buildCollisionShape(scaling);
            this.motionState = new btDefaultMotionState();
            this.world = world;
        }

        public RigidBodyObject buildRigidBody(RigidBodyObject.PhysProperties physicsProperties) {
            this.btRigidBodyConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(1.0d, this.getMotionState(), this.getBtCollisionShape(), null);
            RigidBodyObject rigidBodyObject1 = new RigidBodyObject(this.world, this.getBtRigidBodyConstructionInfo());
            rigidBodyObject1.setRigidBodyProperties(physicsProperties);
            return rigidBodyObject1;
        }

        public btCollisionShape getBtCollisionShape() {
            return this.btCollisionShape;
        }

        public btMotionState getMotionState() {
            return this.motionState;
        }

        public btRigidBody.btRigidBodyConstructionInfo getBtRigidBodyConstructionInfo() {
            return this.btRigidBodyConstructionInfo;
        }
    }
}