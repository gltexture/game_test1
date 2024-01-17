package ru.BouH.engine.render.scene.preforms;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final RenderFabric renderFabric;
    private final Class<? extends PhysicsObject> aClass;
    private MeshDataGroup meshDataGroup;
    private ShaderManager shaderManager;
    private final Vector2d modelTextureScaling;
    private Material overObjectMaterial;

    public RenderObjectData(RenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull ShaderManager shaderManager) {
        this.aClass = aClass;
        this.shaderManager = shaderManager;
        this.renderFabric = renderFabric;
        this.modelTextureScaling = new Vector2d(1.0d);
        this.overObjectMaterial = null;
    }

    public PhysicsObject constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector2d getModelTextureScaling() {
        return new Vector2d(this.modelTextureScaling);
    }

    public RenderObjectData setModelTextureScaling(Vector2d scale) {
        this.modelTextureScaling.set(scale);
        return this;
    }

    public RenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public RenderObjectData setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public RenderObjectData setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }

    public RenderObjectData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        return this;
    }

    public Class<? extends PhysicsObject> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        return new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getShaderManager()).setMeshDataGroup(this.getMeshDataGroup()).setModelTextureScaling(this.getModelTextureScaling()).setOverObjectMaterial(this.getOverObjectMaterial());
    }
}
