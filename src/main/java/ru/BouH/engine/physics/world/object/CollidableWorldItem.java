package ru.BouH.engine.physics.world.object;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btDefaultMotionState;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.collision.JBulletPhysics;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

public abstract class CollidableWorldItem extends WorldItem implements JBulletPhysics {
    protected final btDefaultMotionState defaultMotionState;
    private AbstractCollision abstractCollision;
    private btRigidBody rigidBody;

    public CollidableWorldItem(World world, Vector3d pos, Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
        this.defaultMotionState = new btDefaultMotionState();
    }

    public CollidableWorldItem(World world, Vector3d pos, String itemName) {
        this(world, pos, new Vector3d(0.0d), itemName);
    }

    public CollidableWorldItem(World world, String itemName) {
        this(world, new Vector3d(0.0d), itemName);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.onCollisionCreate(this.constructCollision(this.getScale()));
    }

    public void updateCollisionObjectState(btCollisionObject collisionObject) {
        if (collisionObject == null) {
            Game.getGame().getLogManager().warn("Entity " + this + " doesn't have active RigidBody!");
        } else {
            this.activateRigidBody();
            this.updateTensorRigidBody();
        }
    }

    public void activateRigidBody() {
        this.getRigidBody().activate();
    }

    public void updateTensorRigidBody() {
        this.getRigidBody().updateInertiaTensor();
    }

    public void onJBUpdate() {
    }

    protected void rotateCollisionObject(btCollisionObject collisionObject, Vector3d angles) {
        btTransform transform = collisionObject.getWorldTransform();
        btQuaternion quaternion = new btQuaternion();
        quaternion.setEulerZYX(angles.z, angles.y, angles.x);
        transform.setRotation(quaternion);
        collisionObject.setWorldTransform(transform);
        this.updateCollisionObjectState(collisionObject);
        this.getWorld().getBulletTimer().updateRigidBodyAabb(this.getRigidBody());
    }

    protected void translateCollisionObject(btCollisionObject collisionObject, Vector3d pos) {
        btTransform transform = collisionObject.getWorldTransform();
        transform.setOrigin(new btVector3(pos.x, pos.y, pos.z));
        collisionObject.setWorldTransform(transform);
        this.updateCollisionObjectState(collisionObject);
        this.getWorld().getBulletTimer().updateRigidBodyAabb(this.getRigidBody());
    }

    protected void onRigidBodyCreated(btRigidBody rigidBody) {
    }

    public void setScale(double scale) {
        if (this.getRigidBody() != null) {
            this.getRigidBody().getCollisionShape().setLocalScaling(new btVector3(scale, scale, scale));
        }
        super.setScale(scale);
    }

    protected btRigidBody createRigidBody(btRigidBody.btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
        return new btRigidBody(rigidBodyConstructionInfo);
    }

    public Vector3d getPosition() {
        return this.getRigidBody() != null ? this.getRigidBodyPos() : super.getPosition();
    }

    public void setPosition(Vector3d vector3d) {
        if (this.getRigidBody() != null) {
            this.translateCollisionObject(this.getRigidBody(), new Vector3d(vector3d.x, vector3d.y, vector3d.z));
        }
    }

    public Vector3d getRotation() {
        return this.getRigidBody() != null ? this.getRigidBodyRot() : super.getRotation();
    }

    public void setRotation(Vector3d vector3d) {
        if (this.getRigidBody() != null) {
            this.rotateCollisionObject(this.getRigidBody(), new Vector3d(vector3d.x, vector3d.y, vector3d.z));
        }
    }

    public void onCollisionCreate(AbstractCollision abstractCollision) {
        if (abstractCollision != null) {
            abstractCollision.refreshCollision();
            this.setCollision(abstractCollision);
        }
    }

    protected abstract AbstractCollision constructCollision(double scale);

    public AbstractCollision getCollision() {
        return this.abstractCollision;
    }

    private void setCollision(AbstractCollision abstractCollision) {
        if (abstractCollision != null) {
            btRigidBody.btRigidBodyConstructionInfo constructionInfo = abstractCollision.getCollisionInfo().getRigidBodyConstructionInfo();
            if (this.getRigidBody() == null) {
                Vector3d trans = this.getPosition();
                Vector3d rotate = this.getRotation();
                this.rigidBody = this.createRigidBody(constructionInfo);
                this.onRigidBodyCreated(this.getRigidBody());
                this.getWorld().getBulletTimer().addRigidBodyInWorld(this.getRigidBody());
                this.setPosition(trans);
                this.setRotation(rotate);
                this.updateCollisionObjectState(this.rigidBody);
                this.abstractCollision = abstractCollision;
            } else {
                if (this.getRigidBody().getCollisionShape() != null) {
                    this.getRigidBody().setCollisionShape(null);
                }
                this.getRigidBody().setCollisionShape(abstractCollision.getCollisionShape());
            }
        }
    }

    @Override
    public btRigidBody getRigidBody() {
        return this.rigidBody;
    }
}
