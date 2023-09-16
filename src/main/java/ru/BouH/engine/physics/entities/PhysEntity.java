package ru.BouH.engine.physics.entities;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
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
    protected final Vector2d defaultCCDParams;
    protected final Vector2d explicitCCDParams;
    private final Vector3d velocityVector;
    protected boolean canJump;
    private PhysicsProperties physicsProperties;
    private float speed;

    public PhysEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.physicsProperties = new PhysicsProperties();
        this.defaultCCDParams = new Vector2d(5.0e-2d, 0.1d);
        this.explicitCCDParams = new Vector2d(1.0e-4d, 0.25d);
        this.velocityVector = new Vector3d(0.0d);
        this.speed = PhysEntity.DEFAULT_SPEED;
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

    protected void addCallBacks(btDynamicsWorld w) {
    }

    public void onJBUpdate() {
        if (this.getRigidBody() != null) {
            this.addObjectVelocity(this.getVelocityVector());
            this.enableExplicitCollisionDetection(this.getRigidBody());
            this.setVelocityVector(new Vector3d(0.0d));
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getRigidBody() != null) {
            if (this.getPosition().y <= -10 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setObjectVelocity(new Vector3d(0.0d));
            }
        }
        this.getPrevPosition().set(this.getPosition());
    }

    private void applyCentralForce(Vector3d vector3d) {
        try (btVector3 btVector3 = new btVector3(vector3d.x, vector3d.y, vector3d.z)) {
            this.getRigidBody().applyCentralForce(btVector3);
        }
    }

    public double getObjectSpeed() {
        return this.getObjectVelocity().length();
    }

    public Vector3d getVelocityVector() {
        return new Vector3d(this.velocityVector);
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
        this.addCallBacks(this.getWorld().getDynamicsWorld());
    }

    public void setRigidBodyProperties(PhysicsProperties physicsProperties, @NotNull btRigidBody rigidBody) {
        rigidBody.setAnisotropicFriction(MathHelper.convert(physicsProperties.getFrictionAxes()), btCollisionObject.CF_ANISOTROPIC_FRICTION);
        rigidBody.setFriction(physicsProperties.getFriction());
        rigidBody.setContactStiffnessAndDamping(physicsProperties.getStiffness(), physicsProperties.getDamping());
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

    public Vector3d getLookVector() {
        double x = Math.toRadians(this.getRotation().x);
        double y = Math.toRadians(this.getRotation().y);
        double lX = MathHelper.sin(y) * MathHelper.cos(x);
        double lY = -MathHelper.sin(x);
        double lZ = -MathHelper.cos(y) * MathHelper.cos(x);
        return new Vector3d(lX, lY, lZ);
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public class PhysicsProperties {
        public static final double DEFAULT_FRICTION_X = 1.0f;
        public static final double DEFAULT_FRICTION_Y = 1.0f;
        public static final double DEFAULT_FRICTION_Z = 1.0f;
        public static final double DEFAULT_FRICTION = 0.5d;
        public static final double DEFAULT_LINEAR_DAMPING = 0.75f;
        public static final double DEFAULT_ANGULAR_DAMPING = 0.75f;
        public static final double DEFAULT_STIFFNESS = Double.MAX_VALUE;
        public static final double DEFAULT_DAMPING = 0.75d;
        public static final double DEFAULT_WEIGHT = 1.0f;
        public static final double DEFAULT_RESTITUTION = 0.15f;

        private Vector3d frictionAxes;
        private double friction;
        private double l_damping;
        private double a_damping;
        private double damping;
        private double stiffness;
        private double weight;
        private double restitution;
        private Vector3d inertia;

        public PhysicsProperties() {
            this.frictionAxes = new Vector3d(PhysicsProperties.DEFAULT_FRICTION_X, PhysicsProperties.DEFAULT_FRICTION_Y, PhysicsProperties.DEFAULT_FRICTION_Z);
            this.l_damping = PhysicsProperties.DEFAULT_LINEAR_DAMPING;
            this.a_damping = PhysicsProperties.DEFAULT_ANGULAR_DAMPING;
            this.friction = PhysicsProperties.DEFAULT_FRICTION;
            this.weight = PhysicsProperties.DEFAULT_WEIGHT;
            this.damping = PhysicsProperties.DEFAULT_DAMPING;
            this.stiffness = PhysicsProperties.DEFAULT_STIFFNESS;
            this.restitution = PhysicsProperties.DEFAULT_RESTITUTION;
            this.inertia = new Vector3d(0.0d);
        }

        public void activateRealisticInertia() {
            btCollisionShape collisionShape = PhysEntity.this.getRigidBody().getCollisionShape();
            btVector3 bpVector3f = new btVector3();
            collisionShape.calculateLocalInertia(this.getWeight(), bpVector3f);
            this.setInertia(new Vector3d(bpVector3f.getX(), bpVector3f.getY(), bpVector3f.getZ()));
            this.refreshRigidBody();
        }

        public void setStiffness(double stiffness) {
            this.stiffness = stiffness;
        }

        public double getStiffness() {
            return this.stiffness;
        }

        public void setDamping(double damping) {
            this.damping = damping;
        }

        public double getDamping() {
            return this.damping;
        }

        private void refreshRigidBody() {
            PhysEntity.this.refreshObjectProperties(this);
        }

        public btVector3 getInertia() {
            return new btVector3(this.inertia.x, this.inertia.y, this.inertia.z);
        }

        public void setFriction(double friction) {
            this.friction = friction;
        }

        public double getFriction() {
            return this.friction;
        }

        public void setInertia(Vector3d inertia) {
            this.inertia = inertia;
            this.refreshRigidBody();
        }

        public double getRestitution() {
            return this.restitution;
        }

        public void setRestitution(float restitution) {
            this.restitution = restitution;
            this.refreshRigidBody();
        }

        public double getWeight() {
            return this.weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
            this.refreshRigidBody();
        }

        public double getLDamping() {
            return this.l_damping;
        }

        public void setLDamping(double damping) {
            this.l_damping = damping;
            this.refreshRigidBody();
        }

        public double getADamping() {
            return this.a_damping;
        }

        public void setADamping(double damping) {
            this.a_damping = damping;
            this.refreshRigidBody();
        }

        public Vector3d getFrictionAxes() {
            return this.frictionAxes;
        }

        public void setFrictionAxes(Vector3d frictionAxes) {
            this.frictionAxes = frictionAxes;
            this.refreshRigidBody();
        }
    }
}
