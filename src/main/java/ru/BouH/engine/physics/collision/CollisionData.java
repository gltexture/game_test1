package ru.BouH.engine.physics.collision;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import ru.BouH.engine.math.BPVector3f;

public class CollisionData {
    private final MotionState motionState;
    private RigidBodyConstructionInfo rigidBodyConstructionInfo;
    private CollisionShape collisionShape;

    public CollisionData(float weight, CollisionShape collisionShape, MotionState motionState, BPVector3f inertia) {
        this.rigidBodyConstructionInfo = new RigidBodyConstructionInfo(weight, motionState, collisionShape, inertia);
        this.collisionShape = collisionShape;
        this.motionState = motionState;
    }

    public MotionState getMotionState() {
        return this.motionState;
    }

    public CollisionShape getCollisionShape() {
        return this.collisionShape;
    }

    public void setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        this.rigidBodyConstructionInfo = new RigidBodyConstructionInfo(this.getRigidBodyConstructionInfo().mass, this.getRigidBodyConstructionInfo().motionState, collisionShape, this.getRigidBodyConstructionInfo().localInertia);
    }

    public RigidBodyConstructionInfo getRigidBodyConstructionInfo() {
        return this.rigidBodyConstructionInfo;
    }
}
