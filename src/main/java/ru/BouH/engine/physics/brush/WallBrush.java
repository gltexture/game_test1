package ru.BouH.engine.physics.brush;

import org.joml.Vector3d;
import ru.BouH.engine.physics.world.World;

public class WallBrush extends Plane4dBrush {
    public WallBrush(World world, Vector3d point1, Vector3d point2, Vector3d pos, String itemName) {
        super(world, new Vector3d[]{point1, new Vector3d(point1.x, point1.y, point2.z), new Vector3d(point1.x, point2.y, point1.z), point2}, pos, itemName);
    }

    public WallBrush(World world, Vector3d point1, Vector3d point2, String itemName) {
        this(world, point1, point2, new Vector3d(0.0d), itemName);
    }

    public WallBrush(World world, Vector3d point1, Vector3d point2) {
        this(world, point1, point2, new Vector3d(0.0d), "plane_4d");
    }
}
