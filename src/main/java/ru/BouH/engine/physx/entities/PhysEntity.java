package ru.BouH.engine.physx.entities;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.math.jbullet.JBUtils;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.object.CollidableWorldItem;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.proxy.IWorld;

import javax.vecmath.Matrix3f;

public abstract class PhysEntity extends CollidableWorldItem implements IDynamic {
    public static final float DEFAULT_SPEED = 1.0f;
    public static final float DEFAULT_FRICTIONAL_FORCE = 24.8f;
    protected final Vector2d defaultCCDParams;
    protected final Vector2d explicitCCDParams;
    private final Vector3d velocityVector;
    protected boolean canJump;
    private PhysicsProperties physicsProperties;
    private float speed;
    private float frictionalForce;
    private boolean isOnGround;
    private int ticksBeforeCanJump;
    private boolean activationRecheck;

    public PhysEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.physicsProperties = new PhysicsProperties();
        this.defaultCCDParams = new Vector2d(2.5e-2f, 0.5f);
        this.explicitCCDParams = new Vector2d(1.0e-3d, 0.2f);
        this.velocityVector = new Vector3d(0.0d);
        this.speed = PhysEntity.DEFAULT_SPEED;
        this.frictionalForce = PhysEntity.DEFAULT_FRICTIONAL_FORCE;
        this.canJump = false;
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

