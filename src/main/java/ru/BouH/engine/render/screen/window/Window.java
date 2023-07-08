package ru.BouH.engine.render.screen.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Window {
    private final long window;
    private final WindowProperties windowProperties;

    public Window(WindowProperties windowProperties) {
        this.window = GLFW.glfwCreateWindow(windowProperties.getWidth(), windowProperties.getHeight(), windowProperties.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
        this.windowProperties = windowProperties;
    }

    public int getWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, width, null);
            return width.get(0);
        }
    }

    public int getHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, null, height);
            return height.get(0);
        }
    }

    public WindowProperties getWindowProperties() {
        return this.windowProperties;
    }

    public long getDescriptor() {
        return this.window;
    }

    public static class WindowProperties {
        private int width;
        private int height;
        private String title;

        public WindowProperties(int width, int height, String title) {
            this.width = width;
            this.height = height;
            this.title = title;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public String getTitle() {
            return this.title;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
