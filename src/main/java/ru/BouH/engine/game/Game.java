package ru.BouH.engine.game;

import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.game.profiler.Profiler;
import ru.BouH.engine.game.profiler.Section;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.screen.Screen;

import java.util.Random;

public class Game {
    public static final boolean DEBUG = true;
    public static final String build = "28.08.2023";
    public static long rngSeed;
    public static Random random;
    private static Game startScreen;
    private final GameLogging logManager;
    private final Profiler profiler;
    private final Screen screen;
    private final PhysX physX;
    private final Proxy proxy;
    public boolean shouldBeClosed = false;

    private Game() {
        Game.rngSeed = System.nanoTime();
        Game.random = new Random(Game.rngSeed);
        this.logManager = new GameLogging();
        this.profiler = new Profiler();
        this.physX = new PhysX();
        this.screen = new Screen();
        this.proxy = new Proxy(this.physX, this.screen);
    }

    public static Game getGame() {
        return Game.startScreen;
    }

    public static void main(String[] args) {
        try {
            Game.startScreen = new Game();
            Game.getGame().shouldBeClosed = false;
            Game.getGame().getProfiler().startSection(SectionManager.game);
            Game.getGame().getScreen().init();
            Game.getGame().getPhysX().init();
            Game.getGame().getScreen().startScreen();
        } finally {
            Game.getGame().getProfiler().endSection(SectionManager.game);
            Game.getGame().getProfiler().stopAllSections();
            Game.getGame().displayProfilerResult(Game.getGame().getProfiler());
        }
    }

    public void destroyGame() {
        Game.getGame().shouldBeClosed = true;
    }

    public void displayProfilerResult(Profiler profiler) {
        Game.getGame().getLogManager().debug("=======================================");
        Game.getGame().getLogManager().debug("[ PROFILER OUTPUT ]");
        Game.getGame().getLogManager().debug("======================================");
        for (Section section : profiler.allSections()) {
            Game.getGame().getLogManager().debug(section.toString());
        }
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

    public PhysX getPhysX() {
        return this.physX;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}
