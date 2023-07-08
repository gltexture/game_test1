package ru.BouH.engine.physx.entities.prop;

import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;

public class PhysEntityProjector extends PhysEntity {

    public PhysEntityProjector(World world) {
        super(world);
        this.setCollisionBox3D(new CollisionBox3D(this, 1, 1));
    }

    public void updateEntity() {
        super.updateEntity();
        this.getCollisionBox3D().setBox(this, 1, 1);
    }
}
