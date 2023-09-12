package ru.BouH.engine.physics.world.object;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physics.collision.JBulletPhysics;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

import javax.vecmath.Quat4f;

public abstract class CollidableWorldItem extends WorldItem implements JBulletPhysics {
    protected final DefaultMotionState defaultMotionState;
    private AbstractCollision abstractCollision;
    private RigidBody rigidBody;

    public CollidableWorldItem(World world, Vector3d pos, Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
        this.defaultMotionState = new DefaultMotionState(new Transform());
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

    public void updateCollisionObjectState(CollisionObject collisionObject) {
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

    protected void rotateCollisionObject(CollisionObject collisionObject, Vector3d angles) {
        Transform transform = new Transform();
        Quat4f quat4f = new Quat4f();
        Quaterniond quaterniond = new Quaterniond();
        collisionObject.getWorldTransform(transform);
        quaterniond.rotateXYZ(Math.toRadians(angles.x), Math.toRadians(angles.y), Math.toRadians(angles.z));
        quat4f.set((float) quaterniond.x, (float) quaterniond.y, (float) quaterniond.z, (float) quaterniond.w);
        transform.setRotation(quat4f);
        collisionObject.setWorldTransform(transform);
        this.updateCollisionObjectState(collisionObject);
        this.getWorld().getBulletTimer().updateRigidBodyAabb(this.getRigidBody());
    }

    protected void translateCollisionObject(CollisionObject collisionObject, Vector3d pos) {
        Transform transform = new Transform();
        collisionObject.getWorldTransform(transform);
        transform.origin.set(new BPVector3f((float) (pos.x), (float) (pos.y), (float) (pos.z)));
        collisionObject.setWorldTransform(transform);
        this.updateCollisionObjectState(collisionObject);
        this.getWorld().getBulletTimer().updateRigidBodyAabb(this.getRigidBody());
    }

    protected void onRigidBodyCreated(RigidBody rigidBody) {
    }

    public void setScale(double scale) {
        if (this.getRigidBody() != null) {
            this.onCollisionCreate(this.constructCollision(scale));
        }
        super.setScale(scale);
    }

    protected RigidBody createRigidBody(RigidBodyConstructionInfo rigidBodyConstructionInfo) {
        return new RigidBody(rigidBodyConstructionInfo);
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
            RigidBodyConstructionInfo constructionInfo = abstractCollision.getCollisionInfo().getRigidBodyConstructionInfo();
            if (this.getRigidBody() == null) {
                Vector3d trans = this.getPosition();
                Vector3d rotate = this.getRotation();
                this.rigidBody = this.createRigidBody(constructionInfo);
                this.onRigidBodyCreated(this.getRigidBody());
                this.getWorld().getBulletTimer().addRigidBodyInWorld(this.getRigidBody());
                this.translateCollisionObject(this.rigidBody, trans);
                this.rotateCollisionObject(this.rigidBody, rotate);
                this.abstractCollision = abstractCollision;
                this.updateCollisionObjectState(this.rigidBody);
            } else {
                if (this.getRigidBody().getCollisionShape() != null) {
                    this.getRigidBody().setCollisionShape(null);
                }
                this.getRigidBody().setCollisionShape(abstractCollision.getCollisionShape());
            }
        }
    }

    @Override
    public RigidBody getRigidBody() {
        return this.rigidBody;
    }
}
