package ru.BouH.engine.physx.world.surface;

import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.world.World;

public class Terrain extends PhysEntity {
    private final int startY;
    private final float size;

    public Terrain(World world, int startY, float size) {
        super(world, "terrain");
        this.startY = startY;
        this.size = size;
        this.setCollisionBox3D(new CollisionBox3D(this.getPosition().x - size / 2, startY, this.getPosition().z - size / 2, this.getPosition().x + size / 2, startY, this.getPosition().z + size / 2));
    }

    public void updateEntity() {
    }

    public int getStartY() {
        return this.startY;
    }

    public float getSize() {
        return this.size;
    }
}
