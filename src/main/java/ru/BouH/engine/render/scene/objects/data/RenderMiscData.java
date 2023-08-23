package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;

public class RenderMiscData extends RenderData {
    public RenderMiscData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, worldItemTexture, clazz);
    }

    public RenderMiscData(RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, new WorldItemTexture(sample), clazz);
    }

    public RenderMiscData(RenderFabric renderFabric, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, WorldItemTexture.standardError, clazz);
    }
}
