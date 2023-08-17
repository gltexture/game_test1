package ru.BouH.engine.physx.world;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physx.PhysX;

import javax.vecmath.Vector3f;

public class BulletManager {
    public static final Object lock = new Object();
    private final DbvtBroadphase broadcaster;
    private final CollisionConfiguration collisionConfiguration;
    private final CollisionDispatcher collisionDispatcher;
    private final DiscreteDynamicsWorld discreteDynamicsWorld;
    private final ConstraintSolver constraintSolve;
    private Thread bulletThread;

    public BulletManager() {
        this.broadcaster = new DbvtBroadphase();
        this.collisionConfiguration = new DefaultCollisionConfiguration();
        this.collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        this.constraintSolve = new SequentialImpulseConstraintSolver();
        this.discreteDynamicsWorld = new DiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new Vector3f(0, -9.8f, 0));
    }

    public void startBulletThread() {
        final DiscreteDynamicsWorld discreteDynamicsWorld1 = this.getDiscreteDynamicsWorld();
        if (discreteDynamicsWorld1 == null) {
            throw new GameException("Current Dynamics World is NULL!");
        }
        this.bulletThread = new Thread(() -> {
            try {
                long i = System.currentTimeMillis();
                long l = 0L;
                Game.getGame().getProfiler().startSection(SectionManager.bulletPhysWorld);
                while (!Game.getGame().shouldBeClosed) {
                    long j = System.currentTimeMillis();
                    long k = j - i;
                    l += k;
                    i = j;
                    while (l > PhysX.getTicksForUpdate()) {
                        l -= PhysX.getTicksForUpdate();
                        synchronized (BulletManager.lock) {
                            discreteDynamicsWorld1.stepSimulation(1f / PhysX.TICKS_PER_SECOND, 10);
                        }
                    }
                    Thread.sleep(Math.max(1L, PhysX.getTicksForUpdate() - l));
                }
                Game.getGame().getProfiler().endSection(SectionManager.bulletPhysWorld);
            } catch (InterruptedException | GameException e) {
                throw new RuntimeException(e);
            }
        });
        this.startThread();
    }

    public synchronized final CollisionWorld collisionWorld() {
        return this.getDiscreteDynamicsWorld().getCollisionWorld();
    }

    protected void startThread() {
        this.bulletThread.setName("bullet-phys");
        this.bulletThread.start();
    }

    public void addRigidBodyInWorld(RigidBody rigidBody) {
        synchronized (BulletManager.lock) {
            this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
        }
    }

    public void addCollisionObjectInWorld(CollisionObject collisionObject) {
        synchronized (BulletManager.lock) {
            this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
        }
    }

    public void removeRigidBodyFromWorld(RigidBody rigidBody) {
        synchronized (BulletManager.lock) {
            this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
        }
    }

    public void removeCollisionObjectFromWorld(CollisionObject collisionObject) {
        synchronized (BulletManager.lock) {
            this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
        }
    }

    public void updateRigidBodyAabb(RigidBody rigidBody) {
        synchronized (BulletManager.lock) {
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
