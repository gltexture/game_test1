package ru.BouH.engine.physx.entities;

import org.joml.Vector3d;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.components.MaterialType;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.physx.world.WorldItem;

import java.util.HashSet;
import java.util.Set;

public abstract class PhysEntity extends WorldItem {
    public final World world;
    private final Vector3d position;
    private final Vector3d prevPosition;
    private final Vector3d rotation;
    private final Vector3d moveVector;
    private final Set<PhysEntity> collideList = new HashSet<>();
    private CollisionBox3D collisionBox3D;
    private double scale;
    private MaterialType materialType;
    private boolean isDead;

    public PhysEntity(final World world) {
        this(world, "entity");
    }

    public PhysEntity(final World world, String name) {
        super(name);
        this.world = world;
        this.position = new Vector3d(0.0f, 0.0f, 0.0f);
        this.prevPosition = new Vector3d(0.0f, 0.0f, 0.0f);
        this.rotation = new Vector3d(0.0f, 0.0f, 0.0f);
        this.moveVector = new Vector3d(0.0f, 0.0f, 0.0f);
        this.materialType = MaterialType.Rock;
        this.scale = 1.0d;
    }

    public void onSpawn() {
    }

    public void updateEntity() {
        this.moveVector.set(this.getPosition().x - this.getPrevPosition().x, this.getPosition().y - this.getPrevPosition().y, this.getPosition().z - this.getPrevPosition().z);
        this.moveVector.mul(-1);
        this.getPrevPosition().set(this.getPosition());
        this.detectCollisions();
    }

    protected void detectCollisions() {
        this.collideList.clear();
        for (PhysEntity physEntity : this.getWorld().getEntityList()) {
            if (physEntity != this && this.checkCollision(physEntity)) {
                this.collideList.add(physEntity);
            }
        }
    }

    public void setDead() {
        if (this.canBeDestroyed()) {
            this.isDead = true;
        }
    }

    public Vector3d getMoveVector() {
        return this.moveVector;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        this.getCollisionBox3D().setScale(scale);
    }

    public boolean canBeDestroyed() {
        return true;
    }

    public boolean isCollided() {
        return !this.collideList.isEmpty();
    }

    public Set<PhysEntity> getCollideList() {
        return this.collideList;
    }

    public boolean checkCollision(PhysEntity physEntity) {
        if (this.getCollisionBox3D() == null || physEntity.getCollisionBox3D() == null) {
            return false;
        }
        return this.getCollisionBox3D().checkCollision(physEntity.getCollisionBox3D());
    }

    public boolean checkCollision(CollisionBox3D collisionBox3D1) {
        if (this.getCollisionBox3D() == null || collisionBox3D1 == null) {
            return false;
        }
        return this.getCollisionBox3D().checkCollision(collisionBox3D1);
    }

    public MaterialType getMaterialType() {
        return this.materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public CollisionBox3D getCollisionBox3D() {
        return this.collisionBox3D;
    }

    public void setCollisionBox3D(CollisionBox3D collisionBox3D) {
        this.collisionBox3D = collisionBox3D;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getPrevPosition() {
        return this.prevPosition;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public World getWorld() {
        return this.world;
    }
}
