package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;

public class RenderModeledData extends RenderData {
    private Mesh<Format3D> meshModel;

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, Mesh<Format3D> meshModel, RenderProperties renderProperties) {
        super(renderFabric, worldItemTexture, clazz, shaderManager, renderProperties);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, Mesh<Format3D> meshModel) {
        super(renderFabric, worldItemTexture, clazz, shaderManager);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, Mesh<Format3D> meshModel, RenderProperties renderProperties) {
        this(renderFabric, new WorldItemTexture(sample), clazz, shaderManager, meshModel, renderProperties);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, Mesh<Format3D> meshModel) {
        this(renderFabric, new WorldItemTexture(sample), clazz, shaderManager, meshModel);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, Mesh<Format3D> meshModel) {
        this(renderFabric, WorldItemTexture.standardError, clazz, shaderManager, meshModel);
    }

    public Mesh<Format3D> getMeshModel() {
        return this.meshModel;
    }

    public void setMeshModel(Mesh<Format3D> meshModel) {
        this.meshModel = meshModel;
    }

    @Override
    public RenderData copyRenderData() {
        return new RenderModeledData(this.getRenderFabric(), this.getItemTexture(), this.getPhysObjectClass(), this.getShaderManager(), this.getMeshModel(), this.getRenderProperties().copyProperties());
    }
}
