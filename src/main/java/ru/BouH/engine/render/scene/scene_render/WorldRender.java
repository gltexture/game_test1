package ru.BouH.engine.render.scene.scene_render;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.shadows.CascadeShadowBuilder;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.primitive_forms.IForm;
import ru.BouH.engine.render.scene.primitive_forms.VectorForm;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.util.List;

public class WorldRender extends SceneRenderBase {
    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, RenderGroup.WORLD);
        this.addUniform("dimensions");
        this.addUniform("tick");
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("texture_sampler");
        this.addUniform("object_rgb");
        this.addUniform("use_texture");
        this.addUniform("enable_light");
        this.addUniform("quads_c1");
        this.addUniform("quads_c2");
        this.addUniform("quads_scaling");
        this.addUniformBuffer(UniformBufferUtils.UBO_SUN);
        this.addUniformBuffer(UniformBufferUtils.UBO_POINT_LIGHTS);
        this.addUniformBuffer(UniformBufferUtils.UBO_MISC);

        this.addUniform("model_matrix");
        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            this.addUniform("shadowMap_" + i);
            this.addUniform("CShadows[" + i + "]" + ".projection_view_matrix");
            this.addUniform("CShadows[" + i + "]" + ".split_distance");
        }
    }

    public void onRender(double partialTicks) {
        this.bindProgram();
        UniformBufferUtils.updateLightBuffers(this);
        this.performUniform("dimensions", Game.getGame().getScreen().getWindow().getWindowDimensions());
        this.getUtils().performProjectionMatrix();
        this.renderDebugSunDirection(this);
        for (PhysXObject entityItem : this.getSceneWorld().getEntityList()) {
            this.renderHitBox(partialTicks, this, entityItem);
            if (entityItem.isHasRender()) {
                if (entityItem.isHasModel()) {
                    this.performLightModelProjection(1, entityItem.getModel3D());
                }
                this.getUtils().performProperties(entityItem.getRenderData().getRenderProperties());
                entityItem.renderFabric().onRender(partialTicks, this, entityItem);
            }
        }
        this.unBindProgram();
    }

    private void performLightModelProjection(int start, Model3D model3D) {
        this.getUtils().performModelMatrix3d(model3D);
        List<CascadeShadowBuilder> cascadeShadowBuilders = this.getShadowDispatcher().getCascadeShadowBuilders();
        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            CascadeShadowBuilder cascadeShadowBuilder = cascadeShadowBuilders.get(i);
            this.performUniform("shadowMap_" + i, start + i);
            this.performUniform("CShadows[" + i + "]" + ".projection_view_matrix", cascadeShadowBuilder.getProjectionViewMatrix());
            this.performUniform("CShadows[" + i + "]" + ".split_distance", (float) cascadeShadowBuilder.getSplitDistance());
        }
        this.getShadowDispatcher().getDepthMap().bindTextures(GL30.GL_TEXTURE1);
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        GL30.glDisable(GL30.GL_MULTISAMPLE);
        this.getUtils().disableLight();
        VectorForm vectorForm = sceneRenderBase.getSceneWorld().getEnvironment().sunDebugVector;
        sceneRenderBase.getUtils().performModelViewMatrix3d(this.getSceneWorld(), vectorForm.getMeshInfo());
        GL30.glBindVertexArray(vectorForm.getMeshInfo().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(1, 0, 0, 1)));
        GL30.glDrawElements(GL30.GL_LINES, vectorForm.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        this.getUtils().enableLight();
        GL30.glEnable(GL30.GL_MULTISAMPLE);
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysXObject physXObject) {
        GL30.glDisable(GL30.GL_MULTISAMPLE);
        this.getUtils().disableLight();
        IForm form = physXObject.getCollisionForm();
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
            this.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(0, 1, 0, 1)));
            GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        }
        this.getUtils().enableLight();
        GL30.glEnable(GL30.GL_MULTISAMPLE);
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}