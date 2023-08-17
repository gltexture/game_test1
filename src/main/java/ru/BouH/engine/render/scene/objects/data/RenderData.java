package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.ItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.world.SceneWorld;
import java.lang.reflect.InvocationTargetException;

public class RenderData {
    private final ItemTexture itemTexture;
    private final RenderFabric renderFabric;
    private final Class<? extends PhysXObject> aClass;

    public RenderData(RenderFabric renderFabric, @NotNull ItemTexture itemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        this.renderFabric = renderFabric;
        this.itemTexture = itemTexture;
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

    public ItemTexture getItemTexture() {
        return this.itemTexture;
    }

    public RenderFabric getRenderFabric() {
        return this.renderFabric;
    }
}
