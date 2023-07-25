package ru.BouH.engine.proxy.init;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.game.init.controller.KeyBinding;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.screen.window.Window;

public class KeysInit {
    public static KeyBinding keyLeft;
    public static KeyBinding keyRight;
    public static KeyBinding keyForward;
    public static KeyBinding keyBackward;
    public static KeyBinding keyUp;
    public static KeyBinding keyDown;
    public static KeyBinding keyPlaceLamp;
    public static KeyBinding keyPlaceBlock;
    public static KeyBinding keyFly;
    public static KeyBinding keyPlaceBlock2;

    public static void init(Window window) {
        Proxy proxy = Game.getGame().getProxy();
        KeysInit.keyLeft = new KeyBinding(GLFW.GLFW_KEY_A);
        KeysInit.keyRight = new KeyBinding(GLFW.GLFW_KEY_D);
        KeysInit.keyForward = new KeyBinding(GLFW.GLFW_KEY_W);
        KeysInit.keyBackward = new KeyBinding(GLFW.GLFW_KEY_S);
        KeysInit.keyUp = new KeyBinding(GLFW.GLFW_KEY_SPACE);
        KeysInit.keyDown = new KeyBinding(GLFW.GLFW_KEY_LEFT_SHIFT);
        KeysInit.keyPlaceLamp = new KeyBinding(GLFW.GLFW_KEY_L);
        KeysInit.keyPlaceBlock = new KeyBinding(GLFW.GLFW_KEY_B);
        KeysInit.keyPlaceBlock2 = new KeyBinding(GLFW.GLFW_KEY_Z);
        KeysInit.keyFly = new KeyBinding(GLFW.GLFW_KEY_F);

        proxy.addKeyBinding(KeysInit.keyLeft);
        proxy.addKeyBinding(KeysInit.keyRight);
        proxy.addKeyBinding(KeysInit.keyForward);
        proxy.addKeyBinding(KeysInit.keyBackward);
        proxy.addKeyBinding(KeysInit.keyUp);
        proxy.addKeyBinding(KeysInit.keyDown);
        proxy.addKeyBinding(KeysInit.keyPlaceLamp);
        proxy.addKeyBinding(KeysInit.keyPlaceBlock);
        proxy.addKeyBinding(KeysInit.keyFly);
        proxy.addKeyBinding(KeysInit.keyPlaceBlock2);
    }
}
