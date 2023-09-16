package ru.BouH.engine.physics.entities.prop;

import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.collision.objects.OBB;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.world.World;
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

    protected void onRigidBodyCreated(btRigidBody rigidBody) {
        super.onRigidBodyCreated(rigidBody);
        this.getPhysicsProperties().setWeight(1.0f);
        //this.getPhysicsProperties().activateRealisticInertia();
    }

    @Override
    protected AbstractCollision constructCollision(double scale) {
        return new OBB(this.getScale(), new Vector3d(this.getSize().x, this.getSize().y, this.getSize().z), 1.0f, this.defaultMotionState, new btVector3(0.0f, 0.0f, 0.0f));
    }
}
