package ru.BouH.engine.render.scene.render.entities;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.render.IRenderFabric;
import ru.BouH.engine.render.scene.render.IRenderItem;
import ru.BouH.engine.render.scene.render.entities.init.RenderEntity;
import ru.BouH.engine.render.scene.render.models.entity.EntityForm;
import ru.BouH.engine.render.scene.render.scene.SceneRender;
import ru.BouH.engine.render.scene.world.RenderWorld;

public class EntityRenderer implements IRenderFabric {
    private final RenderWorld renderWorld;

    public EntityRenderer(RenderWorld renderWorld) {
        this.renderWorld = renderWorld;
    }

    @Override
    public void onRender(IRenderItem iRenderItem) {
        RenderEntity renderEntity = (RenderEntity) iRenderItem;
        EntityForm entityForm = renderEntity.getEntityModel().getPropForm();
        SceneRender sceneRender = this.renderWorld.getSceneRender();
        if (entityForm.getColours() == null) {
            sceneRender.getUniformProgram().setUniform("use_texture", entityForm.getTexture() == null ? -1 : 1);
        } else {
            sceneRender.getUniformProgram().setUniform("colour", entityForm.getColours());
            sceneRender.getUniformProgram().setUniform("use_texture", 0);
        }
        sceneRender.getUniformProgram().setUniform("specular_power", 1.0f);
        sceneRender.setMaterialUniform("material", entityForm.getMaterial());
        GL30.glBindVertexArray(renderEntity.getEntityModel().getMeshModel().getMesh().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        if (entityForm.getTexture() != null) {
            sceneRender.getUniformProgram().setUniform("texture_sampler", 0);
            entityForm.getTexture().performTexture();
        }
        GL30.glDrawElements(GL30.GL_TRIANGLES, entityForm.getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void onStartRender(IRenderItem iRenderItem) {
    }

    @Override
    public void onStopRender(IRenderItem iRenderItem) {
        RenderEntity renderEntity = (RenderEntity) iRenderItem;
        Game.getGame().getLogManager().debug("[" + renderEntity.getEntity().getItemName() + " - <id/" + renderEntity.getEntity().getItemId() + ">]" + " - PostRender");
        renderEntity.getEntityModel().getMeshModel().getMesh().cleanMesh();
    }
}
