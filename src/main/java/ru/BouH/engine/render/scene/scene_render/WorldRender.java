package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.environment.shadows.CascadeShadowBuilder;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;
import ru.BouH.engine.render.scene.mesh_forms.VectorForm;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.programs.CubeMapSample;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;

import java.util.List;

public class WorldRender extends SceneRenderBase {
    private final CubeMapSample cubeEnvironmentTexture;

    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, RenderGroup.WORLD);
        this.cubeEnvironmentTexture = this.getSceneWorld().getEnvironment().getSky().getSkyBox().getCubeMap();
        this.addUniform("dimensions");
        this.addUniform("tick");
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("texture_sampler");
        this.addUniform("normal_map");
        this.addUniform("cube_map_sampler");
        this.addUniform("object_rgb");
        this.addUniform("use_texture");
        this.addUniform("enable_light");
        this.addUniform("use_normal_map");
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
        this.getUtils().disableMsaa();
        this.renderDebugSunDirection(this);
        this.getUtils().enableMsaa();
        for (PhysXObject entityItem : this.getSceneWorld().getEntityList()) {
            entityItem.updateRenderPos(partialTicks);
            this.getUtils().disableMsaa();
            this.renderHitBox(partialTicks, this, entityItem);
            this.getUtils().enableMsaa();
            if (entityItem.isVisible()) {
                if (entityItem.isHasRender()) {
                    if (entityItem.isHasModel()) {
                        this.performLightModelProjection(2, entityItem.getModel3D());
                    }
                    this.getUtils().setCubeMapTexture(2, this.getCubeEnvironmentTexture());
                    this.getUtils().setNormalMap(1, entityItem.getRenderData().getItemTexture());
                    this.getUtils().performProperties(entityItem.getRenderData().getRenderProperties());
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                }
            }
        }
        this.unBindProgram();
    }

    public CubeMapSample getCubeEnvironmentTexture() {
        return this.cubeEnvironmentTexture;
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
        this.getUtils().disableLight();
        VectorForm vectorForm = sceneRenderBase.getSceneWorld().getEnvironment().sunDebugVector;
        sceneRenderBase.getUtils().performModelViewMatrix3d(vectorForm.getMeshInfo());
        GL30.glBindVertexArray(vectorForm.getMeshInfo().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(1, 0, 0, 1)));
        GL30.glDrawElements(GL30.GL_LINES, vectorForm.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        this.getUtils().enableLight();
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysXObject physXObject) {
        AbstractMeshForm form = physXObject.genCollisionMesh();
        if (form != null && form.hasMesh()) {
            this.getUtils().disableLight();
            form.getMeshInfo().getPosition().set(physXObject.getRenderPosition());
            form.getMeshInfo().getRotation().set(physXObject.getRenderRotation());
            sceneRenderBase.getUtils().performModelViewMatrix3d(form.getMeshInfo());
            GL30.glBindVertexArray(form.getMeshInfo().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(0, 1, 0, 1)));
            GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            form.clearMesh();
            this.getUtils().enableLight();
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}