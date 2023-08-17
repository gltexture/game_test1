package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.gui.GUI;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class GuiRender extends SceneRenderBase {
    private final SceneWorld sceneWorld;

    public GuiRender(SceneWorld sceneWorld) {
        super(3, sceneWorld, RenderGroup.GUI);
        this.sceneWorld = sceneWorld;
        this.addUniform("projection_model_matrix");
        this.addUniform("colour");
    }

    public void onRender(double partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GUI.renderGUI(partialTicks);
        GL30.glDisable(GL30.GL_BLEND);
    }

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }
}
