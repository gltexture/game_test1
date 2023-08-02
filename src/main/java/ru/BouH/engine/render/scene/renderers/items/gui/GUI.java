package ru.BouH.engine.render.scene.renderers.items.gui;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.game.init.controller.KeyBinding;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.components.Model2DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.font.FontCode;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.font.FontTexture;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.pictures.GuiPictureItem;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.text.GuiTextItem;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.screen.Screen;

import java.awt.*;

public class GUI {
    public static final FontTexture standardFont = new FontTexture(new Font("Arial", Font.PLAIN, 24), FontCode.Window);
    private static final SceneRenderBase sceneRenderBase = Game.getGame().getScreen().getScene().getGuiRender();
    private static final Texture pic1 = Texture.createTexture("gui/pictures/meme2.png");

    public static void renderGUI(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        EntityPlayerSP entityPlayerSP = Game.getGame().getPhysX().getWorld().getLocalPlayer();
        GUI.renderText(partialTicks, 0, 0, "FPS: " + Screen.FPS, 0x00ff00);
        GUI.renderText(partialTicks, 0, 20, "entities: " + Game.getGame().getPhysX().getWorld().getEntityList().size(), 0xffffff);
        GUI.renderText(partialTicks, 0, 40, String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z), 0xffffff);
        int i1 = 60;
        if (!KeyBinding.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            GUI.renderText(partialTicks, 0, i1, "Управление LCTRL", 0xffffff);
        } else {
            for (KeyBinding keyBinding : Game.getGame().getScreen().getController().getKeyBindings()) {
                GUI.renderText(partialTicks, 0, i1, keyBinding.getKeyName() + " - " + keyBinding.getDescription(), 0x00ffff);
                i1 += 20;
            }
        }
        Vector2d vector2d = GUI.getScaledPictureDimensions(GUI.pic1, 0.1f);
        GUI.renderPicture(partialTicks, (int) (width - vector2d.x - 2), 2, (int) vector2d.x, (int) vector2d.y, GUI.pic1);
    }

    private static Vector2d getScaledPictureDimensions(Texture texture, float scale) {
        if (texture == null || !texture.isValid()) {
            return new Vector2d(0.0f);
        }
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        Vector2d WH = new Vector2d(width / Screen.defaultW, height / Screen.defaultH).mul(scale);
        double picScale = Math.min(WH.x, WH.y);
        return new Vector2d(texture.getWidth() * picScale, texture.getHeight() * picScale);
    }

    private static void performMatrix(Model2DInfo model2DInfo) {
        Matrix4d orthographicMatrix = RenderManager.instance.getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWidth(), Game.getGame().getScreen().getHeight(), 0);
        Matrix4d matrix4d = RenderManager.instance.getTransform().getOrthoModelMatrix(model2DInfo, orthographicMatrix);
        sceneRenderBase.performUniform("projection_model_matrix", matrix4d);
    }

    private static void renderPicture(double partialTicks, int x, int y, int w, int h, Texture texture) {
        if (texture == null) {
            return;
        }
        GuiPictureItem guiPictureItem = new GuiPictureItem(texture, x, y, w, h);
        Model2DInfo model2DInfo = guiPictureItem.getModel2DInfo();
        if (model2DInfo != null) {
            GUI.performMatrix(guiPictureItem.getModel2DInfo());
            sceneRenderBase.performUniform("colour", new Vector4d(1.0f, 1.0f, 1.0f, 1.0f));
            guiPictureItem.renderFabric().onRender(partialTicks, GUI.sceneRenderBase, guiPictureItem);
            guiPictureItem.getModel2DInfo().getModel2D().cleanMesh();
        } else {
            Game.getGame().getLogManager().warn("Invalid texture! - " + texture.getPath());
        }
    }

    private static void renderText(double partialTicks, int x, int y, String s, int HEX) {
        GuiTextItem guiTextItem = new GuiTextItem(s, x, y);
        GUI.performMatrix(guiTextItem.getModel2DInfo());
        float[] hex = GUI.HEX2RGB(HEX);
        sceneRenderBase.performUniform("colour", new Vector4d(hex[0], hex[1], hex[2], 1.0f));
        guiTextItem.renderFabric().onRender(partialTicks, GUI.sceneRenderBase, guiTextItem);
        guiTextItem.getModel2DInfo().getModel2D().cleanMesh();
    }

    private static float[] HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new float[] {r / 255.0f, g / 255.0f, b / 255.0f};
    }
}
