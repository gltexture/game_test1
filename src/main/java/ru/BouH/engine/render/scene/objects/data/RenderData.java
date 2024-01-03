package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.lang.reflect.InvocationTargetException;

public abstract class RenderData {
    private final RenderFabric renderFabric;
    private final ShaderManager shaderManager;
    private final Class<? extends PhysicsObject> aClass;
    private WorldItemTexture worldItemTexture;
    private RenderProperties renderProperties;

    public RenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager) {
        this(renderFabric, worldItemTexture, clazz, shaderManager, RenderProperties.defaultRenderProperties());
    }

    public RenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysicsObject> clazz, ShaderManager shaderManager, RenderProperties renderProperties) {
        this.renderFabric = renderFabric;
        this.shaderManager = shaderManager;
        this.worldItemTexture = worldItemTexture;
        this.renderProperties = renderProperties;
        this.aClass = clazz;
    }

    public RenderData attachNormalMap(String mapPath) {
        this.getItemTexture().setNormalMap(mapPath);
        return this;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public Vector2d getTextureScaling() {
        return this.getRenderProperties().getTextureScaling();
    }

    public RenderData setTextureScaling(Vector2d textureScaling) {
        this.getRenderProperties().setTextureScaling(textureScaling);
        return this;
    }

    public RenderData attachNormalMap(PictureSample pictureSample) {
        this.getItemTexture().setNormalMap(pictureSample);
        return this;
    }

    public RenderData attachNormalMap(PNGTexture pngTexture) {
        this.getItemTexture().setNormalMap(pngTexture);
        return this;
    }

    public boolean hasNormalMap() {
        return this.getItemTexture().hasNormalMap();
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    public RenderData setRenderProperties(RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    public PhysicsObject getPhysRender(SceneWorld sceneWorld, WorldItem worldItem) {
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderData.class).newInstance(sceneWorld, worldItem, this.copyRenderData());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract RenderData copyRenderData();

    protected Class<? extends PhysicsObject> getPhysObjectClass() {
        return this.aClass;
    }

    public RenderData setTexture(Sample sample) {
        this.setWorldItemTexture(new WorldItemTexture(sample));
        return this;
    }

    public RenderData setTexture(Sample sample, String normalMap) {
        this.setWorldItemTexture(new WorldItemTexture(sample, normalMap));
        return this;
    }

    public void setWorldItemTexture(WorldItemTexture worldItemTexture) {
        this.worldItemTexture = worldItemTexture;
    }

    public WorldItemTexture getItemTexture() {
        return this.worldItemTexture;
    }

    public RenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    public static class RenderProperties {
        private final Vector2d textureScaling;
        private boolean lightExposed;
        private boolean lerpPosition;
        private boolean lerpRotation;

        public RenderProperties(boolean lightExposed, boolean lerpPosition, boolean lerpRotation) {
            this(new Vector2d(1.0d), lightExposed, lerpPosition, lerpRotation);
        }

        public RenderProperties(Vector2d textureScaling, boolean lightExposed, boolean lerpPosition, boolean lerpRotation) {
            this.lightExposed = lightExposed;
            this.lerpPosition = lerpPosition;
            this.lerpRotation = lerpRotation;
            this.textureScaling = textureScaling;
        }

        public static RenderProperties defaultRenderProperties() {
            return new RenderProperties(true, true, true);
        }

        public RenderProperties copyProperties() {
            return new RenderProperties(this.getTextureScaling(), this.isLightExposed(), this.isLerpPosition(), this.isLerpRotation());
        }

        public Vector2d getTextureScaling() {
            return new Vector2d(this.textureScaling);
        }

        public void setTextureScaling(Vector2d textureScaling) {
            this.textureScaling.set(textureScaling);
        }

        public boolean isLerpPosition() {
            return this.lerpPosition;
        }

        public void setLerpPosition(boolean lerpPosition) {
            this.lerpPosition = lerpPosition;
        }

        public boolean isLerpRotation() {
            return this.lerpRotation;
        }

        public void setLerpRotation(boolean lerpRotation) {
            this.lerpRotation = lerpRotation;
        }

        public boolean isLightExposed() {
            return this.lightExposed;
        }

        public void setLightExposed(boolean lightExposed) {
            this.lightExposed = lightExposed;
        }
    }
}
