package ru.BouH.engine.physx.entities.prop;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import org.joml.Vector3d;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;
import ru.BouH.engine.physx.collision.objects.OBB;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.proxy.IWorld;

public class PhysEntityCube extends PhysEntity {
    private final Vector3d size;

    public PhysEntityCube(World world, Vector3d size, Vector3d pos, Vector3d rot) {
        super(world, pos, rot, "en_prop");
        this.size = size;
    }

    public PhysEntityCube(World world, Vector3d size, Vector3d pos) {
        this(world, size, pos, new Vector3d(0.0d));
    }

    public Vector3d getSize() {
        return this.size;
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    protected void onRigidBodyCreated(RigidBody rigidBody) {
        super.onRigidBodyCreated(rigidBody);
        this.getPhysicsProperties().setFriction(3.0f);
        this.getPhysicsProperties().setWeight(0.1f);
        CollisionShape collisionShape = rigidBody.getCollisionShape();
        BPVector3f bpVector3f = new BPVector3f(0);
        collisionShape.calculateLocalInertia(this.getPhysicsProperties().getWeight(), bpVector3f);
        this.getPhysicsProperties().setInertia(new Vector3d(0));
    }

    @Override
    protected AbstractCollision constructCollision(double scale) {
        return new OBB(this.getScale(), new Vector3d(this.getSize().x, this.getSize().y, this.getSize().z), 1.0f, this.defaultMotionState, new BPVector3f(0.0f, 0.0f, 0.0f));
    }
}
