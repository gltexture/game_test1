package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.ItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public class RenderMiscData extends RenderData {
    public RenderMiscData(RenderFabric renderFabric, @NotNull ItemTexture itemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, itemTexture, clazz);
    }

    public RenderMiscData(RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, new ItemTexture(sample), clazz);
    }

    public RenderMiscData(RenderFabric renderFabric, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, ItemTexture.standardError, clazz);
    }
}
