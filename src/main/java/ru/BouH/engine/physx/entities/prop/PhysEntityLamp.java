package ru.BouH.engine.physx.entities.prop;

import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;

public class PhysEntityLamp extends PhysEntity {
    public PhysEntityLamp(World world) {
        super(world);
        this.setCollisionBox3D(new CollisionBox3D(this, 1, 1));
    }

    public void updateEntity() {
        super.updateEntity();
        this.getPosition().set(new Vector3d(0, this.getPosition().y + 0.05f, 0));
        if (this.getPosition().y > 50) {
           // this.setDead();
        }
        this.getCollisionBox3D().setBox(this, 1, 1);
    }

    public void onSpawn() {
        this.setScale(0.2f);
    }
}