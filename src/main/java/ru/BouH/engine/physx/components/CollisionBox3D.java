package ru.BouH.engine.physx.components;

import org.joml.Intersectiond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.physx.entities.PhysEntity;

public class CollisionBox3D implements ICollision {
    private double minX;
    private double minY;
    private double minZ;

    private double maxX;
    private double maxY;
    private double maxZ;
    private double scale;

    private double lengthX;
    private double lengthY;
    private double lengthZ;


    public CollisionBox3D(PhysEntity physEntity, double width, double height) {
        this.setBox(physEntity, width, height);
        this.scale = 1.0f;
    }

    public CollisionBox3D(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.setBox(minX, minY, minZ, maxX, maxY, maxZ);
        this.scale = 1.0f;
    }

    public void setBox(PhysEntity physEntity, double width, double height) {
        this.setBox(physEntity.getPosition().x - width, physEntity.getPosition().y - height, physEntity.getPosition().z - width, physEntity.getPosition().x + width, physEntity.getPosition().y + height, physEntity.getPosition().z + width);
    }

    public void setBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.lengthX = maxX - minX;
        this.lengthZ = maxZ - minZ;
        this.lengthY = maxY - minY;
    }

    public boolean check(CollisionBox3D collisionBox3D) {
        return this.lengthX == collisionBox3D.lengthX && this.lengthY == collisionBox3D.lengthY && this.lengthZ == collisionBox3D.lengthZ;
    }

    public void moveBox(double x, double y, double z) {
        this.setBox(this.getMinX() + x, this.getMinY() + y, this.getMinZ() + z, this.getMaxX() + x, this.getMaxY() + y, this.getMaxZ() + z);
    }

    public void setPos(double x, double y, double z) {
        this.setBox(x - this.getLengthX() / 2, y - this.getLengthY() / 2, z - this.getLengthZ() / 2, x + this.getLengthX() / 2, y + this.getLengthY() / 2, z + this.getLengthZ() / 2);
    }

    public boolean checkCollision(CollisionBox3D collisionBox3D) {
        if (collisionBox3D == null) {
            return false;
        }
        return Intersectiond.testAabAab(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ(), collisionBox3D.getMinX(), collisionBox3D.getMinY(), collisionBox3D.getMinZ(), collisionBox3D.getMaxX(), collisionBox3D.getMaxY(), collisionBox3D.getMaxZ());
    }

    public Vector2d checkVectorTrace(Vector3d start, Vector3d end) {
        if (start == null || end == null) {
            return null;
        }
        Vector2d vector2d = new Vector2d(0, 0);
        boolean flag = Intersectiond.intersectRayAab(start.x, start.y, start.z, end.x, end.y, end.z, this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ(), vector2d);
        return flag ? vector2d : null;
    }

    public CollisionBox3D copy() {
        return new CollisionBox3D(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }

    public double getLengthX() {
        return this.lengthX;
    }

    public double getLengthY() {
        return this.lengthY;
    }

    public double getLengthZ() {
        return this.lengthZ;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getMinX() {
        return this.minX - ((this.getScale() - 1.0f) / 2.0f);
    }

    public double getMinY() {
        return this.minY - ((this.getScale() - 1.0f) / 2.0f);
    }

    public double getMinZ() {
        return this.minZ - ((this.getScale() - 1.0f) / 2.0f);
    }

    public double getMaxX() {
        return this.maxX + ((this.getScale() - 1.0f) / 2.0f);
    }

    public double getMaxY() {
        return this.maxY + ((this.getScale() - 1.0f) / 2.0f);
    }

    public double getMaxZ() {
        return this.maxZ + ((this.getScale() - 1.0f) / 2.0f);
    }
}
