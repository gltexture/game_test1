package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.ItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public class RenderModeledData extends RenderData {
    private final MeshModel meshModel;

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull ItemTexture itemTexture, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        super(renderFabric, itemTexture, clazz);
        this.meshModel = meshModel;
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Sample sample, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        this(renderFabric, new ItemTexture(sample), clazz, meshModel);
    }

    public RenderModeledData(@NotNull RenderFabric renderFabric, @NotNull Class<? extends PhysXObject> clazz, MeshModel meshModel) {
        this(renderFabric, ItemTexture.standardError, clazz, meshModel);
    }

    public MeshModel getMeshModel() {
        return this.meshModel;
    }
}
