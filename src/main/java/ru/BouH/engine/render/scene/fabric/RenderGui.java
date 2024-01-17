package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.Scene;
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
        Scene.renderModel(abstractGui.getModel2DInfo());
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
