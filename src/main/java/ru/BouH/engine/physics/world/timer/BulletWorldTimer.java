package ru.BouH.engine.physics.world.timer;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.*;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.object.JBulletDynamic;
import ru.BouH.engine.physics.world.object.JBulletObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.screen.timer.Timer;

public class BulletWorldTimer implements IPhysTimer {
    public static final Object lock = new Object();
    public static int TPS;
    private final World world;
    private final btBroadphaseInterface broadcaster;
    private final btCollisionConfiguration collisionConfiguration;
    private final btCollisionDispatcher collisionDispatcher;
    private final btDiscreteDynamicsWorld discreteDynamicsWorld;
    private final btConstraintSolver constraintSolve;

    public BulletWorldTimer(World world) {
        this.world = world;
        this.broadcaster = new btAxisSweep3(new btVector3(PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1()), new btVector3(PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2()));
        this.collisionConfiguration = new btDefaultCollisionConfiguration();
        this.collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        this.constraintSolve = new btSequentialImpulseConstraintSolver();
        this.discreteDynamicsWorld = new btDiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new btVector3(0, -10.0f, 0));
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS)  {
        final btDiscreteDynamicsWorld discreteDynamicsWorld1 = this.getDiscreteDynamicsWorld();
        if (discreteDynamicsWorld1 == null) {
            throw new GameException("Current Dynamics World is NULL!");
        }
        try {
            Game.getGame().getProfiler().startSection(SectionManager.bulletPhysWorld);
            synchronized (Game.EngineSystem.logicLocker) {
                Game.EngineSystem.logicLocker.wait();
            }
            long i = System.currentTimeMillis();
            long l = 0L;
            while (!Game.getGame().isShouldBeClosed()) {
                long j = System.currentTimeMillis();
                long k = j - i;
                l += k;
                i = j;
                while (l > PhysicThreadManager.getTicksForUpdate(TPS)) {
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.wait();
                    }
                    l -= PhysicThreadManager.getTicksForUpdate(TPS);
                    synchronized (BulletWorldTimer.lock) {
                        Timer.syncUp();
                        for (JBulletDynamic worldItem : this.world.getAllJBItems()) {
                            worldItem.onJBUpdate();
                        }
                        discreteDynamicsWorld1.stepSimulation(1.0f / TPS);
                        Timer.syncDown();
                    }
                }
                BulletWorldTimer.TPS += 1;
                Thread.sleep(Math.max(1L, PhysicThreadManager.getTicksForUpdate(TPS) - l));
            }
            Game.getGame().getProfiler().endSection(SectionManager.bulletPhysWorld);
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        } finally {
            this.cleanResources();
        }
    }

    public void cleanResources() {
        Game.getGame().getLogManager().log("Cleaning physics world resources...");
        this.getDiscreteDynamicsWorld().deallocate();
    }

    public synchronized final btDynamicsWorld dynamicsWorld() {
        return this.getDiscreteDynamicsWorld();
    }

    public synchronized final btCollisionWorld collisionWorld() {
        return this.getDiscreteDynamicsWorld().getCollisionWorld();
    }

    public void addRigidBodyInWorld(@NotNull btRigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
        }
    }

    public void addCollisionObjectInWorld(@NotNull btCollisionObject collisionObject) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
        }
    }

    public void removeRigidBodyFromWorld(@NotNull btRigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
        }
    }

    public void removeCollisionObjectFromWorld(@NotNull btCollisionObject collisionObject) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
        }
    }

    public void updateRigidBodyAabb(@NotNull btRigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().updateSingleAabb(rigidBody);
        }
    }

    private synchronized btDiscreteDynamicsWorld getDiscreteDynamicsWorld() {
        return this.discreteDynamicsWorld;
    }

    public btConstraintSolver getConstraintSolver() {
        return this.constraintSolve;
    }

    public btBroadphaseInterface getBroadcaster() {
        return this.broadcaster;
    }

    public btCollisionConfiguration getCollisionConfiguration() {
        return this.collisionConfiguration;
    }

    public btCollisionDispatcher getCollisionDispatcher() {
        return this.collisionDispatcher;
    }
}
