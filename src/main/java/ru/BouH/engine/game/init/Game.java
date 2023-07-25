package ru.BouH.engine.game.init;

import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.screen.Screen;

public class Game {
    public static final boolean DEBUG = true;
    public static final String build = "25.07.2023";
    private static Game startScreen;
    private final GameLogging logManager;
    private final Screen screen;
    private final PhysX physX;
    private final Proxy proxy;
    public boolean shouldBeClosed = false;

    private Game() {
        this.logManager = new GameLogging();
        this.physX = new PhysX();
        this.screen = new Screen();
        this.proxy = new Proxy(this.physX, this.screen);
    }

    public static Game getGame() {
        return Game.startScreen;
    }

    public static void main(String[] args) {
        Game.startScreen = new Game();
        Game.getGame().shouldBeClosed = false;
        Game.getGame().getScreen().init();
        Game.getGame().getPhysX().init();
        Game.getGame().getProxy().addLocalPlayer();
        Game.getGame().getScreen().startScreen();
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
