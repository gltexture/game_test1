package ru.BouH.engine.physx;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.world.World;

public class PhysX {
    private final World world;
    public Thread worldThread;
    public static final int TICKS_PER_SECOND = 50;

    public PhysX() {
        this.world = new World();
    }

    private static long getTicksForUpdate() {
        return 1000L / PhysX.TICKS_PER_SECOND;
    }

    @SuppressWarnings("all")
    public void init() {
        this.worldThread = new Thread(() -> {
            try {
                Game.getGame().getLogManager().debug("Starting phys!");
                this.getWorld().addEntity(this.getWorld().getTerrain());
                long i = System.currentTimeMillis();
                long l = 0L;
                while (!Game.getGame().shouldBeClosed) {
                    long j = System.currentTimeMillis();
                    long k = j - i;
                    if (k >= 3000L && k % 1000 == 0) {
                        Game.getGame().getLogManager().debug("PhysX lags!");
                    }
                    if (k < 0) {
                        Game.getGame().getLogManager().error("PhysX time error!");
                    }
                    l += k;
                    i = j;
                    while (l > this.getTicksForUpdate()) {
                        l -= this.getTicksForUpdate();
                        Game.getGame().getProxy().tickWorlds();
                    }
                    Thread.sleep(Math.max(1L, this.getTicksForUpdate() - l));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        this.startThread();
    }

    protected void startThread() {
        this.worldThread.setName("physx");
        this.worldThread.start();
    }

    private void addLocalPlayer() {
        this.getWorld().addEntity(this.getWorld().getLocalPlayer());
    }

    public World getWorld() {
        return this.world;
    }
}
