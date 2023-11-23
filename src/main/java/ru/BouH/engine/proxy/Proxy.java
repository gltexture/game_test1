package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;
import ru.BouH.engine.render.environment.light.ILight;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.screen.Screen;

public class Proxy {
    private final PhysicsTimer physicsTimer;
    private final Screen screen;
    private LocalPlayer localPlayer;

    public Proxy(PhysicsTimer gameWorldTimer, Screen screen) {
        this.physicsTimer = gameWorldTimer;
        this.screen = screen;
    }

    public void createLocalPlayer() {
        this.localPlayer = new LocalPlayer(this.physicsTimer.getWorld(), new Vector3d(390.0d, 1.0d, 0.0d));
    }

    public void addItemInWorlds(WorldItem worldItem, RenderData renderData) {
        try {
            this.physicsTimer.getWorld().addItem(worldItem);
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
        this.physicsTimer.getWorld().removeItem(physEntity);
    }

    public void clearEntities() {
        this.physicsTimer.getWorld().clearAllItems();
    }
}
