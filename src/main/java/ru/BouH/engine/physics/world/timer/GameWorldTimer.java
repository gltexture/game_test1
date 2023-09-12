package ru.BouH.engine.physics.world.timer;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.screen.Screen;

public class GameWorldTimer implements IPhysTimer {
    private final World world;
    public static int TPS;

    public GameWorldTimer() {
        this.world = new World();
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        try {
            Game.getGame().getLogManager().debug("Starting phys-X!");
            long i = System.currentTimeMillis();
            long l = 0L;
            Game.getGame().getProfiler().startSection(SectionManager.physX);
            this.getWorld().onWorldStart();
            while (!Game.getGame().isShouldBeClosed()) {
                synchronized (PhysicThreadManager.locker) {
                    PhysicThreadManager.locker.wait();
                }
                long j = System.currentTimeMillis();
                long k = j - i;
                l += k;
                i = j;
                while (l > PhysicThreadManager.getTicksForUpdate(TPS)) {
                    l -= PhysicThreadManager.getTicksForUpdate(TPS);
                    this.getWorld().onWorldUpdate();
                    GameWorldTimer.TPS += 1;
                }
                Thread.sleep(Math.max(1L, PhysicThreadManager.getTicksForUpdate(TPS) - l));
            }
            this.getWorld().onWorldEnd();
            Game.getGame().getProfiler().endSection(SectionManager.physX);
            Game.getGame().getLogManager().debug("Stopping phys-X!");
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        }
    }

    public World getWorld() {
        return this.world;
    }
}
