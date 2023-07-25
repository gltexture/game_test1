package ru.BouH.engine.physx.entities;

import org.joml.Vector3d;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.world.World;

public class PhysMoving extends PhysEntity {
    public double motionX;
    public double motionY;
    public double motionZ;
    private boolean isStuck;

    protected PhysMoving(final World world, String name) {
        super(world, name);
    }

    public Vector3d getMotionVector() {
        return new Vector3d(this.motionX, this.motionY, this.motionZ);
    }

    public void setMotion(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void addMotion(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
    }

    public void updateEntity() {
        super.updateEntity();
        this.getPosition().add(this.motionX, this.motionY, this.motionZ);
        this.movingStop();
    }

    protected PhysEntity traceMotion(CollisionBox3D collisionBox3D, Vector3d posPrev, Vector3d posCurr) {
        double x = posPrev.x;
        double y = posPrev.y;
        double z = posPrev.z;
        double d1 = 0.05d;
        for (PhysEntity physEntity : this.getWorld().getEntitySet()) {
            while (x != posCurr.x && y != posCurr.y && z != posCurr.z) {
                CollisionBox3D collisionBox3D1 = collisionBox3D.copy();
                if (posCurr.x > x) {
                    x = Math.min(x + d1, posCurr.x);
                } else {
                    x = Math.max(x - d1, posCurr.x);
                }
                if (posCurr.y > y) {
                    y = Math.min(y + d1, posCurr.y);
                } else {
                    y = Math.max(x - d1, posCurr.y);
                }
                if (posCurr.x > x) {
                    z = Math.min(z + d1, posCurr.z);
                } else {
                    z = Math.max(z - d1, posCurr.z);
                }
                collisionBox3D1.moveBox(x, y, z);
                if (physEntity.checkCollision(collisionBox3D1)) {
                    this.onCollide(collisionBox3D1, physEntity);
                    return physEntity;
                }
            }
        }
        return null;
    }

    protected void detectCollisions() {
        this.getCollideList().clear();
        for (PhysEntity physEntity : this.getWorld().getEntitySet()) {
            CollisionBox3D collisionBox3DX = this.getCollisionBox3D().copy();
            CollisionBox3D collisionBox3DY = this.getCollisionBox3D().copy();
            CollisionBox3D collisionBox3DZ = this.getCollisionBox3D().copy();
            collisionBox3DX.moveBox(this.motionX, 0, 0);
            collisionBox3DY.moveBox(0, this.motionY, 0);
            collisionBox3DZ.moveBox(0, 0, this.motionZ);
            if (physEntity != this) {
                if (physEntity.getCollisionBox3D().checkCollision(collisionBox3DX) || physEntity.getCollisionBox3D().checkCollision(collisionBox3DY) || physEntity.getCollisionBox3D().checkCollision(collisionBox3DZ)) {
                    this.onCollide(this.getCollisionBox3D(), physEntity);
                    this.getCollideList().add(physEntity);
                } else {
                    Vector3d posCurr = new Vector3d(this.getPosition());
                    posCurr.add(this.motionX, this.motionY, this.motionZ);
                    if (!this.getPosition().equals(posCurr, 0.0d)) {
                        double x = this.getPosition().x;
                        double y = this.getPosition().y;
                        double z = this.getPosition().z;
                        double d1 = 0.025d;
                        CollisionBox3D collisionBox3D1 = this.getCollisionBox3D().copy();
                        while (x != posCurr.x || y != posCurr.y || z != posCurr.z) {
                            if (posCurr.x > x) {
                                x = Math.min(x + d1, posCurr.x);
                            } else {
                                x = Math.max(x - d1, posCurr.x);
                            }
                            if (posCurr.y > y) {
                                y = Math.min(y + d1, posCurr.y);
                            } else {
                                y = Math.max(y - d1, posCurr.y);
                            }
                            if (posCurr.z > z) {
                                z = Math.min(z + d1, posCurr.z);
                            } else {
                                z = Math.max(z - d1, posCurr.z);
                            }
                            collisionBox3D1.setPos(x, y, z);
                            if (physEntity.checkCollision(collisionBox3D1)) {
                                this.onCollide(collisionBox3D1, physEntity);
                                this.getCollideList().add(physEntity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    protected void onCollide(CollisionBox3D collisionBox3D, PhysEntity physEntity) {
        double newX = this.getPosition().x;
        double newY = this.getPosition().y;
        double newZ = this.getPosition().z;
        double deltaX = Math.min(collisionBox3D.getMaxX(), physEntity.getCollisionBox3D().getMaxX()) - Math.max(collisionBox3D.getMinX(), physEntity.getCollisionBox3D().getMinX());
        double deltaY = Math.min(collisionBox3D.getMaxY(), physEntity.getCollisionBox3D().getMaxY()) - Math.max(collisionBox3D.getMinY(), physEntity.getCollisionBox3D().getMinY());
        double deltaZ = Math.min(collisionBox3D.getMaxZ(), physEntity.getCollisionBox3D().getMaxZ()) - Math.max(collisionBox3D.getMinZ(), physEntity.getCollisionBox3D().getMinZ());
        if (deltaX < deltaY && deltaX < deltaZ) {
            double centX = physEntity.getCollisionBox3D().getMinX() + (physEntity.getCollisionBox3D().getMaxX() - physEntity.getCollisionBox3D().getMinX()) / 2.0d;
            if (this.getPosition().x >= centX) {
                if (this.motionX < 0) {
                    this.motionX = 0.0f;
                }
                newX = physEntity.getCollisionBox3D().getMaxX() + (this.getCollisionBox3D().getLengthX() / 2);
            } else {
                if (this.motionX > 0) {
                    this.motionX = 0.0f;
                }
                newX = physEntity.getCollisionBox3D().getMinX() - (this.getCollisionBox3D().getLengthX() / 2);
            }
        }
        if (deltaY < deltaZ && deltaY < deltaX) {
            double centY = physEntity.getCollisionBox3D().getMinY() + (physEntity.getCollisionBox3D().getMaxY() - physEntity.getCollisionBox3D().getMinY()) / 2.0d;
            if (this.getPosition().y >= centY) {
                if (this.motionY < 0) {
                    this.motionY = 0.0f;
                }
                newY = physEntity.getCollisionBox3D().getMaxY() + (this.getCollisionBox3D().getLengthY() / 2);
            } else {
                if (this.motionY > 0) {
                    this.motionY = 0.0f;
                }
                newY = physEntity.getCollisionBox3D().getMinY() - (this.getCollisionBox3D().getLengthY() / 2);
            }
        }
        if (deltaZ < deltaY && deltaZ < deltaX) {
            double centZ = physEntity.getCollisionBox3D().getMinZ() + (physEntity.getCollisionBox3D().getMaxZ() - physEntity.getCollisionBox3D().getMinZ()) / 2.0d;
            if (this.getPosition().z >= centZ) {
                if (this.motionZ < 0) {
                    this.motionZ = 0.0f;
                }
                newZ = physEntity.getCollisionBox3D().getMaxZ() + (this.getCollisionBox3D().getLengthZ() / 2);
            } else {
                if (this.motionZ > 0) {
                    this.motionZ = 0.0f;
                }
                newZ = physEntity.getCollisionBox3D().getMinZ() - (this.getCollisionBox3D().getLengthZ() / 2);
            }
        }
        this.getPosition().set(newX, newY, newZ);
    }

    public boolean isStuck() {
        return this.isStuck;
    }

    protected void movingStop() {
        this.motionX *= 0.15f;
        this.motionZ *= 0.15f;
    }
}
