package ru.BouH.engine.physics.world;

import com.bulletphysics.dynamics.RigidBody;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.collision.JBulletPhysics;
import ru.BouH.engine.physics.world.object.IDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.BulletWorldTimer;
import ru.BouH.engine.physics.world.timer.GameWorldTimer;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class World implements IWorld {
    private final List<WorldItem> allWorldItems;
    private final List<JBulletPhysics> allJBItems;
    private final List<IDynamic> allDynamicItems;
    private final List<WorldItem> toCleanItems;
    private boolean collectionsWaitingRefresh;
    private int ticks;

    public World() {
        this.allWorldItems = new ArrayList<>();
        this.allJBItems = new ArrayList<>();
        this.allDynamicItems = new ArrayList<>();
        this.toCleanItems = new ArrayList<>();
    }

    public static boolean isItemDynamic(WorldItem worldItem) {
        return worldItem instanceof IDynamic;
    }

    public static boolean isItemJB(WorldItem worldItem) {
        return worldItem instanceof JBulletPhysics;
    }

    public BulletWorldTimer getBulletTimer() {
        return Game.getGame().getPhysicThreadManager().getBulletWorldTimer();
    }

    public GameWorldTimer getGameWorldTimer() {
        return Game.getGame().getPhysicThreadManager().getGameWorldTimer();
    }

    public void onWorldStart() {
        Game.getGame().getProfiler().startSection(SectionManager.physWorld);
        Game.getGame().getLogManager().log("Creating local player");
        Game.getGame().getProxy().createLocalPlayer();
        Game.getGame().getLogManager().log("Local player created!");
    }

    public void onWorldEnd() {
        Game.getGame().getProfiler().endSection(SectionManager.physWorld);
        this.clearAllItems();
    }

    public void addLight(ILight iLight) {
        Game.getGame().getProxy().addLight(iLight);
    }

    public void addItem(WorldItem worldItem) {
        this.addItemInWorld(worldItem);
    }

    public void removeItem(WorldItem worldItem) {
        this.toCleanItems.add(worldItem);
    }

    public void clearAllItems() {
        this.getAllWorldItems().forEach(WorldItem::setDead);
    }

    public void onWorldUpdate() {
        List<WorldItem> copy1 = new ArrayList<>(this.getAllWorldItems());
        if (this.collectionsWaitingRefresh) {
            this.getAllDynamicItems().clear();
            this.getAllDynamicItems().addAll(copy1.stream().filter(World::isItemDynamic).map(e -> (IDynamic) e).collect(Collectors.toList()));
            synchronized (BulletWorldTimer.lock) {
                this.getAllJBItems().clear();
                this.getAllJBItems().addAll(copy1.stream().filter(World::isItemJB).map(e -> (JBulletPhysics) e).collect(Collectors.toList()));
            }
            this.collectionsWaitingRefresh = false;
        }
        List<IDynamic> copy2 = new ArrayList<>(this.getAllDynamicItems());
        for (IDynamic iDynamic : copy2) {
            if (Game.getGame().isShouldBeClosed()) {
                break;
            }
            iDynamic.onUpdate(this);
        }
        for (WorldItem worldItem : this.toCleanItems) {
            this.collectionsWaitingRefresh = true;
            worldItem.onDestroy(this);
            if (World.isItemJB(worldItem)) {
                JBulletPhysics jbItem = (JBulletPhysics) worldItem;
                RigidBody rigidBody = jbItem.getRigidBody();
                if (rigidBody != null) {
                    this.getBulletTimer().removeRigidBodyFromWorld(rigidBody);
                }
            }
            this.getAllWorldItems().remove(worldItem);
        }
        this.toCleanItems.clear();
        this.ticks += 1;
    }

    public int getTicks() {
        return this.ticks;
    }

    private void addItemInWorld(WorldItem worldItem) throws GameException {
        if (worldItem == null) {
            throw new GameException("Tried to pass NULL item in world");
        }
        this.collectionsWaitingRefresh = true;
        worldItem.onSpawn(this);
        this.getAllWorldItems().add(worldItem);
    }

    public int countItems() {
        return this.getAllWorldItems().size();
    }

    public synchronized List<IDynamic> getAllDynamicItems() {
        return this.allDynamicItems;
    }

    public synchronized List<JBulletPhysics> getAllJBItems() {
        return this.allJBItems;
    }

    public synchronized List<WorldItem> getAllWorldItems() {
        return this.allWorldItems;
    }
}
