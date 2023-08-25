package ru.BouH.engine.render.scene.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.Sample;
import ru.BouH.engine.render.scene.world.SceneWorld;
import java.lang.reflect.InvocationTargetException;

public abstract class RenderData {
    private WorldItemTexture worldItemTexture;
    private final RenderFabric renderFabric;
    private final Class<? extends PhysXObject> aClass;
    private RenderProperties renderProperties;

    public RenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz) {
        this(renderFabric, worldItemTexture, clazz, RenderProperties.defaultRenderProperties());
    }

    public RenderData(RenderFabric renderFabric, @NotNull WorldItemTexture worldItemTexture, @NotNull Class<? extends PhysXObject> clazz, RenderProperties renderProperties) {
        this.renderFabric = renderFabric;
        this.worldItemTexture = worldItemTexture;
        this.renderProperties = renderProperties;
        this.aClass = clazz;
    }

    public RenderData setRenderProperties(RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    public PhysXObject getPhysRender(SceneWorld sceneWorld, WorldItem worldItem) {
        try {
            return this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderData.class).newInstance(sceneWorld, worldItem, this.copyRenderData());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract RenderData copyRenderData();

    protected Class<? extends PhysXObject> getPOClass() {
        return this.aClass;
    }

    public RenderData setTexture(Sample sample) {
        this.setWorldItemTexture(new WorldItemTexture(sample));
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
        private boolean lightExposed;
        private boolean lerpPosition;
        private boolean lerpRotation;

        public RenderProperties(boolean lightExposed, boolean lerpPosition, boolean lerpRotation) {
            this.lightExposed = lightExposed;
            this.lerpPosition = lerpPosition;
            this.lerpRotation = lerpRotation;
        }

        public void setLerpPosition(boolean lerpPosition) {
            this.lerpPosition = lerpPosition;
        }

        public void setLerpRotation(boolean lerpRotation) {
            this.lerpRotation = lerpRotation;
        }

        public static RenderProperties defaultRenderProperties() {
            return new RenderProperties(true, true, true);
        }

        public void setLightExposed(boolean lightExposed) {
            this.lightExposed = lightExposed;
        }

        public boolean isLerpPosition() {
            return this.lerpPosition;
        }

        public boolean isLerpRotation() {
            return this.lerpRotation;
        }

        public boolean isLightExposed() {
            return this.lightExposed;
        }
    }
}
