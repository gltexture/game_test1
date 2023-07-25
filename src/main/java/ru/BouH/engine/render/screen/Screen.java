package ru.BouH.engine.render.screen;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.game.init.controller.Controller;
import ru.BouH.engine.proxy.init.EntitiesInit;
import ru.BouH.engine.proxy.init.KeysInit;
import ru.BouH.engine.render.scene.renderers.main_render.base.Scene;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.window.Window;

import java.nio.IntBuffer;

public class Screen {
    public static int FPS;
    private SceneWorld sceneWorld;
    private Scene scene;
    private Controller controller;
    private Window window;

    public void init() {
        Game game = Game.getGame();
        game.getLogManager().log("Starting screen!");
        if (this.tryToBuildScreen()) {
            game.getLogManager().log("Game screen built successful");
            GL.createCapabilities();
            EntitiesInit.init();
            this.sceneWorld = new SceneWorld(game.getPhysX().getWorld());
            this.scene = new Scene(this.sceneWorld);
            this.scene.init();
            this.setWindowCallbacks();
            this.controller = new Controller(this.getWindow());
            KeysInit.init(this.getWindow());
        } else {
            game.getLogManager().error("Scene build error!");
        }
    }

    public void startScreen() {
        this.updateScreen();
        Game.getGame().getLogManager().log("Stopping game...");
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
        this.window = new Window(new Window.WindowProperties(800, 600, "Build " + Game.build));
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

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    private void updateScreen() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Game.getGame().getLogManager().debug("...........................................");
        Game.getGame().getLogManager().debug("Begin render section");
        this.getScene().preRender();
        int fps = 0;
        double lastFPS = GLFW.glfwGetTime();
        final float tps = 1.0f / 50.0f;
        double lastTime = 0;
        while (!Game.getGame().shouldBeClosed) {
            Game.getGame().shouldBeClosed = GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL30.glEnable(GL30.GL_CULL_FACE);
            GL30.glCullFace(GL30.GL_BACK);
            this.getRenderWorld().getWorld().getLocalPlayer().performController(this.getController());
            this.getRenderWorld().onWorldRenderUpdate();
            double currentTime = GLFW.glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            double progress = deltaTime / tps;
            fps += 1;
            if (currentTime - lastFPS >= 1.0f) {
                Screen.FPS = fps;
                fps = 0;
                lastFPS = currentTime;
            }
            this.getScene().renderScene(progress);
            this.getController().input(this.getWindow());
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
        this.getScene().postRender();
        Game.getGame().getLogManager().debug("Stop render section");
        Game.getGame().getLogManager().debug("...........................................");
    }

    public Scene getScene() {
        return this.scene;
    }

    public Controller getController() {
        return this.controller;
    }

    public Window getWindow() {
        return this.window;
    }
}
