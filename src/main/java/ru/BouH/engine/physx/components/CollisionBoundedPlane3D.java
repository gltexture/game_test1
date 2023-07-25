package ru.BouH.engine.physx.components;

import org.joml.Vector3d;

public class CollisionBoundedPlane3D implements ICollision {
    private final Vector3d v1Start;
    private final Vector3d v1End;
    private final Vector3d v2Start;
    private final Vector3d v2End;

    public CollisionBoundedPlane3D(Vector3d v1Start, Vector3d v1End, Vector3d v2Start, Vector3d v2End) {
        this.v1Start = v1Start;
        this.v1End = v1End;
        this.v2Start = v2Start;
        this.v2End = v2End;
    }
}
