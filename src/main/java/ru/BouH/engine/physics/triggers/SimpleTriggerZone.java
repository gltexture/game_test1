package ru.BouH.engine.physics.triggers;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.bytedeco.bullet.global.BulletCollision;
import org.bytedeco.javacpp.annotation.ByRef;
import org.bytedeco.javacpp.annotation.Const;
import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimpleTriggerZone implements ITriggerZone {
    private final Set<CollidableWorldItem> btEnteredBodies;
    private btGhostObject ghostObject;
    private btCollisionShape collisionShape;
    protected final ITriggerZone.Zone zone;
    private ITrigger ITriggerEntering;
    private ITrigger ITriggerLeaving;

    public SimpleTriggerZone(ITriggerZone.Zone zone, ITrigger ITriggerEntering, ITrigger ITriggerLeaving) {
        this.ITriggerEntering = ITriggerEntering;
        this.ITriggerLeaving = ITriggerLeaving;
        this.zone = zone;
        this.btEnteredBodies = new HashSet<>();
    }

    public btGhostObject createGhostZone() {
        Game.getGame().getLogManager().log("Created new ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
        this.ghostObject = new btGhostObject();
        double d1_1 = this.getZone().getSize().x / 2.0d;
        double d1_2 = this.getZone().getSize().y / 2.0d;
        double d1_3 = this.getZone().getSize().z / 2.0d;
        this.collisionShape = new btSphereShape(new btBoxShape(new btVector3(d1_1, d1_2, d1_3)));
        this.ghostObject.setCollisionShape(this.collisionShape);
        this.ghostObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE);
        try (btTransform transform = this.ghostObject.getWorldTransform()) {
            transform.setOrigin(new btVector3(this.getZone().getLocation().x, this.getZone().getLocation().y, this.getZone().getLocation().z));
            this.ghostObject.setWorldTransform(transform);
        }
        return this.ghostObject;
    }

    public void onDestroy() {
        this.collisionShape.deallocate();
        this.ghostObject.deallocate();
        Game.getGame().getLogManager().log("Destroyed ITrigger zone: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    public void onUpdate(World world) {
        Set<CollidableWorldItem> temp = new HashSet<>();
        for (CollidableWorldItem collidableWorldItem : world.getAllBulletItems()) {
            btOverlappingPairCache btHashedOverlappingPairCache = world.getDynamicsWorld().getPairCache();
            boolean collided = btHashedOverlappingPairCache.findPair(collidableWorldItem.getRigidBodyObject().getBroadphaseHandle(), this.ghostObject.getBroadphaseHandle()) != null;
            if (collided) {
                this.onEnter(collidableWorldItem);
                temp.add(collidableWorldItem);
            }
            btHashedOverlappingPairCache.deallocate();
        }
        if (!this.btEnteredBodies.isEmpty()) {
            for (CollidableWorldItem collidableWorldItem : temp) {
                this.btEnteredBodies.remove(collidableWorldItem);
            }
            Iterator<CollidableWorldItem> collidableWorldItemIterator = this.btEnteredBodies.iterator();
            while (collidableWorldItemIterator.hasNext()) {
                CollidableWorldItem collidableWorldItem = collidableWorldItemIterator.next();
                this.onLeave(collidableWorldItem);
                collidableWorldItemIterator.remove();
            }
        }
        this.btEnteredBodies.addAll(temp);
    }

    public ITriggerZone.Zone getZone() {
        return this.zone;
    }

    public void onEnter(CollidableWorldItem collidableWorldItem) {
        this.getTriggerEntering().trigger(collidableWorldItem);
    }

    public void onLeave(CollidableWorldItem collidableWorldItem) {
        this.getTriggerLeaving().trigger(collidableWorldItem);
    }

    public void setTriggerLeaving(ITrigger ITrigger) {
        this.ITriggerLeaving = ITrigger;
    }

    public void setTriggerEntering(@NotNull ITrigger ITrigger) {
        this.ITriggerEntering = ITrigger;
    }

    public ITrigger getTriggerEntering() {
        return this.ITriggerEntering;
    }

    public ITrigger getTriggerLeaving() {
        return this.ITriggerLeaving;
    }
}
