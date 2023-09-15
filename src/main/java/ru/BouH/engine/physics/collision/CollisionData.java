package ru.BouH.engine.physics.collision;

import org.bytedeco.bullet.BulletCollision.btCollisionShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btMotionState;
import org.bytedeco.bullet.LinearMath.btVector3;

public class CollisionData {
    private final btMotionState motionState;
    private btRigidBody.btRigidBodyConstructionInfo rigidBodyConstructionInfo;
    private btCollisionShape collisionShape;

    public CollisionData(float weight, btCollisionShape collisionShape, btMotionState motionState, btVector3 inertia) {
        this.rigidBodyConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(weight, motionState, collisionShape, inertia);
        this.collisionShape = collisionShape;
        this.motionState = motionState;
    }

    public btMotionState getMotionState() {
        return this.motionState;
    }

    public btCollisionShape getCollisionShape() {
        return this.collisionShape;
    }

    public void setCollisionShape(btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        this.rigidBodyConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(this.getRigidBodyConstructionInfo().m_mass(), this.getRigidBodyConstructionInfo().m_motionState(), collisionShape, this.getRigidBodyConstructionInfo().m_localInertia());
    }

    public btRigidBody.btRigidBodyConstructionInfo getRigidBodyConstructionInfo() {
        return this.rigidBodyConstructionInfo;
    }
}
