package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.gui.GUI;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

public class GuiRender extends SceneRenderBase {

    public GuiRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(3, sceneRenderConveyor, RenderGroup.GUI);
    }

    public void onRender(double partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GUI.renderGUI(this, partialTicks);
        GL30.glDisable(GL30.GL_BLEND);
    }

    @Override
    public void onStartRender() {

    }

    @Override
    public void onStopRender() {

    }
}
