package ru.BouH.engine.physics.collision.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.MotionState;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physics.collision.CollisionData;

public abstract class AbstractCollision {
    private final float weight;
    private final MotionState motionState;
    private final BPVector3f inertia;
    private CollisionData collisionData;
    private double scale;

    public AbstractCollision(double scale, float weight, MotionState motionState, BPVector3f inertia) {
        this.scale = scale;
        this.weight = weight;
        this.motionState = motionState;
        this.inertia = inertia;
    }

    public AbstractCollision(float weight, MotionState motionState, BPVector3f inertia) {
        this(1.0d, weight, motionState, inertia);
    }

    private void buildCollision(float weight, CollisionShape collisionShape, MotionState motionState, BPVector3f inertia) {
        this.collisionData = new CollisionData(weight, collisionShape, motionState, inertia);
    }

    public void refreshCollision() {
        this.buildCollision(this.getWeight(), this.buildCollisionShape(scale), this.getMotionState(), this.getInertia());
    }

    public BPVector3f getInertia() {
        return this.inertia;
    }

    public MotionState getMotionState() {
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

    protected abstract CollisionShape buildCollisionShape(double scale);

    public CollisionShape getCollisionShape() {
        return this.getCollisionInfo().getCollisionShape();
    }

    public void setCollisionShape(CollisionShape collisionShape) {
        this.getCollisionInfo().setCollisionShape(collisionShape);
    }

    public CollisionData getCollisionInfo() {
        return this.collisionData;
    }
}
