package ru.BouH.engine.game.init.controller;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.screen.window.Window;

public class KeyBinding {
    private final int keyCode;
    private boolean isPressed;
    private boolean wasClicked;
    private boolean isClicked;
    private boolean wasUnPressed;
    private boolean isReleased;
    private final String description;

    public KeyBinding(String description, int keyCode) {
        this.description = description;
        this.keyCode = keyCode;
        this.isReleased = true;
    }

    public void updateListeners(Window window) {
        this.wasUnPressed = false;
        this.isClicked = false;
        if (GLFW.glfwGetKey(window.getDescriptor(), this.keyCode) == GLFW.GLFW_PRESS) {
            this.isPressed = true;
            this.isReleased = false;
            if (!this.wasClicked) {
                this.isClicked = true;
                this.wasClicked = true;
            }
        }
        if (GLFW.glfwGetKey(window.getDescriptor(), this.keyCode) == GLFW.GLFW_RELEASE && this.isPressed) {
            this.isPressed = false;
            this.isClicked = false;
            this.wasClicked = false;
            this.wasUnPressed = true;
        }
        if (this.wasUnPressed && this.isReleased) {
            this.wasUnPressed = false;
        }
        this.isReleased = !this.isPressed;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getKeyName() {
        String s = GLFW.glfwGetKeyName(this.getKeyCode(), GLFW.glfwGetKeyScancode(this.getKeyCode()));
        if (s == null) {
            return "<null>";
        }
        return s.toUpperCase();
    }

    public static boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(Game.getGame().getScreen().getWindow().getDescriptor(), keyCode) == GLFW.GLFW_PRESS;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isWasUnPressed() {
        return this.wasUnPressed;
    }

    public boolean isClicked() {
        return this.isClicked;
    }

    public boolean isPressed() {
        return this.isPressed;
    }

    public boolean isReleased() {
        return this.isReleased;
    }
}
