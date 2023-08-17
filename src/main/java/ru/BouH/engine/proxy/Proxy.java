package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.events.WorldEvents;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.screen.Screen;

public class Proxy {
    private final PhysX physX;
    private final Screen screen;
    private LocalPlayer localPlayer;

    public Proxy(PhysX physX, Screen screen) {
        this.physX = physX;
        this.screen = screen;
    }

    public void createLocalPlayer() {
        this.localPlayer = new LocalPlayer(physX.getWorld(), new Vector3d(0.0d, 5.0d, 0.0d));
    }

    public void addItemInWorlds(WorldItem worldItem, RenderData renderData) {
        try {
            this.physX.getWorld().addItem(worldItem);
            this.screen.getRenderWorld().addItem(worldItem, renderData);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSystemStarted() {
        WorldEvents.addEntities(this.physX.getWorld());
        WorldEvents.addBrushes(this.physX.getWorld());
        this.getLocalPlayer().addPlayerInWorlds(this);
    }

    public void tickWorlds() {
        this.physX.getWorld().onWorldUpdate();
        this.screen.getRenderWorld().tickWorld();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public EntityPlayerSP getPlayerSP() {
        return this.getLocalPlayer().getEntityPlayerSP();
    }

    public void removeEntityFromWorlds(PhysEntity physEntity) {
        this.physX.getWorld().removeEntity(physEntity);
    }

    public void clearEntities() {
        this.physX.getWorld().clearAllItems();
    }
}
