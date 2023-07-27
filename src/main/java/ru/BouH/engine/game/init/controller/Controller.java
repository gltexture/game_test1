package ru.BouH.engine.game.init.controller;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.proxy.init.KeysInit;
import ru.BouH.engine.render.screen.window.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Controller {
    private final Vector2d prevPos;
    private final Vector2d currPos;
    private final Vector2d displayVec;
    private final Vector3d camInput;
    private final List<KeyBinding> keyBindings;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;

    public Controller(Window window) {
        this.prevPos = new Vector2d(-1.0d, -1.0d);
        this.currPos = new Vector2d(0.0d, 0.0d);
        this.camInput = new Vector3d(0.0f, 0.0f, 0.0f);
        this.displayVec = new Vector2d();
        this.keyBindings = new ArrayList<>();
        this.mouseInit(window.getDescriptor());
        Game.getGame().getLogManager().log("Controller init");
    }

    private void mouseInit(long window) {
        GLFW.glfwSetCursorPosCallback(window, (getWindow, xPos, yPos) -> {
            this.currPos.x = xPos;
            this.currPos.y = yPos;
        });
        GLFW.glfwSetCursorEnterCallback(window, (getWindow, entered) -> this.inWindow = entered);
        GLFW.glfwSetMouseButtonCallback(window, (getWindow, button, action, mode) -> {
            this.leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            this.rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void addKeyBinding(KeyBinding keybinding) {
        this.keyBindings.add(keybinding);
    }

    public void input(Window window) {
        for (KeyBinding keyBinding1 : this.keyBindings) {
            keyBinding1.updateListeners(window);
        }
        this.getDisplayVec().x = 0;
        this.getDisplayVec().y = 0;
        if (this.prevPos.x > 0 && this.prevPos.y > 0 && this.inWindow) {
            double deltaX = this.currPos.x - this.prevPos.x;
            double deltaY = this.currPos.y - this.prevPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                this.getDisplayVec().y = (float) deltaX;
            }
            if (rotateY) {
                this.getDisplayVec().x = (float) deltaY;
            }
        }
        this.prevPos.x = this.currPos.x;
        this.prevPos.y = this.currPos.y;
        this.camInput.set(0.0f, 0.0f, 0.0f);
        if (KeysInit.keyForward.isPressed()) {
            this.camInput.z = -1.0d;
        }
        if (KeysInit.keyBackward.isPressed()) {
            this.camInput.z = 1.0d;
        }
        if (KeysInit.keyLeft.isPressed()) {
            this.camInput.x = -1.0d;
        }
        if (KeysInit.keyRight.isPressed()) {
            this.camInput.x = 1.0d;
        }
        if (KeysInit.keyDown.isPressed()) {
            this.camInput.y = -1.0d;
        }
        if (KeysInit.keyUp.isPressed()) {
            this.camInput.y = 1.0d;
        }
    }

    public List<KeyBinding> getKeyBindings() {
        return this.keyBindings;
    }

    public Vector3d getCamInput() {
        return this.camInput;
    }

    public Vector2d getDisplayVec() {
        return this.displayVec;
    }

    public boolean isLeftButtonPressed() {
        return this.leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return this.rightButtonPressed;
    }
}
