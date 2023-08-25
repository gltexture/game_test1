package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;

public class StandardRenderData extends RenderData {
    public StandardRenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, worldItemTexture, clazz);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, new WorldItemTexture(sample), clazz);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Class<? extends PhysXObject> clazz) {
        super(renderFabric, WorldItemTexture.standardError, clazz);
    }

    @Override
    public RenderData copyRenderData() {
        return new StandardRenderData(this.getRenderFabric(), this.getItemTexture(), this.getPOClass());
    }
}
