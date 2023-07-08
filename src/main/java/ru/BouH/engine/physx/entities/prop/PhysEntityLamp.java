package ru.BouH.engine.physx.entities.prop;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.proxy.lights.env.PointLight;

public class PhysEntityLamp extends PhysEntity {
    private PointLight pointLight;

    public PhysEntityLamp(World world) {
        super(world);
        this.setCollisionBox3D(new CollisionBox3D(this, 1, 1));
        this.pointLight = (PointLight) Game.getGame().getProxy().createLight(LightType.POINT_LIGHT);
        this.pointLight.doEnable();
        this.pointLight.setIntensity(3.1f);
    }

    public void updateEntity() {
        super.updateEntity();
        this.getCollisionBox3D().setBox(this, 1, 1);
        if (this.getLight() != null) {
            this.pointLight.setPosition(this.getPosition().x, this.getCollisionBox3D().getMinY() - 0.1f, this.getPosition().z);
        }
    }

    public void onSpawn() {
        this.setScale(0.2f);
        this.setLight(this.pointLight);
    }
}
