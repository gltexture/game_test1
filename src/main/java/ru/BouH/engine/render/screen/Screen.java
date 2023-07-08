package ru.BouH.engine.render.screen;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.init.controller.Controller;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.proxy.init.EntitiesInit;
import ru.BouH.engine.proxy.init.KeysInit;
import ru.BouH.engine.render.scene.world.RenderWorld;
import ru.BouH.engine.render.screen.window.Window;

import java.nio.IntBuffer;

public class Screen {
    private RenderWorld renderWorld;
    private Controller controller;
    private Window window;
    public static int FPS;

    public void init() {
        Game game = Game.getGame();
        game.getLogManager().log("Starting screen!");
        if (this.tryToBuildScreen()) {
            game.getLogManager().log("Game screen built successful");
            GL.createCapabilities();
            EntitiesInit.init();
            this.renderWorld = new RenderWorld(game.getPhysX().getWorld());
            this.setWindowCallbacks();
            this.controller = new Controller(this.getWindow());
            KeysInit.init(this.getWindow());
        } else {
            game.getLogManager().error("Screen build error!");
        }
        game.getLogManager().log("Stopping game...");
    }

    public void startScreen() {
        this.updateScreen();
        GLFW.glfwDestroyWindow(this.getWindow().getDescriptor());
        GLFW.glfwTerminate();
    }

    private void setWindowCallbacks() {
        Callbacks.glfwFreeCallbacks(this.getWindow().getDescriptor());
        GLFW.glfwSetWindowSizeCallback(this.getWindow().getDescriptor(), (a, b, c) -> GL30.glViewport(0, 0, b, c));
        GLFWErrorCallback glfwErrorCallback = GLFW.glfwSetErrorCallback(null);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    private boolean tryToBuildScreen() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            Game.getGame().getLogManager().error("Error initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL20.GL_TRUE);
        this.window = new Window(new Window.WindowProperties(800, 600, "Test"));
        if (this.getWindow().getDescriptor() == MemoryUtil.NULL) {
            Game.getGame().getLogManager().error("Failed to create the GLFW window");
            return false;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.getWindow().getDescriptor(), width, height);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            if (vidMode != null) {
                int x = (vidMode.width() - width.get(0)) / 2;
                int y = (vidMode.height() - height.get(0)) / 2;
                GLFW.glfwSetWindowPos(this.getWindow().getDescriptor(), x, y);
            } else {
                return false;
            }
        }
        GLFW.glfwMakeContextCurrent(this.getWindow().getDescriptor());
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(this.getWindow().getDescriptor());
        return true;
    }

    public RenderWorld getRenderWorld() {
        return this.renderWorld;
    }

    private void updateScreen() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Game.getGame().getLogManager().debug("...........................................");
        Game.getGame().getLogManager().debug("Begin render section");
        this.getRenderWorld().getGuiRender().onStartRender();
        this.getRenderWorld().getSkyRender().onStartRender();
        this.getRenderWorld().getSceneRender().onStartRender();
        final float tps = 1.0f / 50.0f;
        double lastTime = 0;
        while (!Game.getGame().shouldBeClosed) {
            Game.getGame().shouldBeClosed = GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL30.glEnable(GL30.GL_CULL_FACE);
            GL30.glCullFace(GL30.GL_BACK);
            this.getRenderWorld().getWorld().getLocalPlayer().performController(this.getController());
            this.getRenderWorld().onWorldUpdate();
            double currentTime = GLFW.glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            double progress = deltaTime / tps;
            this.getRenderWorld().getSkyRender().onRender(progress);
            this.getRenderWorld().getSceneRender().onRender(progress);
            this.getRenderWorld().getGuiRender().onRender(progress);
            this.getController().input(this.getWindow());
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
        this.getRenderWorld().getSceneRender().onStopRender();
        this.getRenderWorld().getSkyRender().onStopRender();
        this.getRenderWorld().getGuiRender().onStopRender();
        Game.getGame().getLogManager().debug("Stop render section");
        Game.getGame().getLogManager().debug("...........................................");
    }

    public Controller getController() {
        return this.controller;
    }

    public Window getWindow() {
        return this.window;
    }
}
