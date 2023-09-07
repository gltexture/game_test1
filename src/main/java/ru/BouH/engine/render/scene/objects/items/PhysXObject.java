package ru.BouH.engine.render.scene.objects.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physx.brush.WorldBrush;
import ru.BouH.engine.physx.collision.JBulletPhysics;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;
import ru.BouH.engine.physx.collision.objects.ConvexShape;
import ru.BouH.engine.physx.collision.objects.OBB;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.physx.world.object.IWorldObject;
import ru.BouH.engine.physx.world.object.WorldItem;
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

public abstract class PhysXObject implements IRenderObject, IWorldObject, IDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderData renderData;
    protected Model3D model3D;
    private boolean blockInterpolation;
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
        if (this.getWorldItem() instanceof JBulletPhysics) {
            JBulletPhysics JBulletPhysics = (JBulletPhysics) this.getWorldItem();
            if (JBulletPhysics.hasCollision()) {
                return this.constructForm(JBulletPhysics.getCollision());
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
        if (this.getWorldItem() instanceof JBulletPhysics && ((JBulletPhysics) this.getWorldItem()).getRigidBody() != null) {
            JBulletPhysics jBulletPhysics = (JBulletPhysics) this.getWorldItem();
            BPVector3f v1 = new BPVector3f();
            BPVector3f v2 = new BPVector3f();
            jBulletPhysics.getRigidBody().getAabb(v1, v2);
            return new Vector3d(1);
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

    public boolean shouldInterpolatePos() {
        return this.getRenderProperties().isLerpPosition() && !this.blockInterpolation;
    }

    public boolean shouldInterpolateRot() {
        return this.getRenderProperties().isLerpRotation() && !this.blockInterpolation;
    }

    public RenderData.RenderProperties getRenderProperties() {
        return this.getRenderData().getRenderProperties();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.blockInterpolation = !this.isVisible();
        if (this.getWorldItem().hasLight() && !this.hasLight()) {
            this.addLight(this.getWorldItem().getLight());
        }
        if (this.getRenderABB() != null) {
            Vector3d size = this.calcABBSize(this.getWorldItem());
            if (size != null) {
                this.getRenderABB().setAbbForm(this.getRenderPosition(), this.calcABBSize(this.getWorldItem()));
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
        if (this.getWorldItem() instanceof JBulletPhysics) {
            JBulletPhysics jBulletPhysics = (JBulletPhysics) this.getWorldItem();
            return jBulletPhysics.getRigidBodyPos();
        }
        return this.getWorldItem().getPosition();
    }

    protected Vector3d getFixedRotation() {
        if (this.getWorldItem() instanceof JBulletPhysics) {
            JBulletPhysics jBulletPhysics = (JBulletPhysics) this.getWorldItem();
            return jBulletPhysics.getRigidBodyRot();
        }
        return this.getWorldItem().getRotation();
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
