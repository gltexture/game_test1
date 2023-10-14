package ru.BouH.engine.render.scene.objects.items;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.frustum.RenderABB;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class PhysXObject implements IRenderObject, IWorldObject, IWorldDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderData renderData;
    protected Model3D model3D;
    protected Vector3d fixedPosition;
    protected Vector3d fixedRotation;
    protected Vector3d renderPrevPosition;
    protected Vector3d renderPrevRotation;
    protected Vector3d renderPosition;
    protected Vector3d renderRotation;
    private boolean isObjectCulled;
    private boolean isVisible;
    private boolean isDead;
    private ILight light;

    public PhysXObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderData renderData) {
        this.renderABB = new RenderABB();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3d(worldItem.getPosition());
        this.renderRotation = new Vector3d(worldItem.getRotation());
        this.fixedPosition = new Vector3d(this.renderPosition);
        this.fixedRotation = new Vector3d(this.renderRotation);
        this.renderPrevPosition = new Vector3d(this.renderPosition);
        this.renderPrevRotation = new Vector3d(this.renderRotation);
        this.light = null;
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
        this.isVisible = true;
        this.isObjectCulled = false;
    }

    protected abstract Model3D buildObjectModel();

    protected void setModel(Model3D model3D) {
        this.model3D = model3D;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PreRender");
        if (this.getWorldItem().hasLight()) {
            this.addLight(this.getWorldItem().getLight());
        }
        this.setModel(this.buildObjectModel());
        if (this.isHasRender()) {
            this.renderFabric().onStartRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PostRender");
        if (this.hasLight()) {
            this.removeLight();
        }
        if (this.isHasRender()) {
            this.renderFabric().onStopRender(this);
        }
    }

    protected void calcABBSize(RenderABB renderABB, WorldItem worldItem) {
        if (renderABB == null || worldItem == null) {
            return;
        }
        if (this.getWorldItem() instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) this.getWorldItem();
            if (jBulletEntity.isValid()) {
                btVector3 v1 = new btVector3();
                btVector3 v2 = new btVector3();
                jBulletEntity.getRigidBodyObject().getAabb(v1, v2);
                renderABB.setAABBMinMax(MathHelper.convert(v1), MathHelper.convert(v2));
                v1.deallocate();
                v2.deallocate();
            }
        } else {
            Vector3d size = new Vector3d(worldItem.getScale() + 1.0d);
            renderABB.setAABBCenterSize(this.getRenderPosition(), size);
        }
    }

    protected void removeLight() {
        this.getSceneWorld().removeLight(this.getLight());
        this.light = null;
    }

    protected void addLight(ILight light) {
        this.light = light;
        light.doAttachTo(this);
        this.getSceneWorld().addLight(light);
    }

    public boolean hasLight() {
        return this.light != null;
    }

    public ILight getLight() {
        return this.light;
    }

    public RenderABB getRenderABB() {
        return this.renderABB;
    }

    public double getScale() {
        return this.getWorldItem().getScale();
    }

    public boolean shouldInterpolatePos() {
        return this.getRenderProperties().isLerpPosition();
    }

    public boolean shouldInterpolateRot() {
        return this.getRenderProperties().isLerpRotation();
    }

    public RenderData.RenderProperties getRenderProperties() {
        return this.getRenderData().getRenderProperties();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getWorldItem().hasLight() && !this.hasLight()) {
            this.addLight(this.getWorldItem().getLight());
        }
        this.calcABBSize(this.getRenderABB(), this.getWorldItem());
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    private void interpolateVector(double partialTicks, Vector3d vectorCurrent, Vector3d vectorStart, Vector3d vectorEnd, double blend) {
        Vector3d v3 = new Vector3d(vectorStart.lerp(vectorEnd, partialTicks));
        if (blend > 0) {
            vectorCurrent.lerp(v3, 1);
        }
    }

    public void test() {
        this.renderPrevPosition.set(this.getWorldItem().getPrevPosition());
        this.renderPrevRotation.set(this.getFixedRotation());
        this.fixedPosition.set(this.getWorldItem().getPosition());
        this.fixedRotation.set(this.getWorldItem().getRotation());
    }

    public void updateRenderPos(double partialTicks, double blend) {

        if (this.shouldInterpolatePos()) {
            this.interpolateVector(partialTicks, this.renderPosition, this.getRenderPrevPosition(), this.getFixedPosition(), blend);
        } else {
            this.renderPosition.set(this.getFixedPosition());
        }

        if (this.shouldInterpolateRot()) {
            this.interpolateVector(partialTicks, this.renderRotation, this.getRenderPrevRotation(), this.getFixedRotation(), blend);
        } else {
            this.renderRotation.set(this.getFixedRotation());
        }
    }

    public boolean isPlayer() {
        return (this.getWorldItem() instanceof EntityPlayerSP);
    }

    public void checkCulling(FrustumCulling frustumCulling) {
        this.isObjectCulled = !frustumCulling.isInFrustum(this.getRenderABB());
    }

    public boolean isVisible() {
        if (this.isPlayer()) {
            return true;
        }
        return !this.isObjectCulled && this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    protected Vector3d getFixedPosition() {
        return new Vector3d(this.fixedPosition);
    }

    protected Vector3d getFixedRotation() {
        return new Vector3d(this.fixedRotation);
    }

    public Vector3d getRenderPrevPosition() {
        return new Vector3d(this.renderPrevPosition);
    }

    public Vector3d getRenderPrevRotation() {
        return new Vector3d(this.renderPrevRotation);
    }

    public Vector3d getRenderPosition() {
        return new Vector3d(this.renderPosition);
    }

    public Vector3d getRenderRotation() {
        return new Vector3d(this.renderRotation);
    }

    public Model3D getModel3D() {
        return this.model3D;
    }

    @Override
    public RenderFabric renderFabric() {
        return this.getRenderData().getRenderFabric();
    }

    public boolean isHasRender() {
        return this.renderFabric() != null;
    }

    public RenderData getRenderData() {
        return this.renderData;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }

    public boolean isHasModel() {
        return this.isHasRender() && this.getModel3D() != null;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public void setDead() {
        this.isDead = true;
    }

    public boolean isDead() {
        return this.isDead;
    }
}
