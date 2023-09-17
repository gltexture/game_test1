package ru.BouH.engine.render.scene.objects.items;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.brush.WorldBrush;
import ru.BouH.engine.physics.world.object.JBulletObject;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.collision.objects.ConvexShape;
import ru.BouH.engine.physics.collision.objects.OBB;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.frustum.RenderABB;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;
import ru.BouH.engine.render.scene.mesh_forms.collision_wire.CollisionOBBoxForm;
import ru.BouH.engine.render.scene.mesh_forms.collision_wire.CollisionPlaneForm;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class PhysXObject implements IRenderObject, IWorldObject, IWorldDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderData renderData;
    protected Model3D model3D;
    private boolean isObjectCulled;
    private boolean isVisible;
    private boolean isDead;
    protected Vector3d renderPosition;
    protected Vector3d renderRotation;
    private ILight light;

    public PhysXObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderData renderData) {
        this.renderABB = new RenderABB();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3d(worldItem.getPosition());
        this.renderRotation = new Vector3d(worldItem.getRotation());
        this.light = null;
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
        this.isVisible = true;
        this.isObjectCulled = false;
    }

    protected void setModel() {
        this.model3D = null;
    }

    public AbstractMeshForm genCollisionMesh() {
        if (this.getWorldItem() instanceof JBulletObject) {
            JBulletObject JBulletObject = (JBulletObject) this.getWorldItem();
            if (JBulletObject.hasCollision()) {
                return this.constructForm(JBulletObject.getCollision());
            }
        }
        return null;
    }

    private AbstractMeshForm constructForm(AbstractCollision iCollision) {
        if (iCollision != null) {
            if (iCollision instanceof OBB) {
                return new CollisionOBBoxForm((OBB) iCollision);
            } else if (iCollision instanceof ConvexShape) {
                ConvexShape c = (ConvexShape) iCollision;
                return new CollisionPlaneForm(c.getPoints()[0], c.getPoints()[1], c.getPoints()[2], c.getPoints()[3]);
            }
        }
        return null;
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
        if (this.getWorldItem() instanceof JBulletObject) {
            JBulletObject jBulletObject = (JBulletObject) this.getWorldItem();
            if (jBulletObject.getRigidBody() != null) {
                btVector3 v1 = new btVector3();
                btVector3 v2 = new btVector3();
                jBulletObject.getRigidBody().getAabb(v1, v2);
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
            this.renderPosition.lerp(pos, partialTicks);
        } else {
            this.renderPosition.set(pos);
        }
        if (this.shouldInterpolateRot()) {
            this.renderRotation.lerp(rot, partialTicks);
        } else {
            this.renderRotation.set(rot);
        }
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
        Vector3d position;
        if (this.getWorldItem() instanceof JBulletObject) {
            JBulletObject jBulletObject = (JBulletObject) this.getWorldItem();
            position = jBulletObject.getRigidBodyPos();
        } else {
            position = this.getWorldItem().getPosition();
        }
        return new Vector3d(position);
    }

    protected Vector3d getFixedRotation() {
        Vector3d rotation;
        if (this.getWorldItem() instanceof JBulletObject) {
            JBulletObject jBulletObject = (JBulletObject) this.getWorldItem();
            rotation = jBulletObject.getRigidBodyRot();
        } else {
            rotation = this.getWorldItem().getRotation();
        }
        return new Vector3d(rotation);
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

    public RenderData getRenderData() {
        return this.renderData;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }

    public boolean isHasRender() {
        return this.renderFabric() != null;
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
