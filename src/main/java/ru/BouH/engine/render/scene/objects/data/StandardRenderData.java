package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;

public class StandardRenderData extends RenderData {
    public StandardRenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, RenderProperties renderProperties) {
        super(renderFabric, worldItemTexture, clazz, shaderManager, renderProperties);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager) {
        super(renderFabric, worldItemTexture, clazz, shaderManager);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager) {
        super(renderFabric, new WorldItemTexture(sample), clazz, shaderManager);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, RenderProperties renderProperties) {
        super(renderFabric, WorldItemTexture.standardError, clazz, shaderManager, renderProperties);
    }

    public StandardRenderData(RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager) {
        super(renderFabric, WorldItemTexture.standardError, clazz, shaderManager);
    }

    @Override
    public RenderData copyRenderData() {
        return new StandardRenderData(this.getRenderFabric(), this.getItemTexture(), this.getPhysObjectClass(), this.getShaderManager(), this.getRenderProperties().copyProperties());
    }
}
