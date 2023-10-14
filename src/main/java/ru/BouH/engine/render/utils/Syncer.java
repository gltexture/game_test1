package ru.BouH.engine.render.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Syncer {
    private final AtomicBoolean atomicBoolean;

    public Syncer() {
        this.atomicBoolean = new AtomicBoolean(false);
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
