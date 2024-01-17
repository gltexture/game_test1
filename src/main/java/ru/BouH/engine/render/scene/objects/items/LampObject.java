package ru.BouH.engine.render.scene.objects.items;

import org.joml.Vector3d;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class LampObject extends EntityObject {
    private Vector3d color;

    public LampObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
        this.color = new Vector3d(1.0d);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getWorldItem() instanceof PhysLightCube) {
            if (this.getWorldItem().hasLight()) {
                ILight light = this.getWorldItem().getLight();
                this.color = light.getLightColor().mul(8.0d);
            }
        }
    }
}
