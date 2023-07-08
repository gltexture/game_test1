package ru.BouH.engine.physx.components;

public enum MaterialType {
    Rock("sounds/rock.ogg"),
    Grass("sounds/grass.ogg"),
    Wood("sounds/wood.ogg");

    private final String stepSound;

    MaterialType(String stepSound) {
        this.stepSound = stepSound;
    }

    public String getStepSound() {
        return this.stepSound;
    }
}
