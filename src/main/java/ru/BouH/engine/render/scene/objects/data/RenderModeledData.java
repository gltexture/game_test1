package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;

public class RenderModeledData extends RenderData {
    private final MeshModel meshModel;

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        super(renderFabric, worldItemTexture, clazz);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        this(renderFabric, new WorldItemTexture(sample), clazz, meshModel);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        this(renderFabric, WorldItemTexture.standardError, clazz, meshModel);
    }

    public MeshModel getMeshModel() {
        return this.meshModel;
    }
}
