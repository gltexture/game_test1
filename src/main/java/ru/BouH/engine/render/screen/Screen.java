package ru.BouH.engine.render.screen;

import org.joml.Vector2d;
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
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.game.g_static.render.ItemRenderList;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.window.Window;

import java.nio.IntBuffer;

public class Screen {
    public static int FPS;
    private ControllerDispatcher controllerDispatcher;
    private Scene scene;
    private Window window;
    public boolean isInFocus;
    public static final int defaultW = 1200;
    public static final int defaultH = 720;

    public void init() {
        Game game = Game.getGame();
        this.isInFocus = true;
        game.getLogManager().log("Starting screen!");
        if (this.tryToBuildScreen()) {
            GL.createCapabilities();
            ItemRenderList.init();
            this.scene = new Scene(new SceneWorld(game.getPhysX().getWorld()));
            this.scene.init();
            this.setWindowCallbacks();
            this.createControllerDispatcher(this.getWindow());
            game.getLogManager().log("Screen built successful");
        } else {
            game.getLogManager().error("Screen build error!");
        }
    }

    private void createControllerDispatcher(Window window) {
        this.controllerDispatcher = new ControllerDispatcher(window);
    }

    public void startScreen() {
        this.updateScreen();
        Game.getGame().getLogManager().log("Stopping screen...");
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
            Game.getGame().getLogManager().error("Error, while initializing GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL20.GL_TRUE);
        this.window = new Window(new Window.WindowProperties(Screen.defaultW, Screen.defaultH, "Build " + Game.build));
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
        return this.getScene().getRenderWorld();
    }

    public ICamera getCamera() {
        return this.getScene().getCurrentCamera();
    }

    private void updateScreen() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Game.getGame().getProfiler().startSection(SectionManager.startSystem);
        Game.getGame().getProxy().onSystemStarted();
        Game.getGame().getProfiler().endSection(SectionManager.startSystem);
        Game.getGame().getProfiler().startSection(SectionManager.renderE);
        this.getRenderWorld().onWorldStart();
        this.getScene().preRender();
        this.controllerDispatcher.attachControllerTo(ControllerDispatcher.mouseKeyboardController, Game.getGame().getPlayerSP());
        int fps = 0;
        double lastFPS = GLFW.glfwGetTime();
        final float tps = 1.0f / PhysX.TICKS_PER_SECOND;
        double lastTime = 0;
        GLFW.glfwSetTime(0.0d);
        while (!Game.getGame().shouldBeClosed) {
            Game.getGame().shouldBeClosed = GLFW.glfwWindowShouldClose(this.getWindow().getDescriptor());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL30.glEnable(GL30.GL_CULL_FACE);
            GL30.glCullFace(GL30.GL_BACK);
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
            this.getRenderWorld().onWorldUpdate();
            if (this.getScene().getCurrentCamera() != null) {
                this.getScene().renderScene(MathHelper.clamp(progress, 0.0f, 1.0f));
            }
            if (this.getControllerDispatcher() != null) {
                this.getControllerDispatcher().updateController(this.isInFocus, this.getWindow());
            }
            GLFW.glfwSetInputMode(this.getWindow().getDescriptor(), GLFW.GLFW_CURSOR, !this.isInFocus ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_HIDDEN);
            GLFW.glfwSwapBuffers(this.getWindow().getDescriptor());
            GLFW.glfwPollEvents();
        }
        this.getScene().postRender();
        this.getRenderWorld().onWorldEnd();
        Game.getGame().getProfiler().endSection(SectionManager.renderE);
    }

    public ControllerDispatcher getControllerDispatcher() {
        return this.controllerDispatcher;
    }

    public int getWidth() {
        return this.getWindow().getWidth();
    }

    public int getHeight() {
        return this.getWindow().getHeight();
    }

    public Vector2d getDimensions() {
        return this.getWindow().getWindowDimensions();
    }

    public Scene getScene() {
        return this.scene;
    }

    public Window getWindow() {
        return this.window;
    }
}
