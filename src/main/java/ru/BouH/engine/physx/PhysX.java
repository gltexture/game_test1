package ru.BouH.engine.physx;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physx.world.World;

public class PhysX {
    public static final int TICKS_PER_SECOND = 50;
    private final World world;
    public Thread worldThread;

    public PhysX() {
        this.world = new World();
    }

    public static long getTicksForUpdate() {
        return 1000L / PhysX.TICKS_PER_SECOND;
    }

    public void init() {
        this.startPhysTimer();
    }

    @SuppressWarnings("all")
    private void startPhysTimer() {
        this.worldThread = new Thread(() -> {
            try {
                Game.getGame().getLogManager().debug("Starting phys-X!");
                long i = System.currentTimeMillis();
                long l = 0L;
                Game.getGame().getProfiler().startSection(SectionManager.physX);
                this.getWorld().onWorldStart();
                while (!Game.getGame().shouldBeClosed) {
                    long j = System.currentTimeMillis();
                    long k = j - i;
                    if (k >= 3000L && k % 1000 == 0) {
                        Game.getGame().getLogManager().debug("Phys-X lags!");
                    }
                    if (k < 0) {
                        throw new GameException("Phys-X time error!");
                    }
                    l += k;
                    i = j;
                    while (l > PhysX.getTicksForUpdate()) {
                        l -= PhysX.getTicksForUpdate();
                        Game.getGame().getProxy().tickWorlds();
                    }
                    Thread.sleep(Math.max(1L, PhysX.getTicksForUpdate() - l));
                }
                this.getWorld().onWorldEnd();
                Game.getGame().getProfiler().endSection(SectionManager.physX);
                Game.getGame().getLogManager().debug("Stopping phys-X!");
            } catch (InterruptedException | GameException e) {
                throw new RuntimeException(e);
            }
        });
        this.startThread();
    }

    public void stopPhysX() {
        this.worldThread.interrupt();
    }

    public void resumePhysX() {
        this.worldThread.start();
    }

    public boolean isRunning() {
        return this.worldThread.isAlive() && !this.worldThread.isInterrupted();
    }

    protected void startThread() {
        this.worldThread.setName("phys-X");
        this.worldThread.start();
    }

    public World getWorld() {
        return this.world;
    }
}
