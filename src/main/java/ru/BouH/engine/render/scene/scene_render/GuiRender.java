package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.gui.GUI;

public class GuiRender extends SceneRenderBase {
    public GuiRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(3, sceneRenderConveyor, RenderGroup.GUI);
        this.addUniform("projection_model_matrix");
        this.addUniform("colour");
    }

    public void onRender(double partialTicks) {
        this.bindProgram();
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GUI.renderGUI(partialTicks);
        GL30.glDisable(GL30.GL_BLEND);
        this.unBindProgram();
    }
}
