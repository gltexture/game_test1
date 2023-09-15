package ru.BouH.engine.physics.entities;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btConvexShape;
import org.bytedeco.bullet.BulletCollision.btPersistentManifold;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btMatrix3x3;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.physics.world.object.IDynamic;
import ru.BouH.engine.proxy.IWorld;

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

    public PhysEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.physicsProperties = new PhysicsProperties();
        this.defaultCCDParams = new Vector2d(3.5e-2d, 0.05d);
        this.explicitCCDParams = new Vector2d(1.0e-4d, 0.01d);
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
            if (this.getObjectSpeed() >= 10.0f) {
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
            if (this.getPosition().y <= -10 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setObjectVelocity(new Vector3d(0.0d));
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
        btVector3 vector3f = this.getRigidBody().getLinearVelocity();
        return new Vector3d(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public void setObjectVelocity(Vector3d vector3d) {
        this.getRigidBody().setLinearVelocity(new btVector3(vector3d.x, vector3d.y, vector3d.z));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public void addObjectVelocity(Vector3d vector3d) {
        Vector3d vel = this.getObjectVelocity();
        vel.add(vector3d);
        this.getRigidBody().setLinearVelocity(new btVector3(vel.x, vel.y, vel.z));
        this.updateCollisionObjectState(this.getRigidBody());
    }

    public PhysicsProperties getPhysicsProperties() {
        return this.physicsProperties;
    }

    public void setPhysicsProperties(PhysicsProperties physicsProperties) {
        this.physicsProperties = physicsProperties;
        this.refreshObjectProperties(physicsProperties);
    }

    protected void onRigidBodyCreated(btRigidBody rigidBody) {
        this.setRigidBodyProperties(this.getPhysicsProperties(), rigidBody);
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public void setRigidBodyProperties(PhysicsProperties physicsProperties, @NotNull btRigidBody rigidBody) {
        rigidBody.setFriction(physicsProperties.getFriction());
        rigidBody.setDamping(physicsProperties.getLDamping(), physicsProperties.getADamping());
        rigidBody.setRestitution(physicsProperties.getRestitution());
        rigidBody.setMassProps(physicsProperties.getWeight(), new btVector3(physicsProperties.getInertia()));
        rigidBody.updateInertiaTensor();
    }

    public void enableExplicitCollisionDetection(btRigidBody rigidBody) {
        this.enableCCD(rigidBody);
    }

    public void disableExplicitCollisionDetection(btRigidBody rigidBody) {
        this.disableCCD(rigidBody);
    }

    protected void enableCCD(btRigidBody rigidBody) {
        rigidBody.setCcdMotionThreshold((float) this.explicitCCDParams.x);
        rigidBody.setCcdSweptSphereRadius((float) this.explicitCCDParams.y);
    }

    protected void disableCCD(btRigidBody rigidBody) {
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

        btVector3 v1 = new btVector3();
        btVector3 v2 = new btVector3();
        this.getRigidBody().getAabb(v1, v2);

        btTransform transform_m = this.getRigidBody().getWorldTransform();
        btTransform transform1 = new btTransform();
        btTransform transform2 = new btTransform();

        final double f1 = Math.min(v2.getY() - v1.getY(), 0.03f);

        transform1.setIdentity();
        transform1.getOrigin().setValue(this.getPosition().x, v1.getY() + f1 * 2, this.getPosition().z);
        transform1.setRotation(new btQuaternion(transform_m.getRotation()));

        transform2.setIdentity();
        transform2.setOrigin(new btVector3(this.getPosition().x, v1.getY(), this.getPosition().z));
        transform2.setRotation(new btQuaternion(transform_m.getRotation()));

        //btConvexShape convexShape = new btBoxShape(new btVector3(Math.abs(v2.getX() - v1.getX()) * 0.5f, f1, Math.abs(v2.getZ() - v1.getZ()) * 0.5f));
        //btCollisionWorld.ClosestConvexResultCallback closestConvexResultCallback = new btCollisionWorld.ClosestConvexResultCallback(transform1.getOrigin(), transform2.getOrigin());
        //this.getWorld().getBulletTimer().dynamicsWorld().convexSweepTest(convexShape, transform1, transform2, closestConvexResultCallback);

        this.isOnGround = true;
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

    protected void applyFrictionalForce(btRigidBody rigidBody) {
        Vector3d vector3d = this.getObjectVelocity().mul(new Vector3d(1, 0, 1));
        btVector3 vector3f = new btVector3(vector3d.x, vector3d.y, vector3d.z);
        vector3f.multiplyPut(-this.getFrictionalForce());
        rigidBody.applyCentralForce(vector3f);
    }

    public Vector3d getLookVector() {
        double x = Math.toRadians(this.getRotation().x);
        double y = Math.toRadians(this.getRotation().y);
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

        public btVector3 getInertia() {
            return new btVector3(this.inertia.x, this.inertia.y, this.inertia.z);
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
