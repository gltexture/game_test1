package ru.BouH.engine.physx;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.world.World;

public class PhysX {
    private final World world;
    public Thread worldThread;

    public PhysX() {
        this.world = new World();
    }

    public void init() {
        this.worldThread = new Thread(() -> {
            try {
                Game.getGame().getLogManager().debug("Starting phys!");
                this.addLocalPlayer();
                this.getWorld().addEntity(this.getWorld().getTerrain());
                long i = System.currentTimeMillis();
                long l = 0L;
                while(!Game.getGame().shouldBeClosed) {
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
                    while (l > 20L) {
                        l -= 20L;
                        this.world.onWorldUpdate();
                    }
                    Thread.sleep(Math.max(1L, 20L - l));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        this.startThread();
    }

    protected void startThread() {
        this.worldThread.setName("phys");
        this.worldThread.start();
    }

    private void addLocalPlayer() {
        this.getWorld().addEntity(this.getWorld().getLocalPlayer());
    }

    public World getWorld() {
        return this.world;
    }
}
