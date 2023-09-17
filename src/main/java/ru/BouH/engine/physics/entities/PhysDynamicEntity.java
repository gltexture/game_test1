package ru.BouH.engine.physics.entities;

import org.joml.Vector3d;
import ru.BouH.engine.physics.world.object.JBulletDynamic;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;

public abstract class PhysDynamicEntity extends PhysEntity implements IWorldDynamic, JBulletDynamic {

    public PhysDynamicEntity(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
    }

    public PhysDynamicEntity(World world, Vector3d pos, Vector3d rot) {
        super(world, pos, rot);
    }

    public PhysDynamicEntity(World world, Vector3d pos, String name) {
        super(world, pos, name);
    }

    public PhysDynamicEntity(World world, Vector3d pos) {
        super(world, pos);
    }

    public PhysDynamicEntity(World world) {
        super(world);
    }

    @Override
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
}
