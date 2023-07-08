package ru.BouH.engine.physx.world;

import io.netty.util.internal.ConcurrentSet;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.surface.Terrain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class World {
    private final Terrain terrain;
    @SuppressWarnings("deprecation")
    private final Set<PhysEntity> physEntityInfoSet = new ConcurrentSet<>();
    private EntityPlayerSP localPlayer;

    public World() {
        this.terrain = new Terrain(this, -2, 300);
        this.onWorldStart();
    }

    protected void onWorldStart() {
        this.localPlayer = new EntityPlayerSP(this);
        this.localPlayer.getPosition().set(0, 2, 0);
    }

    public void onWorldUpdate() {
        Iterator<PhysEntity> iterator = this.getEntitySet().iterator();
        while (iterator.hasNext()) {
            PhysEntity physEntity = iterator.next();
            if (physEntity.isDead()) {
                iterator.remove();
                Game.getGame().getLogManager().log("Removed entity in world - [" + physEntity.getItemName() + " - <id/" + physEntity.getItemId() + ">]");
            } else {
                physEntity.updateEntity();
            }
        }
    }

    public EntityPlayerSP getLocalPlayer() {
        return this.localPlayer;
    }

    public Set<PhysEntity> getEntitySet() {
        return this.physEntityInfoSet;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void addEntity(PhysEntity physEntity) {
        Game.getGame().getLogManager().log("Added new entity in world - [" + physEntity.getItemName() + " - <id/" + physEntity.getItemId() + ">]");
        physEntity.onSpawn();
        this.physEntityInfoSet.add(physEntity);
    }

    public void removeEntity(PhysEntity physEntity) {
        physEntity.setDead();
    }
}
