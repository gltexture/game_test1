package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;

public class RenderGui implements RenderFabric {

    public RenderGui() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        AbstractGui abstractGui = (AbstractGui) renderItem;
        abstractGui.performGuiTexture();
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        GL30.glBindVertexArray(abstractGui.getModel2DInfo().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glDrawElements(GL30.GL_TRIANGLES, abstractGui.getModel2DInfo().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
