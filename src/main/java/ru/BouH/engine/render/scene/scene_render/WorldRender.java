package ru.BouH.engine.render.scene.scene_render;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physx.collision.JBulletPhysics;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.ItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.primitive_forms.IForm;
import ru.BouH.engine.render.scene.primitive_forms.VectorForm;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class WorldRender extends SceneRenderBase {
    private final SceneWorld sceneWorld;

    public WorldRender(SceneWorld sceneWorld) {
        super(1, sceneWorld, RenderGroup.WORLD);
        this.sceneWorld = sceneWorld;
        this.addUniform("dimensions");
        this.addUniform("tick");
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("texture_sampler");
        this.addUniform("colors");
        this.addUniform("use_texture");
        this.addUniform("disable_light");
        this.addUniformBuffer("Lights", 150);
    }

    public void onRender(double partialTicks) {
        UniformBufferUtils.updateLightBuffers(this);
        this.performUniform("tick", this.getSceneWorld().tick);
        this.performUniform("dimensions", Game.getGame().getScreen().getWindow().getWindowDimensions());
        this.getUtils().performProjectionMatrix();
        this.renderDebugSunDirection(this);
        for (PhysXObject entityItem : this.sceneWorld.getFilteredEntityList()) {
            this.renderHitBox(partialTicks, this, entityItem);
            if (entityItem.isHasRender()) {
                entityItem.renderFabric().onRender(partialTicks, this, entityItem);
            }
        }
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        this.getUtils().disableLight();
        VectorForm vectorForm = sceneRenderBase.getSceneWorld().getEnvironment().sunDebugVector;
        sceneRenderBase.getUtils().performModelViewMatrix3d(this.getSceneWorld(), vectorForm.getMeshInfo());
        GL30.glBindVertexArray(vectorForm.getMeshInfo().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.getUtils().setTexture(ItemTexture.createItemTexture(new Color3FA(1, 0, 0, 1)));
        GL30.glDrawElements(GL30.GL_LINES, vectorForm.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        this.getUtils().enableLight();
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysXObject physXObject) {
        this.getUtils().disableLight();
        IForm form = physXObject.getCollisionForm();
        WorldItem worldItem = physXObject.getWorldItem();
        if (worldItem instanceof JBulletPhysics) {
            JBulletPhysics bulletPhysics = (JBulletPhysics) worldItem;
            if (bulletPhysics.hasCollision()) {

            }
        }
        if (form != null && form.hasMesh()) {
            if (Game.getGame().getScreen().getScene().isCameraAttachedToItem(physXObject.getWorldItem()) && physXObject.getWorldItem() instanceof EntityPlayerSP) {
                EntityPlayerSP entityPlayerSP = (EntityPlayerSP) physXObject.getWorldItem();
                Vector3d vector3d = new Vector3d(this.getCamera().getCamPosition().x, this.getCamera().getCamPosition().y, this.getCamera().getCamPosition().z);
                Vector3d v = entityPlayerSP.getRigidBodyRot(entityPlayerSP.getRigidBody());
                Vector3d vector3d2 = new Vector3d(v.x, v.y, v.z);
                form.getMeshInfo().getPosition().set(vector3d);
                form.getMeshInfo().getRotation().set(vector3d2);
            } else {
                form.getMeshInfo().getPosition().set(physXObject.getRenderPosition());
                form.getMeshInfo().getRotation().set(physXObject.getRenderRotation());
            }
            sceneRenderBase.getUtils().performModelViewMatrix3d(this.getSceneWorld(), form.getMeshInfo());
            GL30.glBindVertexArray(form.getMeshInfo().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.getUtils().setTexture(ItemTexture.createItemTexture(new Color3FA(0, 1, 0, 1)));
            GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        }
        this.getUtils().enableLight();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
        this.getSceneWorld().removeAllEntities();
    }
}
