package ru.BouH.engine.render.scene.renderers.main_render;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.renderers.items.entity.EntityItem;
import ru.BouH.engine.render.scene.renderers.items.models.box.CollisionBoxForm;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.scene.renderers.main_render.base.RenderGroup;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.RenderManager;

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
    }

    public void onRender(double partialTicks) {
        this.performUniform("tick", this.getSceneWorld().tick);
        this.performUniform("dimensions", Game.getGame().getScreen().getWindow().getWindowDimensions());
        this.performUniform("projection_matrix", RenderManager.instance.getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR));
        for (EntityItem entityItem : this.sceneWorld.getEntityList()) {
            this.renderHitBox(partialTicks, this, entityItem);
            if (entityItem.isHasRender()) {
                entityItem.getEntityModel().getPropForm().getiRenderFabric().onRender(partialTicks, this, entityItem);
            }
        }
        this.sceneWorld.getTerrainItem().renderFabric().onRender(partialTicks, this, this.sceneWorld.getTerrainItem());
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, EntityItem entityItem) {
        CollisionBoxForm collisionBoxForm = entityItem.getCollisionBoxForm();
        if (entityItem.getEntity() instanceof EntityPlayerSP) {
            collisionBoxForm.getMeshInfo().getPosition().set(this.getSceneWorld().getCamera().getCamPosition());
        } else {
            collisionBoxForm.getMeshInfo().getPosition().lerp(entityItem.getEntity().getPosition(), partialTicks);
        }
        sceneRenderBase.performUniform("model_view_matrix", RenderManager.instance.getTransform().getModelViewMatrix(collisionBoxForm.getMeshInfo(), RenderManager.instance.getTransform().getViewMatrix(sceneRenderBase.getSceneWorld().getCamera())));
        GL30.glBindVertexArray(collisionBoxForm.getMeshInfo().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        sceneRenderBase.performUniform("use_texture", EntityModel.EntityTexture.TextureType.RGB.getI());
        sceneRenderBase.performUniform("colors", new Vector3d(0.0f, 1.0f, 0.0f));
        GL30.glDrawElements(GL30.GL_LINES, collisionBoxForm.getMeshInfo().getModel3D().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
        this.getSceneWorld().getTerrainItem().renderFabric().onStopRender(this.getSceneWorld().getTerrainItem());
        for (EntityItem entityItem : this.sceneWorld.getEntityList()) {
            if (entityItem.isHasRender()) {
                entityItem.renderFabric().onStopRender(entityItem);
            }
            entityItem.getCollisionBoxForm().getMeshInfo().getModel3D().cleanMesh();
        }
    }
}
