package ru.BouH.engine.physics.entities;

import org.joml.Vector3d;

public class Materials {
    public static MaterialProperties defaultMaterial = new MaterialProperties("defaultMaterial");
    public static MaterialProperties grassGround = new MaterialProperties("grassGround").setFriction(5.0d).setRestitution(0.0d);
    public static MaterialProperties brickCube = new MaterialProperties("brickCube").setFriction(5.0d).setWeightMultiplier(1.25d).setRestitution(0.0d).setDamping(1.0d);

    public static class MaterialProperties {
        public static final double DEFAULT_FRICTION_X = 1.0d;
        public static final double DEFAULT_FRICTION_Y = 0.0d;
        public static final double DEFAULT_FRICTION_Z = 1.0d;
        public static final double DEFAULT_FRICTION = 0.5d;
        public static final double DEFAULT_LINEAR_DAMPING = 0.35d;
        public static final double DEFAULT_ANGULAR_DAMPING = 0.35d;
        public static final double DEFAULT_STIFFNESS = Double.MAX_VALUE;
        public static final double DEFAULT_DAMPING = 0.5d;
        public static final double DEFAULT_WEIGHT_MULTIPLIER = 1.0d;
        public static final double DEFAULT_RESTITUTION = 0.0d;

        private final String materialName;
        private Vector3d frictionAxes;
        private double friction;
        private double l_damping;
        private double a_damping;
        private double damping;
        private double stiffness;
        private double weightMultiplier;
        private double restitution;

        public MaterialProperties(String materialName) {
            this.materialName = materialName;
            this.frictionAxes = new Vector3d(MaterialProperties.DEFAULT_FRICTION_X, MaterialProperties.DEFAULT_FRICTION_Y, MaterialProperties.DEFAULT_FRICTION_Z);
            this.l_damping = MaterialProperties.DEFAULT_LINEAR_DAMPING;
            this.a_damping = MaterialProperties.DEFAULT_ANGULAR_DAMPING;
            this.friction = MaterialProperties.DEFAULT_FRICTION;
            this.weightMultiplier = MaterialProperties.DEFAULT_WEIGHT_MULTIPLIER;
            this.damping = MaterialProperties.DEFAULT_DAMPING;
            this.stiffness = MaterialProperties.DEFAULT_STIFFNESS;
            this.restitution = MaterialProperties.DEFAULT_RESTITUTION;
        }

        public void writeInPhysicsProperties(PhysEntity.PhysicsProperties physicsProperties) {
            physicsProperties.setDamping(this.getDamping());
            physicsProperties.setFriction(this.getFriction());
            physicsProperties.setFrictionAxes(new Vector3d(this.getFrictionAxes()));
            physicsProperties.setADamping(this.getA_damping());
            physicsProperties.setLDamping(this.getL_damping());
            physicsProperties.setRestitution(this.getRestitution());
            physicsProperties.setStiffness(this.getStiffness());
        }

        public String getMaterialName() {
            return this.materialName;
        }

        public double getStiffness() {
            return this.stiffness;
        }

        public double getDamping() {
            return this.damping;
        }

        public double getFriction() {
            return this.friction;
        }

        public double getRestitution() {
            return this.restitution;
        }

        public Vector3d getFrictionAxes() {
            return this.frictionAxes;
        }

        public double getWeightMultiplier() {
            return this.weightMultiplier;
        }

        public double getA_damping() {
            return this.a_damping;
        }

        public double getL_damping() {
            return this.l_damping;
        }

        private MaterialProperties setStiffness(double stiffness) {
            this.stiffness = stiffness;
            return this;
        }

        private MaterialProperties setFriction(double friction) {
            this.friction = friction;
            return this;
        }

        private MaterialProperties setDamping(double damping) {
            this.damping = damping;
            return this;
        }

        private MaterialProperties setRestitution(double restitution) {
            this.restitution = restitution;
            return this;
        }

        private MaterialProperties setA_damping(double a_damping) {
            this.a_damping = a_damping;
            return this;
        }

        private MaterialProperties setWeightMultiplier(double weightMultiplier) {
            this.weightMultiplier = weightMultiplier;
            return this;
        }

        private MaterialProperties setL_damping(double l_damping) {
            this.l_damping = l_damping;
            return this;
        }

        private MaterialProperties setFrictionAxes(Vector3d frictionAxes) {
            this.frictionAxes = frictionAxes;
            return this;
        }
    }
}
