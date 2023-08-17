package ru.BouH.engine.physx.world;

import com.bulletphysics.dynamics.RigidBody;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physx.collision.JBulletPhysics;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class World implements IWorld {
    private final BulletManager bulletManager;
    private List<WorldItem> allWorldItems;

    public World() {
        this.allWorldItems = new ArrayList<>();
        this.bulletManager = new BulletManager();
    }

    public BulletManager getPhysXBulletManager() {
        return this.bulletManager;
    }

    public void onWorldStart() {
        Game.getGame().getProfiler().startSection(SectionManager.physWorld);
        Game.getGame().getLogManager().log("Creating local player");
        Game.getGame().getProxy().createLocalPlayer();
        Game.getGame().getLogManager().log("Local player created!");
        this.getPhysXBulletManager().startBulletThread();
    }

    public void onWorldEnd() {
        Game.getGame().getProfiler().endSection(SectionManager.physWorld);
        this.clearAllItems();
    }

    public void addItem(WorldItem worldItem) {
        this.addItemInWorld(worldItem);
    }

    public void removeEntity(PhysEntity physEntity) {
        physEntity.setDead();
    }

    public void clearAllItems() {
        this.getAllWorldItems().forEach(WorldItem::setDead);
    }

    public void onWorldUpdate() {
        List<WorldItem> copy = new ArrayList<>(this.getAllWorldItems());
        for (WorldItem worldItem : copy) {
            if (worldItem instanceof IDynamic) {
                ((IDynamic) worldItem).onUpdate(this);
            }
            if (worldItem.isDead()) {
                if (worldItem instanceof JBulletPhysics) {
                    JBulletPhysics JBulletPhysics = (JBulletPhysics) worldItem;
                    RigidBody rigidBody = JBulletPhysics.getRigidBody();
                    if (rigidBody != null) {
                        this.getPhysXBulletManager().removeRigidBodyFromWorld(rigidBody);
                    }
                }
                worldItem.onDestroy(this);
                this.getAllWorldItems().remove(worldItem);
            }
            worldItem.ticksExisted += 1;
        }
    }

    private void addItemInWorld(WorldItem worldItem) throws GameException {
        if (worldItem == null) {
            throw new GameException("Tried to pass NULL item in world");
        }
        worldItem.onSpawn(this);
        this.getAllWorldItems().add(worldItem);
    }

    public int countItems() {
        return this.getAllWorldItems().size();
    }

    public List<JBulletPhysics> getAllWorldСollidableItems() {
        return this.getAllWorldItems().stream().filter(e -> e instanceof JBulletPhysics).map(e -> (JBulletPhysics) e).collect(Collectors.toList());
    }

    public List<WorldItem> getAllWorldItems() {
        return this.allWorldItems;
    }
}
