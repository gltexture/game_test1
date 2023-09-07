package ru.BouH.engine.render.scene.objects.items;

import org.joml.Vector3d;
import ru.BouH.engine.physx.entities.prop.PhysLightCube;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class PhysicsColoredLamp extends EntityPhysicsObject {
    public PhysicsColoredLamp(SceneWorld sceneWorld, WorldItem worldItem, RenderData renderData) {
        super(sceneWorld, worldItem, renderData);
        this.getRenderProperties().setLightExposed(false);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getWorldItem() instanceof PhysLightCube) {
            if (this.getWorldItem().hasLight()) {
                ILight light = this.getWorldItem().getLight();
                Vector3d color = light.getLightColor().mul(8.0d);
                this.getRenderData().setTexture(new Color3FA(color));
            }
        }
    }
}
