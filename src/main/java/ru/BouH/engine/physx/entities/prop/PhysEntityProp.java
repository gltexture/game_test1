package ru.BouH.engine.physx.entities.prop;

import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;

public class PhysEntityProp extends PhysEntity {
    public PhysEntityProp(World world) {
        super(world);
        this.setCollisionBox3D(new CollisionBox3D(this, 0.5f, 0.5f));
    }

    public void updateEntity() {
        super.updateEntity();
        this.getCollisionBox3D().setBox(this, 0.5f, 0.5f);
    }
}
