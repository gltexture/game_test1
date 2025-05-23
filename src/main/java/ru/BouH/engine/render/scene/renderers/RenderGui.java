package ru.BouH.engine.render.scene.renderers;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;
import ru.BouH.engine.render.scene.renderers.items.gui.AbstractGui;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;

public class RenderGui implements IRenderFabric {
    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderItem iRenderItem) {
        AbstractGui abstractGui = (AbstractGui) iRenderItem;
        abstractGui.performGuiTexture();
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glBindVertexArray(abstractGui.getModel2DInfo().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glDrawElements(GL30.GL_TRIANGLES, abstractGui.getModel2DInfo().getModel2D().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    @Override
    public void onStartRender(IRenderItem iRenderItem) {

    }

    @Override
    public void onStopRender(IRenderItem iRenderItem) {

    }
}
