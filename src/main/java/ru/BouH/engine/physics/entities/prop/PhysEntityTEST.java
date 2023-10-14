package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.OBB;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;

public class PhysEntityTEST extends WorldItem implements IWorldDynamic {
    public PhysEntityTEST(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        super(world, scale, pos, rot, itemName);
    }

    public PhysEntityTEST(World world, double scale, Vector3d pos, String itemName) {
        super(world, scale, pos, itemName);
    }

    public PhysEntityTEST(World world, double scale, String itemName) {
        super(world, scale, itemName);
    }

    public PhysEntityTEST(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public PhysEntityTEST(World world, Vector3d pos, String itemName) {
        super(world, pos, itemName);
    }

    public PhysEntityTEST(World world, String itemName) {
        super(world, itemName);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.setPosition(new Vector3d(this.getPosition()).add(1, 0, 0));
        if (this.getPosition().x >= 200) {
            this.setPosition(new Vector3d(0, 5, 0));
        }
    }
}
