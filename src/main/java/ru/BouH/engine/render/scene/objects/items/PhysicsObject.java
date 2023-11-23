package ru.BouH.engine.render.scene.objects.items;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.brush.WorldBrush;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.frustum.RenderABB;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class PhysicsObject implements IRenderObject, IWorldObject, IWorldDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderData renderData;
    protected Model3D model3D;
    protected Vector3d prevRenderPosition;
    protected Vector3d prevRenderRotation;
    protected Vector3d renderPosition;
    protected Vector3d renderRotation;
    private InterpolationPoints currentPositionInterpolation;
    private InterpolationPoints currentRotationInterpolation;
    private boolean isObjectCulled;
    private boolean isVisible;
    private boolean isDead;
    private ILight light;

    public PhysicsObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderData renderData) {
        this.renderABB = new RenderABB();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3d(worldItem.getPosition());
        this.renderRotation = new Vector3d(worldItem.getRotation());
        this.prevRenderPosition = new Vector3d(worldItem.getPosition());
        this.prevRenderRotation = new Vector3d(worldItem.getRotation());
        this.light = null;
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
        this.isVisible = true;
        this.isObjectCulled = false;
        this.currentPositionInterpolation = new InterpolationPoints(this.getPrevRenderPosition(), this.getFixedPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getPrevRenderRotation(), this.getFixedRotation());
    }

    protected void setModel() {
        this.model3D = null;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PreRender");
        if (this.getWorldItem().hasLight()) {
            this.addLight(this.getWorldItem().getLight());
        }
        this.setModel();
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

    protected Vector3d calcABBSize(WorldItem worldItem) {
        if (worldItem == null) {
            return null;
        }
        if (this.getWorldItem() instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) this.getWorldItem();
            if (jBulletEntity.isValid()) {
                btVector3 v1 = new btVector3();
                btVector3 v2 = new btVector3();
                jBulletEntity.getRigidBodyObject().getAabb(v1, v2);
                Vector3d vector3d = new Vector3d(v2.getX() - v1.getX(), v2.getY() - v1.getY(), v2.getZ() - v1.getZ());
                v1.deallocate();
                v2.deallocate();
                return vector3d;
            }
        }
        return new Vector3d(worldItem.getScale() + 1.0d);
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
        return this.getWorldItem() instanceof WorldBrush ? null : this.renderABB;
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
        if (this.getRenderABB() != null) {
            Vector3d size = this.calcABBSize(this.getWorldItem());
            if (size != null) {
                this.getRenderABB().setAbbForm(this.getRenderPosition(), size);
            }
        }
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public void updateRenderPos(double partialTicks) {
        Vector3d pos = this.getFixedPosition();
        Vector3d rot = this.getFixedRotation();

        if (this.shouldInterpolatePos()) {
            this.renderPosition.set(this.getCurrentPosState().interpolatedPoint(partialTicks));
        } else {
            this.renderPosition.set(pos);
        }

        if (this.shouldInterpolateRot()) {
            Vector3d newRotation = new Vector3d();
            Quaterniond result = getQuaternionInterpolated(partialTicks);
            result.getEulerAnglesXYZ(newRotation);
            this.renderRotation.set(new Vector3d(Math.toDegrees(newRotation.x), Math.toDegrees(newRotation.y), Math.toDegrees(newRotation.z)));
        } else {
            this.renderRotation.set(rot);
        }
    }

    private Quaterniond getQuaternionInterpolated(double partialTicks) {
        Quaterniond start = new Quaterniond();
        Quaterniond end = new Quaterniond();

        start.rotateXYZ(Math.toRadians(this.getCurrentRotState().getStartPoint().x), Math.toRadians(this.getCurrentRotState().getStartPoint().y), Math.toRadians(this.getCurrentRotState().getStartPoint().z));
        end.rotateXYZ(Math.toRadians(this.getCurrentRotState().getEndPoint().x), Math.toRadians(this.getCurrentRotState().getEndPoint().y), Math.toRadians(this.getCurrentRotState().getEndPoint().z));

        Quaterniond res = new Quaterniond();
        start.slerp(end, partialTicks, res);
        return res;
    }

    public void refreshInterpolatingState() {
        this.currentPositionInterpolation = new InterpolationPoints(this.getWorldItem().getPosition(), this.getPrevRenderPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getWorldItem().getRotation(), this.getPrevRenderRotation());
    }

    private InterpolationPoints getCurrentPosState() {
        return this.currentPositionInterpolation;
    }

    private InterpolationPoints getCurrentRotState() {
        return this.currentRotationInterpolation;
    }

    public void checkCulling(FrustumCulling frustumCulling) {
        this.isObjectCulled = !frustumCulling.isInFrustum(this.getRenderABB());
    }

    public boolean isVisible() {
        return !this.isObjectCulled && this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    protected Vector3d getFixedPosition() {
        return this.getWorldItem().getPosition();
    }

    protected Vector3d getFixedRotation() {
        return this.getWorldItem().getRotation();
    }

    public Vector3d getPrevRenderRotation() {
        return new Vector3d(this.prevRenderRotation);
    }

    public void setPrevPos(Vector3d vector3d) {
        this.prevRenderPosition.set(new Vector3d(vector3d));
    }

    public void setPrevRot(Vector3d vector3d) {
        this.prevRenderRotation.set(new Vector3d(vector3d));
    }

    public Vector3d getPrevRenderPosition() {
        return new Vector3d(this.prevRenderPosition);
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

    public static final class InterpolationPoints {
        private final Vector3d startPoint;
        private final Vector3d endPoint;

        public InterpolationPoints(Vector3d startPoint, Vector3d endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Vector3d interpolatedPoint(double partialTicks) {
            Vector3d newP = new Vector3d(this.getEndPoint());
            return newP.lerp(this.getStartPoint(), partialTicks);
        }

        public Vector3d getStartPoint() {
            return this.startPoint;
        }

        public Vector3d getEndPoint() {
            return this.endPoint;
        }
    }
}
