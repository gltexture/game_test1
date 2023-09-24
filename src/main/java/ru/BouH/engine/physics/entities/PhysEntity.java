package ru.BouH.engine.physics.entities;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;

public abstract class PhysEntity extends CollidableWorldItem {
    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, scale, pos, rot, name);
    }

    public PhysEntity(World world, String name, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, name, properties, 1.0d, pos, rot);
    }

    public PhysEntity(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_ent", properties, scale, pos, rot);
    }

    public PhysEntity(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_ent", properties, 1.0d, pos, rot);
    }
}