    public void onJBUpdate() {
        if (this.getRigidBody() != null) {
            this.addObjectVelocity(this.getVelocityVector());
            if (this.getObjectSpeed() >= 20.0f) {
                this.enableExplicitCollisionDetection(this.getRigidBody());
            } else {
                this.disableExplicitCollisionDetection(this.getRigidBody());
            }
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.groundCheck();
        if (this.isOnGround()) {
            if (this.ticksBeforeCanJump-- > 0) {
                this.ticksBeforeCanJump -= 1;
            } else {
                this.canJump = true;
            }
        } else {
            if (this.canJump) {
                this.ticksBeforeCanJump = 50;
            }
            this.canJump = false;
        }
        if (this.getRigidBody() != null) {
            if (this.getPosition().y <= -100 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setObjectVelocity(new Vector3d(0.0d));
            }
            if (this.activationRecheck) {
                this.checkCollisionAndActivate(this.getWorld().getBulletTimer().collisionWorld());
            } else {
                if (!this.getRigidBody().isActive()) {
                    if (!this.isOnGround()) {
                        this.getRigidBody().activate();
                    } else {
                        this.activationRecheck = true;
                    }
                }
            }
        }
        this.getPrevPosition().set(this.getPosition());
        this.setVelocityVector(new Vector3d(0.0d));
    }

    public double getObjectSpeed() {
        return this.getObjectVelocity().length();
    }

    public Vector3d getVelocityVector() {
        Vector3d vector3d = new Vector3d(this.velocityVector);
        if (!this.isOnGround()) {
            vector3d.mul(0.1f);
        }
        return vector3d;
    }

    public void setVelocityVector(Vector3d vector3d) {
        this.velocityVector.set(vector3d);
    }

    public Vector3d getObjectVelocity() {
        BPVector3f vector3f = new BPVector3f();
        this.getRigidBody().getLinearVelocity(vector3f);
        return new Vector3d(vector3f.x, vector3f.y, vector3f.z);
    }

    public void setObjectVelocity(Vector3d vector3d) {
        this.getRigidBody().setLinearVelocity(new BPVector3f(vector3d));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public void addObjectVelocity(Vector3d vector3d) {
        Vector3d vel = this.getObjectVelocity();
        vel.add(vector3d);
        this.getRigidBody().setLinearVelocity(new BPVector3f(vel));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public void checkCollisionAndActivate(CollisionWorld collisionWorld) {
        int numManifolds = collisionWorld.getDispatcher().getNumManifolds();
        for (int i = 0; i < numManifolds; i++) {
            PersistentManifold contactManifold = collisionWorld.getDispatcher().getManifoldByIndexInternal(i);
            if (contactManifold != null && contactManifold.getNumContacts() > 0) {
                RigidBody obj0 = (RigidBody) contactManifold.getBody0();
                RigidBody obj1 = (RigidBody) contactManifold.getBody1();
                if (obj0 == this.getRigidBody() && !obj1.isStaticObject() && obj1.isActive()) {
                    this.getRigidBody().activate();
                    this.activationRecheck = false;
                    break;
                }
            }
        }
    }

    public PhysicsProperties getPhysicsProperties() {
        return this.physicsProperties;
    }

    public void setPhysicsProperties(PhysicsProperties physicsProperties) {
        this.physicsProperties = physicsProperties;
        this.refreshObjectProperties(physicsProperties);
    }

    protected void onRigidBodyCreated(RigidBody rigidBody) {
        this.setRigidBodyProperties(this.getPhysicsProperties(), rigidBody);
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public void setRigidBodyProperties(PhysicsProperties physicsProperties, @NotNull RigidBody rigidBody) {
        rigidBody.setFriction(physicsProperties.getFriction());
        rigidBody.setDamping(physicsProperties.getLDamping(), physicsProperties.getADamping());
        rigidBody.setRestitution(physicsProperties.getRestitution());
        rigidBody.setMassProps(physicsProperties.getWeight(), new BPVector3f(physicsProperties.getInertia()));
        rigidBody.updateInertiaTensor();
    }

    public void enableExplicitCollisionDetection(RigidBody rigidBody) {
        this.enableCCD(rigidBody);
    }

    public void disableExplicitCollisionDetection(RigidBody rigidBody) {
        this.disableCCD(rigidBody);
    }

    protected void enableCCD(RigidBody rigidBody) {
        rigidBody.setCcdMotionThreshold((float) this.explicitCCDParams.x);
        rigidBody.setCcdSweptSphereRadius((float) this.explicitCCDParams.y);
    }

    protected void disableCCD(RigidBody rigidBody) {
        rigidBody.setCcdMotionThreshold((float) this.defaultCCDParams.x);
        rigidBody.setCcdSweptSphereRadius((float) this.defaultCCDParams.y);
    }

    public void refreshObjectProperties(PhysicsProperties physicsProperties) {
        if (this.getRigidBody() != null) {
            this.setRigidBodyProperties(physicsProperties, this.getRigidBody());
        }
    }

    protected void groundCheck() {
        if (!this.getRigidBody().isInWorld() || this.getRigidBody() == null) {
            this.isOnGround = false;
            return;
        }
        BPVector3f v1 = new BPVector3f();
        BPVector3f v2 = new BPVector3f();
        this.getRigidBody().getAabb(v1, v2);

        Transform transform_m = new Transform();
        this.getRigidBody().getWorldTransform(transform_m);
        Matrix3f bs = transform_m.basis;

        final float f1 = Math.min(v2.y - v1.y, 0.03f);
        Transform transform1 = new Transform();
        transform1.origin.set((float) this.getPosition().x, v1.y + f1 * 2, (float) this.getPosition().z);
        transform1.basis.set(bs);

        Transform transform2 = new Transform();
        transform2.origin.set((float) this.getPosition().x, v1.y, (float) this.getPosition().z);
        transform2.basis.set(bs);

        ConvexShape convexShape = new BoxShape(new BPVector3f(Math.abs(v2.x - v1.x) * 0.5f, f1, Math.abs(v2.z - v1.z) * 0.5f));
        CollisionWorld.ClosestConvexResultCallback closestConvexResultCallback = new CollisionWorld.ClosestConvexResultCallback(transform1.origin, transform2.origin);
        JBUtils.convexSweepTest(this.getWorld().getBulletTimer().collisionWorld(), convexShape, transform1, transform2, closestConvexResultCallback);

        this.isOnGround = closestConvexResultCallback.hasHit();
    }

    public void jump() {
        if (this.canJump) {
            this.addObjectVelocity(new Vector3d(0.0d, 6.5d, 0.0d));
            this.ticksBeforeCanJump = 20;
            this.canJump = false;
        }
    }

    public float getFrictionalForce() {
        return this.isOnGround() ? this.frictionalForce : this.frictionalForce * 0.5f;
    }

    public void setFrictionalForce(float frictionalForce) {
        this.frictionalForce = frictionalForce;
    }

    protected void applyFrictionalForce(RigidBody rigidBody) {
        BPVector3f vector3f = new BPVector3f(this.getObjectVelocity().mul(new Vector3d(1, 0, 1)));
        vector3f.scale(-this.getFrictionalForce());
        rigidBody.applyCentralForce(vector3f);
    }

    public Vector3d getLookVector() {
        double x = Math.toRadians(this.getRotation().x);
        double y = Math.toRadians(this.getRotation().y);
        double z = Math.toRadians(this.getRotation().z);
        double lX = MathHelper.sin(y) * MathHelper.cos(x);
        double lY = -MathHelper.sin(x);
        double lZ = -MathHelper.cos(y) * MathHelper.cos(x);
        return new Vector3d(lX, lY, lZ);
    }

    public float getSpeed() {
        return this.isOnGround() ? this.speed : this.speed * 1;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public class PhysicsProperties {
        public static final float DEFAULT_FRICTION = 0.5f;
        public static final float DEFAULT_LDAMPING = 0.65f;
        public static final float DEFAULT_ADAMPING = 0.65f;
        public static final float DEFAULT_WEIGHT = 1.0f;
        public static final float DEFAULT_RESTITUTION = 0.25f;

        private float friction;
        private float l_damping;
        private float a_damping;
        private float weight;
        private float restitution;
        private Vector3d inertia;

        public PhysicsProperties() {
            this.friction = PhysicsProperties.DEFAULT_FRICTION;
            this.l_damping = PhysicsProperties.DEFAULT_LDAMPING;
            this.a_damping = PhysicsProperties.DEFAULT_ADAMPING;
            this.weight = PhysicsProperties.DEFAULT_WEIGHT;
            this.restitution = PhysicsProperties.DEFAULT_RESTITUTION;
            this.inertia = new Vector3d(0.0d);
        }

        private void refreshRigidBody() {
            PhysEntity.this.refreshObjectProperties(this);
        }

        public Vector3d getInertia() {
            return this.inertia;
        }

        public void setInertia(Vector3d inertia) {
            this.inertia = inertia;
            this.refreshRigidBody();
        }

        public float getRestitution() {
            return this.restitution;
        }

        public void setRestitution(float restitution) {
            this.restitution = restitution;
            this.refreshRigidBody();
        }

        public float getWeight() {
            return this.weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
            this.refreshRigidBody();
        }

        public float getLDamping() {
            return this.l_damping;
        }

        public void setLDamping(float damping) {
            this.l_damping = damping;
            this.refreshRigidBody();
        }

        public float getADamping() {
            return this.a_damping;
        }

        public void setADamping(float damping) {
            this.a_damping = damping;
            this.refreshRigidBody();
        }

        public float getFriction() {
            return this.friction;
        }

        public void setFriction(float friction) {
            this.friction = friction;
            this.refreshRigidBody();
        }
    }
}
