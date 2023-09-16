package ru.BouH.engine.game;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.game.g_static.render.RenderResources;
import ru.BouH.engine.game.jframe.ProgressBar;
import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.game.profiler.Profiler;
import ru.BouH.engine.game.profiler.Section;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

import java.util.Random;

public class Game {
    public static final String build = "07.09.2023";
    public static long rngSeed;
    private EngineSystem engineSystem;
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

    public static void main(String[] args) throws InterruptedException {
        Game.startScreen = new Game();
        Game.getGame().engineSystem = new EngineSystem();
        Game.getGame().engineSystem.startSystem();
    }

    public EngineSystem getEngineSystem() {
        return this.engineSystem;
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

    public static class EngineSystem {
        public static final Object logicLocker = new Object();
        private Thread thread;
        private final RenderResources renderResources;
        private boolean threadHasStarted;

        public EngineSystem() {
            this.thread = null;
            this.threadHasStarted = false;
            this.renderResources = new RenderResources();
        }

        @SuppressWarnings("all")
        public void startSystem() {
            if (this.threadHasStarted) {
                Game.getGame().getLogManager().warn("Engine thread is currently running!");
                return;
            }
            this.thread = new Thread(() -> {
                try {
                    Game.getGame().getProfiler().startSection(SectionManager.startSystem);
                    Game.getGame().shouldBeClosed = false;
                    Game.getGame().getProfiler().startSection(SectionManager.game);
                    Game.getGame().getPhysicThreadManager().initService();
                    Game.getGame().getProfiler().startSection(SectionManager.preLoading);
                    this.preLoading();
                    Game.getGame().getProfiler().endSection(SectionManager.preLoading);
                    this.postLoading();
                    Game.getGame().getProfiler().endSection(SectionManager.startSystem);
                    Game.getGame().getScreen().startScreen();
                } finally {
                    Game.getGame().getPhysicThreadManager().destroy();
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.notifyAll();
                    }
                    synchronized (Game.EngineSystem.logicLocker) {
                        Game.EngineSystem.logicLocker.notifyAll();
                    }
                    while (Game.getGame().getPhysicThreadManager().checkActivePhysics()) {
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Game.getGame().getProfiler().endSection(SectionManager.game);
                    Game.getGame().getProfiler().stopAllSections();
                    Game.getGame().displayProfilerResult(Game.getGame().getProfiler());
                }
            });
            this.thread.setName("game");
            this.thread.start();
        }

        private void preLoading() {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(0);
            progressBar.showBar();
            Game.getGame().getScreen().buildScreen();
            this.preLoadingResources();
            progressBar.setProgress(50);
            Game.getGame().getScreen().initScreen();
            this.populateEnvironment();
            progressBar.setProgress(100);
            Game.getGame().getScreen().showWindow();
            progressBar.hideBar();
        }

        private void postLoading() {
            synchronized (EngineSystem.logicLocker) {
                EngineSystem.logicLocker.notifyAll();
            }
            this.threadHasStarted = true;
        }

        private void populateEnvironment() {
            World world = Game.getGame().getPhysicsWorld();
            Game.getGame().getLogManager().log("Populating environment...");
            GameEvents.populate(world);
            Game.getGame().getProxy().getLocalPlayer().addPlayerInWorlds(Game.getGame().getProxy());
            Game.getGame().getLogManager().log("Environment populated!");
        }

        private void preLoadingResources() {
            Game.getGame().getLogManager().log("Loading rendering resources...");
            this.renderResources.preLoad();
            Game.getGame().getLogManager().log("Rendering resources loaded!");
        }
    }
}
