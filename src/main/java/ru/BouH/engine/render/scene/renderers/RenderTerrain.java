package ru.BouH.engine.render.scene.renderers;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.scene.renderers.items.terrain.TerrainItem;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;

public class RenderTerrain implements IRenderFabric {

    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderItem iRenderItem) {
        TerrainItem terrainItem = (TerrainItem) iRenderItem;
        sceneRenderBase.performUniform("model_view_matrix", sceneRenderBase.getSceneWorld().getRenderManager().getTransform().getModelViewMatrix(terrainItem.getMesh(), sceneRenderBase.getSceneWorld().getRenderManager().getTransform().getViewMatrix(sceneRenderBase.getSceneWorld().getCamera())));
        GL30.glBindVertexArray(terrainItem.getMesh().getModel3D().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        sceneRenderBase.performUniform("use_texture", EntityModel.EntityTexture.TextureType.TEXTURE.getI());
        sceneRenderBase.performUniform("texture_sampler", 0);
        terrainItem.getTexture().performTexture();
        GL30.glDrawElements(GL30.GL_TRIANGLES, terrainItem.getMesh().getModel3D().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void onStartRender(IRenderItem iRenderItem) {
    }

    public void onStopRender(IRenderItem iRenderItem) {
        TerrainItem terrainItem = (TerrainItem) iRenderItem;
        terrainItem.getMesh().getModel3D().cleanMesh();
    }
}
