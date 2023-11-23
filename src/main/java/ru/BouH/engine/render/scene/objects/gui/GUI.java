package ru.BouH.engine.render.scene.objects.gui;

import org.joml.Vector2d;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.Binding;
import ru.BouH.engine.game.controller.input.Keyboard;
import ru.BouH.engine.game.g_static.render.RenderResources;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.objects.gui.hud.GuiPicture;
import ru.BouH.engine.render.scene.objects.gui.hud.GuiText;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.screen.Screen;

public class GUI {
    private static final SceneRenderBase sceneRenderBase = Game.getGame().getScreen().getScene().getGuiRender();

    public static void renderGUI(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        final EntityPlayerSP entityPlayerSP = Game.getGame().getPlayerSP();
        GUI.renderText(partialTicks, 0, 0, "FPS: " + Screen.FPS + " | TPS: " + Screen.PHYS2_TPS, 0xffffff);
        GUI.renderText(partialTicks, 0, 20, "entities: " + Game.getGame().getPhysicsWorld().countItems(), 0xffffff);
        GUI.renderText(partialTicks, 0, 40, String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z), 0xffffff);
        int i1 = 60;
        if (!Keyboard.isPressedKey(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            GUI.renderText(partialTicks, 0, i1, "Управление LCTRL", 0xffffff);
        } else {
            for (Binding keyBinding : Binding.getBindingList()) {
                GUI.renderText(partialTicks, 0, i1, keyBinding.toString(), 0xffffff);
                i1 += 20;
            }
        }
        GUI.renderText(partialTicks, 0, i1 + 20, "speed: " + String.format("%.2f", entityPlayerSP.getObjectSpeed()), 0xffffff);
        //Vector2d vector2d = GUI.getScaledPictureDimensions(RenderResources.pngGuiPic1, 0.1f);
        //GUI.renderPicture(partialTicks, (int) (width - vector2d.x - 2), 2, (int) vector2d.x, (int) vector2d.y, RenderResources.pngGuiPic1);
    }

    private static Vector2d getScaledPictureDimensions(PNGTexture PNGTexture, float scale) {
        if (PNGTexture == null || !PNGTexture.isValid()) {
            return new Vector2d(0.0f);
        }
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        Vector2d WH = new Vector2d(width / Screen.defaultW, height / Screen.defaultH).mul(scale);
        double picScale = Math.min(WH.x, WH.y);
        return new Vector2d(PNGTexture.getWidth() * picScale, PNGTexture.getHeight() * picScale);
    }

    private static void performMatrix(Model2D model2D) {
        sceneRenderBase.performUniform("projection_model_matrix", RenderManager.instance.getOrthographicModelMatrix(model2D));
    }

    private static void renderPicture(double partialTicks, int x, int y, int w, int h, PNGTexture PNGTexture) {
        if (PNGTexture == null) {
            return;
        }
        GuiPicture guiPicture = new GuiPicture(PNGTexture, x, y, w, h);
        Model2D model2D = guiPicture.getModel2DInfo();
        if (model2D != null) {
            GUI.performMatrix(guiPicture.getModel2DInfo());
            sceneRenderBase.performUniform("colour", new Vector4d(1.0f, 1.0f, 1.0f, 1.0f));
            guiPicture.renderFabric().onRender(partialTicks, GUI.sceneRenderBase, guiPicture);
            guiPicture.getModel2DInfo().clean();
        } else {
            Game.getGame().getLogManager().warn("Invalid Texture!");
        }
    }

    private static void renderText(double partialTicks, int x, int y, String s, int HEX) {
        GuiText guiText = new GuiText(s, x, y);
        GUI.performMatrix(guiText.getModel2DInfo());
        float[] hex = Color3FA.HEX2RGB(HEX);
        sceneRenderBase.performUniform("colour", new Vector4d(hex[0], hex[1], hex[2], 1.0f));
        guiText.renderFabric().onRender(partialTicks, GUI.sceneRenderBase, guiText);
        guiText.getModel2DInfo().clean();
    }
}
