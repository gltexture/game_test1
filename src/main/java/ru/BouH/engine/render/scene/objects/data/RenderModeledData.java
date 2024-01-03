package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;

public class RenderModeledData extends RenderData {
    private MeshModel meshModel;

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, MeshModel meshModel, RenderProperties renderProperties) {
        super(renderFabric, worldItemTexture, clazz, shaderManager, renderProperties);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, MeshModel meshModel) {
        super(renderFabric, worldItemTexture, clazz, shaderManager);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, MeshModel meshModel, RenderProperties renderProperties) {
        this(renderFabric, new WorldItemTexture(sample), clazz, shaderManager, meshModel, renderProperties);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, MeshModel meshModel) {
        this(renderFabric, new WorldItemTexture(sample), clazz, shaderManager, meshModel);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, MeshModel meshModel) {
        this(renderFabric, WorldItemTexture.standardError, clazz, shaderManager, meshModel);
    }

    public MeshModel getMeshModel() {
        return this.meshModel;
    }

    public void setMeshModel(MeshModel meshModel) {
        this.meshModel = meshModel;
    }

    @Override
    public RenderData copyRenderData() {
        return new RenderModeledData(this.getRenderFabric(), this.getItemTexture(), this.getPhysObjectClass(), this.getShaderManager(), this.getMeshModel(), this.getRenderProperties().copyProperties());
    }
}
