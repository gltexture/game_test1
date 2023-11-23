package ru.BouH.engine.render.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Syncer {
    private final AtomicBoolean atomicBoolean;
    private final CountDownLatch countDownLatch;

    public Syncer() {
        this.atomicBoolean = new AtomicBoolean(false);
        this.countDownLatch = new CountDownLatch(1);
    }

    public void tryBlock() throws InterruptedException {
        if (this.shouldSync()) {
            this.countDownLatch.await();
        }
    }

    public void unBlock() {
        this.syncDown();
        this.countDownLatch.countDown();
    }

    public void syncUp() {
        this.atomicBoolean.set(true);
    }

    public void syncDown() {
        this.atomicBoolean.set(false);
    }

    public boolean shouldSync() {
        return this.atomicBoolean.get();
    }
}
