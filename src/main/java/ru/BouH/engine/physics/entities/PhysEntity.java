package ru.BouH.engine.physics.entities;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;

public abstract class PhysEntity extends CollidableWorldItem {
    private PhysicsProperties physicsProperties;
    protected final Vector2d explicitCCDParams;
    private final Vector3d velocityVector;
    protected boolean canJump;

    public PhysEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.physicsProperties = new PhysicsProperties();
        this.explicitCCDParams = new Vector2d(1.0e-4d, 0.25d);
        this.velocityVector = new Vector3d(0.0d);
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

    public void applyCentralForce(Vector3d vector3d) {
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
        rigidBody.setMassProps(physicsProperties.getWeight(), physicsProperties.getInertia());
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
        rigidBody.setCcdMotionThreshold(0.0d);
        rigidBody.setCcdSweptSphereRadius(0.0d);
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

    public PhysEntity setMaterial(Materials.MaterialProperties materialProperties) {
        this.getPhysicsProperties().setMaterial(materialProperties);
        return this;
    }

    public void setScale(double scale) {
        super.setScale(scale);
        this.refreshObjectProperties(this.getPhysicsProperties());
    }

    public class PhysicsProperties {
        public static final double DEFAULT_WEIGHT = 1.0f;
        private Materials.MaterialProperties materialProperties;
        private boolean scaleWeight;
        private boolean realisticInertia;
        private Vector3d frictionAxes;
        private double friction;
        private double l_damping;
        private double a_damping;
        private double damping;
        private double stiffness;
        private double weight;
        private double restitution;

        public PhysicsProperties() {
            this.weight = PhysicsProperties.DEFAULT_WEIGHT;
            this.scaleWeight = true;
            this.realisticInertia = false;
            this.setMaterial(Materials.defaultMaterial);
        }

        public void setMaterial(Materials.MaterialProperties materialProperties) {
            this.materialProperties = materialProperties;
            materialProperties.writeInPhysicsProperties(this);
            this.refreshRigidBody();
        }

        public btVector3 calcRealisticInertia() {
            btCollisionShape collisionShape = PhysEntity.this.getRigidBody().getCollisionShape();
            btVector3 bpVector3f = new btVector3();
            collisionShape.calculateLocalInertia(this.getWeight(), bpVector3f);
            return bpVector3f;
        }

        public boolean isRealisticInertia() {
            return this.realisticInertia;
        }

        public void setRealisticInertia(boolean realisticInertia) {
            this.realisticInertia = realisticInertia;
            this.refreshRigidBody();
        }

        public PhysicsProperties setStiffness(double stiffness) {
            this.stiffness = stiffness;
            return this;
        }

        public double getStiffness() {
            return this.stiffness;
        }

        public PhysicsProperties setDamping(double damping) {
            this.damping = damping;
            return this;
        }

        public double getDamping() {
            return this.damping;
        }

        public boolean isScaleWeight() {
            return this.scaleWeight;
        }

        public void setScaleWeight(boolean scaleWeight) {
            this.scaleWeight = scaleWeight;
        }

        private void refreshRigidBody() {
            PhysEntity.this.refreshObjectProperties(this);
        }

        public btVector3 getInertia() {
            return this.isRealisticInertia() ? this.calcRealisticInertia() : new btVector3(0.0d, 0.0d, 0.0d);
        }

        public Materials.MaterialProperties getMaterialProperties() {
            return this.materialProperties;
        }

        public PhysicsProperties setFriction(double friction) {
            this.friction = friction;
            this.refreshRigidBody();
            return this;
        }

        public double getFriction() {
            return this.friction;
        }

        public double getRestitution() {
            return this.restitution;
        }

        public PhysicsProperties setRestitution(double restitution) {
            this.restitution = restitution;
            this.refreshRigidBody();
            return this;
        }

        public double getWeight() {
            return this.weight * this.getMaterialProperties().getWeightMultiplier() * (PhysEntity.this.getScale() * PhysEntity.this.getScale());
        }

        public PhysicsProperties setWeight(double weight) {
            this.weight = weight;
            this.refreshRigidBody();
            return this;
        }

        public double getLDamping() {
            return this.l_damping;
        }

        public PhysicsProperties setLDamping(double damping) {
            this.l_damping = damping;
            this.refreshRigidBody();
            return this;
        }

        public double getADamping() {
            return this.a_damping;
        }

        public PhysicsProperties setADamping(double damping) {
            this.a_damping = damping;
            this.refreshRigidBody();
            return this;
        }

        public Vector3d getFrictionAxes() {
            return this.frictionAxes;
        }

        public PhysicsProperties setFrictionAxes(Vector3d frictionAxes) {
            this.frictionAxes = frictionAxes;
            this.refreshRigidBody();
            return this;
        }
    }
}
