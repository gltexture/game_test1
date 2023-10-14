package ru.BouH.engine.render.scene.scene_render;

import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.environment.shadows.CascadeShadowBuilder;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.debug.jbullet.JBDebugDraw;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;
import ru.BouH.engine.render.scene.mesh_forms.VectorForm;
import ru.BouH.engine.render.scene.mesh_forms.wire.AABBWireForm;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.programs.CubeMapSample;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

import java.util.Iterator;

public class WorldRender extends SceneRenderBase {
    private final CubeMapSample cubeEnvironmentTexture;
    public static PhysXObject physXObject;

    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, RenderGroup.WORLD);
        this.cubeEnvironmentTexture = this.getSceneWorld().getEnvironment().getSky().getSkyBox().getCubeMap();
        this.addUniform(UniformConstants.dimensions);
        this.addUniform(UniformConstants.tick);
        this.addUniform(UniformConstants.projection_matrix);
        this.addUniform(UniformConstants.model_view_matrix);
        this.addUniform(UniformConstants.texture_sampler);
        this.addUniform(UniformConstants.normal_map);
        this.addUniform(UniformConstants.cube_map_sampler);
        this.addUniform(UniformConstants.object_rgb);
        this.addUniform(UniformConstants.use_texture);
        this.addUniform(UniformConstants.enable_light);
        this.addUniform(UniformConstants.use_normal_map);
        this.addUniform(UniformConstants.texture_scaling);
        this.addUniform(UniformConstants.quads_c1);
        this.addUniform(UniformConstants.quads_c2);
        this.addUniformBuffer(UniformBufferUtils.UBO_SUN);
        this.addUniformBuffer(UniformBufferUtils.UBO_POINT_LIGHTS);
        this.addUniformBuffer(UniformBufferUtils.UBO_MISC);

        this.addUniform(UniformConstants.model_matrix);
        for (int i = 0; i < CascadeShadowBuilder.SHADOW_CASCADE_MAX; i++) {
            this.addUniform("shadowMap_" + i);
            this.addUniform("CShadows[" + i + "]" + ".projection_view_matrix");
            this.addUniform("CShadows[" + i + "]" + ".split_distance");
        }
    }

    public void onRender(double partialTicks) {
        this.bindProgram();
        UniformBufferUtils.updateLightBuffers(this);
        this.performUniform(UniformConstants.dimensions, Game.getGame().getScreen().getWindow().getWindowDimensions());
        this.getUtils().performProjectionMatrix();
        this.renderDebugSunDirection(this);
        for (PhysXObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
            this.renderHitBox(partialTicks, this, entityItem);
            if (entityItem.isHasRender()) {
                if (entityItem.isHasModel()) {
                    this.setRenderTranslation(entityItem);
                }
                if (entityItem.isVisible()) {
                    this.getUtils().performProperties(entityItem.getRenderData().getRenderProperties());
                    this.getUtils().setCubeMapTexture(2, this.getCubeEnvironmentTexture());
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                }
            }
        }
        this.unBindProgram();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void setRenderTranslation(PhysXObject physXObject) {
        Model3D model3D = physXObject.getModel3D();
        model3D.setScale(physXObject.getScale());
        model3D.setPosition(physXObject.getRenderPosition());
        model3D.setRotation(physXObject.getRenderRotation());
    }

    public CubeMapSample getCubeEnvironmentTexture() {
        return this.cubeEnvironmentTexture;
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        this.getUtils().disableMsaa();
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
        this.getUtils().enableMsaa();
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysXObject physXObject) {
        WorldItem worldItem = physXObject.getWorldItem();
        if (worldItem instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) worldItem;
            RigidBodyObject rigidBodyObject = jBulletEntity.getRigidBodyObject();
            if (jBulletEntity.isValid()) {
                this.getUtils().disableMsaa();
                this.getUtils().disableLight();
                btVector3 min = new btVector3();
                btVector3 max = new btVector3();
                btTransform transform = new btTransform();
                transform.setIdentity();
                rigidBodyObject.getCollisionShape().getAabb(transform, min, max);
                transform.deallocate();
                AABBWireForm form = new AABBWireForm(min, max);
                if (form.hasMesh()) {
                    form.getMeshInfo().getPosition().set(physXObject.getRenderPosition());
                    sceneRenderBase.getUtils().performModelViewMatrix3d(form.getMeshInfo());
                    GL30.glBindVertexArray(form.getMeshInfo().getVao());
                    GL30.glEnableVertexAttribArray(0);
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    this.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(0, 1, 0, 1)));
                    GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
                    GL30.glDisableVertexAttribArray(0);
                    GL30.glBindVertexArray(0);
                    form.clearMesh();
                }
                this.getUtils().enableLight();
                this.getUtils().enableMsaa();
            }
        }
    }
}