package ru.BouH.engine.physics.entities.prop;

import com.bulletphysics.dynamics.RigidBody;
import org.joml.Vector3d;
import ru.BouH.engine.physics.world.World;

public class PhysLightCube extends PhysEntityCube {
    public PhysLightCube(World world, Vector3d size, Vector3d pos, Vector3d rot) {
        super(world, size, pos, rot);
    }

    public PhysLightCube(World world, Vector3d size, Vector3d pos) {
        this(world, size, pos, new Vector3d(0.0d));
    }

    protected void onRigidBodyCreated(RigidBody rigidBody) {
        super.onRigidBodyCreated(rigidBody);
        this.getPhysicsProperties().setWeight(0.01f);
    }
}
