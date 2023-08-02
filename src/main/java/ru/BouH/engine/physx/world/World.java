package ru.BouH.engine.physx.world;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.physx.world.surface.Terrain;

import java.util.*;

public class World {
    private final Terrain terrain;
    private final List<PhysEntity> entityList = new ArrayList<>();
    private final Deque<PhysEntity> entityDeque = new ArrayDeque<>();

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
        Iterator<PhysEntity> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysEntity physEntity = iterator.next();
            if (physEntity.isDead()) {
                iterator.remove();
                Game.getGame().getLogManager().log("Removed entity in world - [" + physEntity.getItemName() + " - <id/" + physEntity.getItemId() + ">]");
                continue;
            }
            physEntity.updateEntity();
        }
        while (!this.entityDeque.isEmpty()) {
            PhysEntity physEntity = this.entityDeque.pollFirst();
            this.entityList.add(physEntity);
            Game.getGame().getLogManager().log("Added new entity in world - [" + physEntity.getItemName() + " - <id/" + physEntity.getItemId() + ">]");
        }
    }

    public EntityPlayerSP getLocalPlayer() {
        return this.localPlayer;
    }

    public List<PhysEntity> getEntityList() {
        return this.entityList;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void addEntity(PhysEntity physEntity) {
        this.entityDeque.add(physEntity);
        physEntity.onSpawn();
    }

    public void removeEntity(PhysEntity physEntity) {
        physEntity.setDead();
    }
}
