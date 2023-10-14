package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.GameWorldTimer;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.screen.Screen;

public class Proxy {
    private final GameWorldTimer gameWorldTimer;
    private final Screen screen;
    private LocalPlayer localPlayer;

    public Proxy(GameWorldTimer gameWorldTimer, Screen screen) {
        this.gameWorldTimer = gameWorldTimer;
        this.screen = screen;
    }

    public void createLocalPlayer() {
        this.localPlayer = new LocalPlayer(gameWorldTimer.getWorld(), new Vector3d(195.0d, 1.0d, 0.0d));
    }

    public void addItemInWorlds(WorldItem worldItem, RenderData renderData) {
        try {
            this.gameWorldTimer.getWorld().addItem(worldItem);
            this.screen.getRenderWorld().addItem(worldItem, renderData);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLight(ILight light) {
        this.screen.getRenderWorld().getEnvironment().getLightManager().addLight(light);
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public EntityPlayerSP getPlayerSP() {
        return this.getLocalPlayer().getEntityPlayerSP();
    }

    public void removeEntityFromWorlds(PhysEntity physEntity) {
        this.gameWorldTimer.getWorld().removeItem(physEntity);
    }

    public void clearEntities() {
        this.gameWorldTimer.getWorld().clearAllItems();
    }
}
