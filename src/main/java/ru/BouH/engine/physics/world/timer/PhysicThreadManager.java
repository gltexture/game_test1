package ru.BouH.engine.physics.world.timer;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.math.IntPair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class PhysicThreadManager {
    public static final Object locker = new Object();
    public static final IntPair WORLD_BORDERS = new IntPair(-750, 750);
    public static final int TICKS_PER_SECOND = 50;
    public static final int PHYS_THREADS = 2;
    private final ExecutorService executorService;
    private final GameWorldTimer gameWorldTimer;
    private final BulletWorldTimer bulletWorldTimer;
    private final int tps;

    public PhysicThreadManager(int tps) {
        this.tps = tps;
        this.executorService = Executors.newFixedThreadPool(PhysicThreadManager.PHYS_THREADS, new GamePhysicsThreadFactory("physics"));
        this.gameWorldTimer = new GameWorldTimer();
        this.bulletWorldTimer = new BulletWorldTimer(this.getGameWorldTimer().getWorld());
    }

    public static long getTicksForUpdate(int TPS) {
        return 1000L / TPS;
    }

    public boolean checkActivePhysics() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) this.getExecutorService();
        return executor.getActiveCount() > 0;
    }

    public void initService() {
        this.getExecutorService().execute(() -> {
            this.getGameWorldTimer().updateTimer(this.getTps());
        });
        this.getExecutorService().execute(() -> {
            this.getBulletWorldTimer().updateTimer(this.getTps());
        });
    }

    public void destroy() {
        this.getExecutorService().shutdown();
    }

    public int getTps() {
        return this.tps;
    }

    public GameWorldTimer getGameWorldTimer() {
        return this.gameWorldTimer;
    }

    public final BulletWorldTimer getBulletWorldTimer() {
        return this.bulletWorldTimer;
    }

    public final ExecutorService getExecutorService() {
        return this.executorService;
    }

    private static class GamePhysicsThreadFactory implements ThreadFactory {
        private final String threadName;

        public GamePhysicsThreadFactory(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(this.threadName);
            return thread;
        }
    }
}
