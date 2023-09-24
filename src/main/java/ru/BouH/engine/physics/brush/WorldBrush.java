package ru.BouH.engine.physics.brush;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

public abstract class WorldBrush extends PhysEntity {

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties, String name) {
        super(world, name, properties, 1.0d, new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties) {
        this(world, properties, "brush_ent");
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getRigidBodyObject().makeStatic();
    }

    public boolean canBeDestroyed() {
        return false;
    }
}
