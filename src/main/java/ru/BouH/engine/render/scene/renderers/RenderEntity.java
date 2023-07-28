package ru.BouH.engine.render.scene.renderers;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;
import ru.BouH.engine.render.scene.renderers.items.entity.EntityItem;
import ru.BouH.engine.render.scene.renderers.items.models.box.CollisionBoxForm;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;

public class RenderEntity implements IRenderFabric {
    public RenderEntity() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderItem iRenderItem) {
        EntityItem entityItem = (EntityItem) iRenderItem;
        PhysEntity physEntity = entityItem.getEntity();
        entityItem.getEntityModel().getMeshModel().setScale(physEntity.getScale());
        entityItem.getEntityModel().getMeshModel().getPosition().lerp(physEntity.getPosition(), partialTicks);
        entityItem.getEntityModel().getMeshModel().setRotation(physEntity.getRotation().x, physEntity.getRotation().y, physEntity.getRotation().z);
        sceneRenderBase.performUniform("model_view_matrix", RenderManager.instance.getTransform().getModelViewMatrix(entityItem.getEntityModel().getMeshModel(), RenderManager.instance.getTransform().getViewMatrix(sceneRenderBase.getSceneWorld().getCamera())));
        EntityModel.EntityForm entityForm = entityItem.getEntityModel().getPropForm();
        GL30.glBindVertexArray(entityItem.getEntityModel().getMeshModel().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        EntityModel.EntityTexture.TextureType textureType = entityForm.getEntityTexture().getTextureType();
        int i1 = entityForm.getEntityTexture().getTextureType().getI();
        sceneRenderBase.performUniform("use_texture", i1);
        switch (textureType) {
            case RGB: {
                sceneRenderBase.performUniform("colors", new Vector3d(1, 0, 1));
                break;
            }
            case TEXTURE: {
                sceneRenderBase.performUniform("texture_sampler", 0);
                entityForm.getEntityTexture().getTexture().performTexture();
                break;
            }
            default: {
                break;
            }
        }
        GL30.glDrawElements(GL30.GL_TRIANGLES, entityForm.getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void onStartRender(IRenderItem iRenderItem) {
        EntityItem entityItem = (EntityItem) iRenderItem;
        if (entityItem.isHasRender()) {
            entityItem.getEntityModel().getMeshModel().setPosition(entityItem.getEntity().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderItem iRenderItem) {
        EntityItem entityItem = (EntityItem) iRenderItem;
        if (entityItem.isHasRender()) {
            Game.getGame().getLogManager().debug("[" + entityItem.getEntity().getItemName() + " - <id/" + entityItem.getEntity().getItemId() + ">]" + " - PostRender");
            entityItem.getEntityModel().getMeshModel().getModel3D().cleanMesh();
        }
    }
}
