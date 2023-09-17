package ru.BouH.engine.physics.world;

import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.object.JBulletDynamic;
import ru.BouH.engine.physics.world.object.JBulletObject;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.BulletWorldTimer;
import ru.BouH.engine.physics.world.timer.GameWorldTimer;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class World implements IWorld {
    private final List<WorldItem> allWorldItems;
    private final List<JBulletDynamic> allJBItems;
    private final List<IWorldDynamic> allDynamicItems;
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
        return worldItem instanceof IWorldDynamic;
    }

    public static boolean isItemJBulletDynamic(WorldItem worldItem) {
        return worldItem instanceof JBulletDynamic;
    }

    public static boolean isItemJBulletObject(WorldItem worldItem) {
        return worldItem instanceof JBulletObject;
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

    public btDynamicsWorld getDynamicsWorld() {
        return this.getBulletTimer().dynamicsWorld();
    }

    public void onWorldUpdate() {
        List<WorldItem> copy1 = new ArrayList<>(this.getAllWorldItems());
        if (this.collectionsWaitingRefresh) {
            this.getAllDynamicItems().clear();
            this.getAllDynamicItems().addAll(copy1.stream().filter(World::isItemDynamic).map(e -> (IWorldDynamic) e).collect(Collectors.toList()));
            synchronized (BulletWorldTimer.lock) {
                this.getAllJBItems().clear();
                this.getAllJBItems().addAll(copy1.stream().filter(World::isItemJBulletDynamic).map(e -> (JBulletDynamic) e).collect(Collectors.toList()));
            }
            this.collectionsWaitingRefresh = false;
        }
        List<IWorldDynamic> copy2 = new ArrayList<>(this.getAllDynamicItems());
        for (IWorldDynamic iWorldDynamic : copy2) {
            iWorldDynamic.onUpdate(this);
        }
        this.clearItemsCollection(this.toCleanItems);
        this.toCleanItems.clear();
        this.ticks += 1;
    }

    private void clearItemsCollection(Collection<? extends WorldItem> collection) {
        for (WorldItem worldItem : collection) {
            this.collectionsWaitingRefresh = true;
            worldItem.onDestroy(this);
            if (World.isItemJBulletObject(worldItem)) {
                JBulletObject jbItem = (JBulletObject) worldItem;
                btRigidBody rigidBody = jbItem.getRigidBody();
                if (rigidBody != null) {
                    this.getBulletTimer().removeRigidBodyFromWorld(rigidBody);
                }
            }
            this.getAllWorldItems().remove(worldItem);
        }
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

    public synchronized List<IWorldDynamic> getAllDynamicItems() {
        return this.allDynamicItems;
    }

    public synchronized List<JBulletDynamic> getAllJBItems() {
        return this.allJBItems;
    }

    public synchronized List<WorldItem> getAllWorldItems() {
        return this.allWorldItems;
    }
}
