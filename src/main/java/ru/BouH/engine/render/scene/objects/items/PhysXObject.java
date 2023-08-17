package ru.BouH.engine.render.scene.objects.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physx.collision.JBulletPhysics;
import ru.BouH.engine.physx.collision.objects.AbstractCollision;
import ru.BouH.engine.physx.collision.objects.ConvexShape;
import ru.BouH.engine.physx.collision.objects.OBB;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.physx.world.object.IWorldObject;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.primitive_forms.CollisionOBBoxForm;
import ru.BouH.engine.render.scene.primitive_forms.CollisionPlaneForm;
import ru.BouH.engine.render.scene.primitive_forms.IForm;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class PhysXObject implements IRenderObject, IWorldObject, IDynamic {
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderData renderData;
    private IForm collisionForm;
    protected Model3D model3D;
    private boolean isDead;

    public PhysXObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderData renderData) {
        this.worldItem = worldItem;
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
    }

    protected void setModel() {
        this.model3D = null;
    }

    private void genCollisionBox(JBulletPhysics JBulletPhysics) {
        if (JBulletPhysics.hasCollision()) {
            this.collisionForm = this.constructForm(JBulletPhysics.getCollision());
        }
    }

    private IForm constructForm(AbstractCollision iCollision) {
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
        if (worldItem instanceof JBulletPhysics) {
            this.genCollisionBox((JBulletPhysics) worldItem);
        }
        this.setModel();
        if (this.isHasRender()) {
            this.renderFabric().onStartRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PostRender");
        if (this.hasCollisionForm()) {
            this.getCollisionForm().getMeshInfo().clean();
        }
        if (this.isHasRender()) {
            this.renderFabric().onStopRender(this);
        }
    }

    public IForm getCollisionForm() {
        return this.collisionForm;
    }

    public boolean hasCollisionForm() {
        return this.getCollisionForm() != null;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getWorldItem() instanceof JBulletPhysics && !this.hasCollisionForm()) {
            this.genCollisionBox((JBulletPhysics) this.getWorldItem());
        }
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public Vector3d getRenderPosition() {
        return this.getWorldItem().getPosition();
    }

    public Vector3d getRenderRotation() {
        return this.getWorldItem().getRotation();
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
