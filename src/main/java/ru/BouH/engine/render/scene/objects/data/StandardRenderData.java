package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;

public class StandardRenderData extends RenderData {
    public StandardRenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, RenderProperties renderProperties) {
        super(renderFabric, worldItemTexture, clazz, renderProperties);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz) {
        super(renderFabric, worldItemTexture, clazz);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz) {
        super(renderFabric, new WorldItemTexture(sample), clazz);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz, RenderProperties renderProperties) {
        super(renderFabric, WorldItemTexture.standardError, clazz, renderProperties);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz) {
        super(renderFabric, WorldItemTexture.standardError, clazz);
    }

    @Override
    public RenderData copyRenderData() {
        return new StandardRenderData(this.getRenderFabric(), this.getItemTexture(), this.getPOClass(), this.getRenderProperties().copyRP());
    }
}
