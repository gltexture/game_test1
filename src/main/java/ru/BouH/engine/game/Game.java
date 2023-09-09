package ru.BouH.engine.game;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.game.profiler.Profiler;
import ru.BouH.engine.game.profiler.Section;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.timer.GameWorldTimer;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.window.Window;

import java.util.Random;

public class Game {
    public static final String build = "07.09.2023";
    public static long rngSeed;
    public static Random random;
    private static Game startScreen;
    private final GameLogging logManager;
    private final Profiler profiler;
    private final Screen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private boolean shouldBeClosed = false;

    private Game() {
        Game.rngSeed = System.nanoTime();
        Game.random = new Random(Game.rngSeed);
        this.logManager = new GameLogging();
        this.profiler = new Profiler();
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.screen = new Screen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getGameWorldTimer(), this.getScreen());
    }

    public static double systemTime() {
        return GLFW.glfwGetTime();
    }

    public static Game getGame() {
        return Game.startScreen;
    }

    @SuppressWarnings("all")
    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = new Thread(() -> {
            try {
                Game.startScreen = new Game();
                Game.getGame().shouldBeClosed = false;
                Game.getGame().getProfiler().startSection(SectionManager.game);
                Game.getGame().getPhysicThreadManager().initService();
                Game.getGame().getScreen().init();
                Game.getGame().getScreen().startScreen();
            } finally {
                try {
                    Game.getGame().getPhysicThreadManager().destroy();
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.notifyAll();
                    }
                    while (Game.getGame().getPhysicThreadManager().checkActivePhysics()) {
                        Thread.sleep(25);
                    }
                    Game.getGame().getProfiler().endSection(SectionManager.game);
                    Game.getGame().getProfiler().stopAllSections();
                    Game.getGame().displayProfilerResult(Game.getGame().getProfiler());
                } catch (InterruptedException ignored) {
                }
            }
        });
        mainThread.setName("game");
        mainThread.start();
    }

    public void destroyGame() {
        Game.getGame().shouldBeClosed = true;
    }

    @SuppressWarnings("all")
    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public void displayProfilerResult(Profiler profiler) {
        Game.getGame().getLogManager().debug("=======================================");
        Game.getGame().getLogManager().debug("[ PROFILER OUTPUT ]");
        Game.getGame().getLogManager().debug("======================================");
        for (Section section : profiler.allSections()) {
            Game.getGame().getLogManager().debug(section.toString());
        }
    }

    public World getPhysicsWorld() {
        return this.getPhysicThreadManager().getGameWorldTimer().getWorld();
    }

    public SceneWorld getSceneWorld() {
        return this.getScreen().getRenderWorld();
    }

    public EntityPlayerSP getPlayerSP() {
        return this.getProxy().getPlayerSP();
    }

    public Profiler getProfiler() {
        return this.profiler;
    }

    public GameLogging getLogManager() {
        return this.logManager;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public PhysicThreadManager getPhysicThreadManager() {
        return this.physicThreadManager;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}
