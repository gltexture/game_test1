package ru.BouH.engine.physx.entities;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.GhostObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.object.CollidableWorldItem;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.proxy.IWorld;

import javax.vecmath.Matrix3f;

public abstract class PhysEntity extends CollidableWorldItem implements IDynamic  {
    public static final float DEFAULT_SPEED = 1.0f;
    public static final float DEFAULT_FRICTIONAL_FORCE = 9.8f;
    private float speed;
    private final Vector3d motionVector;
    private float frictionalForce;
    protected boolean canJump;

    public PhysEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.motionVector = new Vector3d(0.0d);
        this.speed = PhysEntity.DEFAULT_SPEED;
        this.frictionalForce = PhysEntity.DEFAULT_FRICTIONAL_FORCE;
    }

    public PhysEntity(World world, Vector3d pos, Vector3d rot) {
        this(world, pos, rot, "phys_entity");
    }

    public PhysEntity(World world, Vector3d pos, String name) {
        this(world, pos, new Vector3d(0.0d), name);
    }

    public PhysEntity(World world, Vector3d pos) {
        this(world, pos, new Vector3d(0.0d));
    }

    public PhysEntity(World world) {
        this(world, new Vector3d(0.0d));
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.getPrevPosition().set(this.getPosition());
        if (this.getRigidBody() != null) {
            if (this.isMotionIsActive()) {
                this.addVelocity(this.getSpeedVector());
            }
            this.applyFrictionalForce(this.getRigidBody());
            if (this.isOnGround()) {
                if (this.getVelocity().y <= 0.1f) {
                    this.canJump = true;
                }
            }
            if (this.getPosition().y <= -100) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setVelocity(new Vector3d(0.0d));
            }
        }
        this.motionVector.set(0.0d);
    }

    public boolean isOnGround() {
        if (this.getRigidBody() == null) {
            return false;
        }
        BPVector3f v1 = new BPVector3f();
        BPVector3f v2 = new BPVector3f();
        this.getRigidBody().getAabb(v1, v2);

        Transform transform_m = new Transform();
        this.getRigidBody().getWorldTransform(transform_m);
        Matrix3f bs = transform_m.basis;

        final float f1 = Math.min(v2.y - v1.y, 0.05f);
        Transform transform1 = new Transform();
        transform1.origin.set((float) this.getPosition().x, v1.y + f1, (float) this.getPosition().z);
        transform1.basis.set(bs);

        Transform transform2 = new Transform();
        transform2.origin.set((float) this.getPosition().x, v1.y, (float) this.getPosition().z);
        transform2.basis.set(bs);

        ConvexShape convexShape = new BoxShape(new BPVector3f((v2.x - v1.x) * 0.5f, f1, (v2.z - v1.z) * 0.5f));
        CollisionWorld.ConvexResultCallback closestConvexResultCallback = new CollisionWorld.ClosestConvexResultCallback(transform1.origin, transform2.origin);
        this.getWorld().getPhysXBulletManager().collisionWorld().convexSweepTest(convexShape, transform1, transform2, closestConvexResultCallback);

        return closestConvexResultCallback.hasHit();
    }

    public void jump() {
        if (this.canJump) {
            this.addVelocity(new Vector3d(0.0d, 5.0d, 0.0d));
            this.canJump = false;
        }
    }

    public boolean isMotionIsActive() {
        double x = this.getMotionVector().x;
        double y = this.getMotionVector().y;
        double z = this.getMotionVector().z;
        return x + y + z != 0;
    }

    public void setFrictionalForce(float frictionalForce) {
        this.frictionalForce = frictionalForce;
    }

    public float getFrictionalForce() {
        return this.isOnGround() ? this.frictionalForce : this.frictionalForce * 0.25f;
    }

    protected void applyFrictionalForce(RigidBody rigidBody) {
        BPVector3f vector3f = new BPVector3f(this.getVelocity().mul(new Vector3d(1, 0, 1)));
        vector3f.scale(-this.getFrictionalForce());
        rigidBody.applyCentralForce(vector3f);
    }

    public Vector3d getVelocity() {
        BPVector3f vector3f = new BPVector3f();
        this.getRigidBody().getLinearVelocity(vector3f);
        return new Vector3d(vector3f.x, vector3f.y, vector3f.z);
    }

    public void addVelocity(Vector3d vector3d) {
        Vector3d vel = this.getVelocity();
        vel.add(vector3d);
        this.getRigidBody().setLinearVelocity(new BPVector3f(vel));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public void setVelocity(Vector3d vector3d) {
        this.getRigidBody().setLinearVelocity(new BPVector3f(vector3d));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public Vector3d getMotionVector() {
        return new Vector3d(this.motionVector);
    }

    public Vector3d getLookVector() {
        Vector3d vector3d = new Vector3d(this.getRotation());
        if (vector3d.length() > 0) {
            vector3d.normalize();
        }
        return vector3d;
    }

    public void setMotionVector(Vector3d vector3d) {
        this.motionVector.set(vector3d);
        if (this.motionVector.length() > 0) {
            this.motionVector.normalize();
        }
    }

    public float getSpeed() {
        return this.isOnGround() ? this.speed : this.speed * 0.1f;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector3d getSpeedVector() {
        return this.getMotionVector().mul(this.getSpeed());
    }
}
