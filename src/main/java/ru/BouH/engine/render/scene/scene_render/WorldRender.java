package ru.BouH.engine.render.scene.scene_render;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.mesh_forms.VectorForm;
import ru.BouH.engine.render.scene.mesh_forms.wire.AABBWireForm;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

import java.util.ArrayList;
import java.util.List;

public class WorldRender extends SceneRenderBase {
    private final CubeMapProgram cubeEnvironmentTexture;
    private final ShaderManager debugShaders;

    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, RenderGroup.WORLD);
        this.cubeEnvironmentTexture = this.getSceneWorld().getEnvironment().getSky().getSkyBox().getCubeMap();
        this.debugShaders = ResourceManager.shaderAssets.world;
    }

    public void onRender(double partialTicks) {
        this.debugShaders.bind();
        this.debugShaders.getUtils().disableLightAndMsaa();
        this.renderDebugSunDirection(this);
        this.renderTriggers(partialTicks, this);
        this.debugShaders.getUtils().enableLightAndMsaa();
        this.debugShaders.unBind();

        for (PhysicsObject entityItem : this.getSceneWorld().getFilteredEntityList()) {

            this.debugShaders.bind();
            this.debugShaders.getUtils().disableLightAndMsaa();
            this.renderHitBox(partialTicks, this, entityItem);
            this.debugShaders.getUtils().enableLightAndMsaa();
            this.debugShaders.unBind();

            if (entityItem.isHasRender()) {
                entityItem.getShaderManager().bind();
                entityItem.getShaderManager().getUtils().performProjectionMatrix();
                entityItem.getShaderManager().performUniform(UniformConstants.dimensions, Game.getGame().getScreen().getWindow().getWindowDimensions());
                if (entityItem.isHasModel()) {
                    this.setRenderTranslation(entityItem);
                }
                if (entityItem.isVisible()) {
                    entityItem.getShaderManager().getUtils().performProperties(entityItem.getRenderData().getRenderProperties());
                    entityItem.getShaderManager().getUtils().setCubeMapTexture(2, this.getCubeEnvironmentTexture());
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                }
                entityItem.getShaderManager().unBind();
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void setRenderTranslation(PhysicsObject physicsObject) {
        Model3D model3D = physicsObject.getModel3D();
        model3D.setScale(physicsObject.getScale());
        model3D.setPosition(physicsObject.getRenderPosition());
        model3D.setRotation(physicsObject.getRenderRotation());
    }

    public CubeMapProgram getCubeEnvironmentTexture() {
        return this.cubeEnvironmentTexture;
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        VectorForm vectorForm = sceneRenderBase.getSceneWorld().getEnvironment().sunDebugVector;
        this.debugShaders.getUtils().performModelViewMatrix3d(vectorForm.getMeshInfo());
        GL30.glBindVertexArray(vectorForm.getMeshInfo().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        this.debugShaders.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(1, 0, 0, 1)));
        GL30.glDrawElements(GL30.GL_LINES, vectorForm.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void renderTriggers(double partialTicks, SceneRenderBase sceneRenderBase) {
        List<ITriggerZone> triggerZones = new ArrayList<>(this.getSceneWorld().getWorld().getTriggerZones());
        for (ITriggerZone triggerZone : triggerZones) {
            AABBWireForm form = new AABBWireForm(triggerZone.getZone());
            if (form.hasMesh()) {
                form.getMeshInfo().getPosition().set(triggerZone.getZone().getLocation());
                this.debugShaders.getUtils().performModelViewMatrix3d(form.getMeshInfo());
                GL30.glBindVertexArray(form.getMeshInfo().getVao());
                GL30.glEnableVertexAttribArray(0);
                GL30.glEnable(GL30.GL_DEPTH_TEST);
                this.debugShaders.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(1, 1, 0, 1)));
                GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
                GL30.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
                form.clearMesh();
            }
        }
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysicsObject physicsObject) {
        WorldItem worldItem = physicsObject.getWorldItem();
        if (worldItem instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) worldItem;
            RigidBodyObject rigidBodyObject = jBulletEntity.getRigidBodyObject();
            if (jBulletEntity.isValid()) {
                AABBWireForm form = this.constructForm(rigidBodyObject);
                if (form.hasMesh()) {
                    form.getMeshInfo().getPosition().set(physicsObject.getRenderPosition());
                    this.debugShaders.getUtils().performModelViewMatrix3d(form.getMeshInfo());
                    GL30.glBindVertexArray(form.getMeshInfo().getVao());
                    GL30.glEnableVertexAttribArray(0);
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    this.debugShaders.getUtils().setTexture(WorldItemTexture.createItemTexture(new Color3FA(0, 1, 0, 1)));
                    GL30.glDrawElements(GL30.GL_LINES, form.getMeshInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
                    GL30.glDisableVertexAttribArray(0);
                    GL30.glBindVertexArray(0);
                    form.clearMesh();
                }
            }
        }
    }

    private AABBWireForm constructForm(btCollisionObject btCollisionObject) {
        btVector3 min = new btVector3();
        btVector3 max = new btVector3();
        btTransform transform = new btTransform();
        transform.setIdentity();
        btCollisionObject.getCollisionShape().getAabb(transform, min, max);
        transform.deallocate();
        AABBWireForm form = new AABBWireForm(min, max);
        min.deallocate();
        max.deallocate();
        return form;
    }
}