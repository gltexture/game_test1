package ru.BouH.engine.physics.collision.objects;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.BouH.engine.physics.collision.CollisionData;

public abstract class AbstractCollision {
    private final float weight;
    private final btMotionState motionState;
    private final btVector3 inertia;
    private CollisionData collisionData;
    private double scale;

    public AbstractCollision(double scale, float weight, btMotionState motionState, btVector3 inertia) {
        this.scale = scale;
        this.weight = weight;
        this.motionState = motionState;
        this.inertia = inertia;
    }

    public AbstractCollision(float weight, btMotionState motionState, btVector3 inertia) {
        this(1.0d, weight, motionState, inertia);
    }

    private void buildCollision(float weight, btCollisionShape collisionShape, btMotionState motionState, btVector3 inertia) {
        this.collisionData = new CollisionData(weight, collisionShape, motionState, inertia);
    }

    public void refreshCollision() {
        this.buildCollision(this.getWeight(), this.buildCollisionShape(scale), this.getMotionState(), this.getInertia());
    }

    public btVector3 getInertia() {
        return this.inertia;
    }

    public btMotionState getMotionState() {
        return this.motionState;
    }

    public float getWeight() {
        return this.weight;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    protected abstract btCollisionShape buildCollisionShape(double scale);

    public btCollisionShape getCollisionShape() {
        return this.getCollisionInfo().getCollisionShape();
    }

    public void setCollisionShape(btCollisionShape collisionShape) {
        this.getCollisionInfo().setCollisionShape(collisionShape);
    }

    public CollisionData getCollisionInfo() {
        return this.collisionData;
    }
}
