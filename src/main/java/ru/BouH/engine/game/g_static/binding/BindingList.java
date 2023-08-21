package ru.BouH.engine.game.g_static.binding;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.Binding;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.controller.components.FunctionalKey;
import ru.BouH.engine.game.controller.components.IKeyAction;
import ru.BouH.engine.game.controller.components.Key;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class BindingList {
    public static BindingList instance = new BindingList();
    public Key keyA = new Key(GLFW.GLFW_KEY_A);
    public Key keyD = new Key(GLFW.GLFW_KEY_D);
    public Key keyW = new Key(GLFW.GLFW_KEY_W);
    public Key keyS = new Key(GLFW.GLFW_KEY_S);
    public Key keyUp = new Key(GLFW.GLFW_KEY_SPACE);
    public Key keyDown = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
    public Key keyBlock1 = new Key(GLFW.GLFW_KEY_F);
    public Key keyEsc = new FunctionalKey(e -> {
        if (e == IKeyAction.KeyAction.CLICK) {
            Game.getGame().destroyGame();
        }
    }, GLFW.GLFW_KEY_ESCAPE);

    public Key keyR = new FunctionalKey(e -> {
        if (e == IKeyAction.KeyAction.CLICK) {
            IController controller = Game.getGame().getScreen().getControllerDispatcher().getCurrentController();
            ICamera camera = Game.getGame().getScreen().getCamera();
            if (controller != null) {
                if (camera != null) {
                    if (camera instanceof AttachedCamera) {
                        AttachedCamera attachedCamera = (AttachedCamera) camera;
                        Game.getGame().getScreen().getScene().enableFreeCamera(controller, attachedCamera.getCamPosition(), attachedCamera.getCamRotation());
                        Game.getGame().getScreen().getControllerDispatcher().detachController(controller);
                    } else if (camera instanceof FreeCamera) {
                        Game.getGame().getScreen().getScene().enableAttachedCamera(Game.getGame().getPlayerSP());
                        Game.getGame().getScreen().getControllerDispatcher().attachControllerTo(controller, Game.getGame().getPlayerSP());
                    }
                } else {
                    Game.getGame().getScreen().getScene().enableAttachedCamera(Game.getGame().getPlayerSP());
                }
            }
        }
    }, GLFW.GLFW_KEY_R);

    public Key keyT = new FunctionalKey(e -> {
        if (e == IKeyAction.KeyAction.CLICK) {
            Game.getGame().getScreen().isInFocus = !Game.getGame().getScreen().isInFocus;
        }
    }, GLFW.GLFW_KEY_T);

    public BindingList() {
        Binding.createBinding(this.keyA, "Шаг влево");
        Binding.createBinding(this.keyD, "Шаг вправо");
        Binding.createBinding(this.keyW, "Шаг вперед");
        Binding.createBinding(this.keyS, "Шаг назад");
        Binding.createBinding(this.keyEsc, "Закрыть экран");
        Binding.createBinding(this.keyT, "Фокус");
        Binding.createBinding(this.keyUp, "Лететь вверх");
        Binding.createBinding(this.keyDown, "Лететь вниз");
        Binding.createBinding(this.keyR, "Режим камеры");
        Binding.createBinding(this.keyBlock1, "Славянский выстрел блоком");
    }
}
