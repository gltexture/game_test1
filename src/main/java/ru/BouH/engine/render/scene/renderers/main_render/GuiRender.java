package ru.BouH.engine.render.scene.renderers.main_render;

import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.game.init.controller.KeyBinding;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.renderers.items.gui.AbstractGui;
import ru.BouH.engine.render.scene.renderers.items.gui.GUI;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.text.GuiTextItem;
import ru.BouH.engine.render.scene.renderers.main_render.base.RenderGroup;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

import java.util.ArrayList;
import java.util.List;

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
