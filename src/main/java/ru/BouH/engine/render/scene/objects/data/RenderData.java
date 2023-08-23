package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.world.SceneWorld;
import java.lang.reflect.InvocationTargetException;

public class RenderData {
    private final WorldItemTexture worldItemTexture;
    private final RenderFabric renderFabric;
    private final Class<? extends PhysXObject> aClass;

    public RenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        this.renderFabric = renderFabric;
        this.worldItemTexture = worldItemTexture;
        this.aClass = clazz;
    }

    public PhysXObject getPhysRender(SceneWorld sceneWorld, WorldItem worldItem) {
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderData.class).newInstance(sceneWorld, worldItem, this);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public RenderData setTexture(Sample sample) {
        this.getItemTexture().setSample(sample);
        return this;
    }

    public WorldItemTexture getItemTexture() {
        return this.worldItemTexture;
    }

    public RenderFabric getRenderFabric() {
        return this.renderFabric;
    }
}
