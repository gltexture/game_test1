package ru.BouH.engine.physics.world.timer;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.collision.JBulletPhysics;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.timer.Timer;

import javax.vecmath.Vector3f;

public class BulletWorldTimer implements IPhysTimer {
    public static final Object lock = new Object();
    public static int TPS;
    private final World world;
    private final DbvtBroadphase broadcaster;
    private final CollisionConfiguration collisionConfiguration;
    private final CollisionDispatcher collisionDispatcher;
    private final DiscreteDynamicsWorld discreteDynamicsWorld;
    private final ConstraintSolver constraintSolve;

    public BulletWorldTimer(World world) {
        this.world = world;
        this.broadcaster = new DbvtBroadphase();
        this.collisionConfiguration = new DefaultCollisionConfiguration();
        this.collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        this.constraintSolve = new SequentialImpulseConstraintSolver();
        this.discreteDynamicsWorld = new DiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new Vector3f(0, -10.0f, 0));
    }

    @SuppressWarnings("all")
    public void updateTimer(int TPS) {
        final DiscreteDynamicsWorld discreteDynamicsWorld1 = this.getDiscreteDynamicsWorld();
        if (discreteDynamicsWorld1 == null) {
            throw new GameException("Current Dynamics World is NULL!");
        }
        try {
            long i = System.currentTimeMillis();
            long l = 0L;
            Game.getGame().getProfiler().startSection(SectionManager.bulletPhysWorld);
            while (!Game.getGame().isShouldBeClosed()) {
                synchronized (PhysicThreadManager.locker) {
                    PhysicThreadManager.locker.wait();
                }
                long j = System.currentTimeMillis();
                long k = j - i;
                l += k;
                i = j;
                while (l > PhysicThreadManager.getTicksForUpdate(TPS)) {
                    l -= PhysicThreadManager.getTicksForUpdate(TPS);
                    synchronized (BulletWorldTimer.lock) {
                        for (JBulletPhysics worldItem : this.world.getAllJBItems()) {
                            worldItem.onJBUpdate();
                        }
                        Timer.syncUp();
                        discreteDynamicsWorld1.stepSimulation(1f / TPS, 20);
                        Timer.syncDown();
                    }
                    BulletWorldTimer.TPS += 1;
                }
                Thread.sleep(Math.max(1L, PhysicThreadManager.getTicksForUpdate(TPS) - l));
            }
            Game.getGame().getProfiler().endSection(SectionManager.bulletPhysWorld);
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        }
        this.cleanResources();
    }

    public void cleanResources() {
        Game.getGame().getLogManager().log("Cleaning physics world resources...");
        this.getDiscreteDynamicsWorld().destroy();
    }

    public synchronized final DynamicsWorld dynamicsWorld() {
        return this.getDiscreteDynamicsWorld();
    }

    public synchronized final CollisionWorld collisionWorld() {
        return this.getDiscreteDynamicsWorld().getCollisionWorld();
    }

    public void addRigidBodyInWorld(@NotNull RigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
        }
    }

    public void addCollisionObjectInWorld(@NotNull CollisionObject collisionObject) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
        }
    }

    public void removeRigidBodyFromWorld(@NotNull RigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
        }
    }

    public void removeCollisionObjectFromWorld(@NotNull CollisionObject collisionObject) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
        }
    }

    public void updateRigidBodyAabb(@NotNull RigidBody rigidBody) {
        synchronized (BulletWorldTimer.lock) {
            this.getDiscreteDynamicsWorld().updateSingleAabb(rigidBody);
        }
    }

    private synchronized DiscreteDynamicsWorld getDiscreteDynamicsWorld() {
        return this.discreteDynamicsWorld;
    }

    public ConstraintSolver getConstraintSolver() {
        return this.constraintSolve;
    }

    public DbvtBroadphase getBroadcaster() {
        return this.broadcaster;
    }

    public CollisionConfiguration getCollisionConfiguration() {
        return this.collisionConfiguration;
    }

    public CollisionDispatcher getCollisionDispatcher() {
        return this.collisionDispatcher;
    }
}
