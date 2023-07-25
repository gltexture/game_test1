package ru.BouH.engine.render.scene.renderers.main_render;

import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.renderers.items.gui.AbstractGui;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.text.GuiTextItem;
import ru.BouH.engine.render.scene.renderers.main_render.base.RenderGroup;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class GuiRender extends SceneRenderBase {
    private final List<AbstractGui> guiList = new ArrayList<>();
    private final SceneWorld sceneWorld;
    private final GuiTextItem guiTextItem = this.createText("", 0, 0, 0);
    private final GuiTextItem guiTextItem2 = this.createText("", 0, 0, 0);
    private final GuiTextItem guiTextItem3 = this.createText("", 0, 0, 0);

    public GuiRender(SceneWorld sceneWorld) {
        super(3, sceneWorld, RenderGroup.GUI);
        this.sceneWorld = sceneWorld;
        this.addUniform("projection_model_matrix");
        this.addUniform("colour");
    }

    public GuiTextItem createText(String text, int x, int y, int z) {
        GuiTextItem guiTextItem = new GuiTextItem(text, z);
        guiTextItem.getModel2DInfo().setPosition(x, y);
        this.getGuiList().add(guiTextItem);
        Game.getGame().getLogManager().log("Added new gui(text) element");
        return guiTextItem;
    }

    public void onRender(double partialTicks) {
        EntityPlayerSP entityPlayerSP = this.getRenderWorld().getWorld().getLocalPlayer();
        this.guiTextItem.setText((int) entityPlayerSP.getPosition().x + " " + (int) entityPlayerSP.getPosition().y + " " + (int) entityPlayerSP.getPosition().z);
        this.guiTextItem2.setText("fps: " + Screen.FPS);
        this.guiTextItem2.getModel2DInfo().setPosition(0, 20);
        this.guiTextItem3.setText("entities: " + this.getRenderWorld().getWorld().getEntitySet().size());
        this.guiTextItem3.getModel2DInfo().setPosition(0, 40);
        Matrix4d matrix4d1 = this.getRenderWorld().getRenderManager().getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), 0);
        for (AbstractGui abstractGui : this.getGuiList()) {
            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            Matrix4d matrix4d = this.getRenderWorld().getRenderManager().getTransform().getOrthoModelMatrix(abstractGui.getModel2DInfo(), matrix4d1);
            this.performUniform("projection_model_matrix", matrix4d);
            this.performUniform("colour", new Vector4d(1, 1, 1, 1));
            abstractGui.renderFabric().onRender(partialTicks, this, abstractGui);
            GL30.glDisable(GL30.GL_BLEND);
        }
    }

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    public List<AbstractGui> getGuiList() {
        return this.guiList;
    }
}
