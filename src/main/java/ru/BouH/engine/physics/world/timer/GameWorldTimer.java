package ru.BouH.engine.physics.world.timer;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;

import java.util.Iterator;
import java.util.List;

public class GameWorldTimer implements IPhysTimer {
    public static int TPS;
    private final World world;

    public GameWorldTimer() {
        this.world = new World();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        try {
            Game.getGame().getLogManager().debug("Starting phys-X!");
            Game.getGame().getProfiler().startSection(SectionManager.physX);
            this.getWorld().onWorldStart();
            synchronized (Game.EngineSystem.logicLocker) {
                Game.EngineSystem.logicLocker.wait();
            }
            long i = System.currentTimeMillis();
            long l = 0L;
            while (!Game.getGame().isShouldBeClosed()) {
                long j = System.currentTimeMillis();
                long k = j - i;
                l += k;
                i = j;
                while (l > PhysicThreadManager.getTicksForUpdate(TPS)) {
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.wait();
                    }
                    l -= PhysicThreadManager.getTicksForUpdate(TPS);
                    this.getWorld().onWorldUpdate();
                }
                GameWorldTimer.TPS += 1;
                Thread.sleep(Math.max(1L, PhysicThreadManager.getTicksForUpdate(TPS) - l));
            }
            this.getWorld().onWorldEnd();
            Game.getGame().getProfiler().endSection(SectionManager.physX);
            Game.getGame().getLogManager().debug("Stopping phys-X!");
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        } finally {
            this.cleanResources();
        }
    }

    public void cleanResources() {
        Game.getGame().getLogManager().log("Cleaning game world resources...");
        List<WorldItem> worldItems = this.getWorld().getAllWorldItems();
        Iterator<WorldItem> worldItemIterator = worldItems.iterator();
        while (worldItemIterator.hasNext()) {
            WorldItem worldItem = worldItemIterator.next();
            worldItem.onDestroy(this.getWorld());
            worldItemIterator.remove();
        }
    }

    public World getWorld() {
        return this.world;
    }
}
