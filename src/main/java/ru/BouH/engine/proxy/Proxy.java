package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.events.WorldEvents;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physx.world.timer.GameWorldTimer;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.object.WorldItem;
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
        this.localPlayer = new LocalPlayer(gameWorldTimer.getWorld(), new Vector3d(390.0d, 5.0d, 0.0d));
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

    public void onSystemStarted() {
        WorldEvents.addEntities(this.gameWorldTimer.getWorld());
        WorldEvents.addBrushes(this.gameWorldTimer.getWorld());
        this.getLocalPlayer().addPlayerInWorlds(this);
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
