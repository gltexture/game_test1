package ru.BouH.engine.physics.world.timer;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.*;
import org.bytedeco.bullet.LinearMath.btIDebugDraw;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.g_static.profiler.SectionManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.debug.jbullet.JBDebugDraw;
import ru.BouH.engine.render.screen.timer.Timer;

import java.util.Iterator;
import java.util.List;

public class GameWorldTimer implements IPhysTimer {
    public static int TPS;
    private final World world;
    private final btBroadphaseInterface broadcaster;
    private final btCollisionConfiguration collisionConfiguration;
    private final btCollisionDispatcher collisionDispatcher;
    private final btDiscreteDynamicsWorld discreteDynamicsWorld;
    private final btConstraintSolver constraintSolve;
    private final JBDebugDraw jbDebugDraw;

    public GameWorldTimer() {
        this.world = new World();
        this.broadcaster = new btAxisSweep3(new btVector3(PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1(), PhysicThreadManager.WORLD_BORDERS.getA1()), new btVector3(PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2(), PhysicThreadManager.WORLD_BORDERS.getA2()));
        this.collisionConfiguration = new btDefaultCollisionConfiguration();
        this.collisionDispatcher = new btCollisionDispatcher(collisionConfiguration);
        this.collisionDispatcher.setDispatcherFlags(btCollisionDispatcher.CD_STATIC_STATIC_REPORTED | btCollisionDispatcher.CD_DISABLE_CONTACTPOOL_DYNAMIC_ALLOCATION | btCollisionDispatcher.CD_USE_RELATIVE_CONTACT_BREAKING_THRESHOLD);
        this.constraintSolve = new btConstraintSolverPoolMt(2);
        this.discreteDynamicsWorld = new btDiscreteDynamicsWorld(this.getCollisionDispatcher(), this.getBroadcaster(), this.getConstraintSolver(), this.getCollisionConfiguration());
        this.discreteDynamicsWorld.setGravity(new btVector3(0, -9.8f, 0));
        this.discreteDynamicsWorld.getDispatchInfo().m_deterministicOverlappingPairs(false);
        this.discreteDynamicsWorld.getDispatchInfo().m_useConvexConservativeDistanceUtil(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_useContinuous(true);
        this.discreteDynamicsWorld.getDispatchInfo().m_convexConservativeDistanceThreshold(0.01f);
        this.discreteDynamicsWorld.getDispatchInfo().m_allowedCcdPenetration(0.0d);
        this.discreteDynamicsWorld.performDiscreteCollisionDetection();

        this.jbDebugDraw = new JBDebugDraw();
        this.jbDebugDraw.setDebugMode(btIDebugDraw.DBG_DrawWireframe | btIDebugDraw.DBG_DrawAabb);
        this.discreteDynamicsWorld.setDebugDrawer(this.jbDebugDraw);
    }

    public void updateTimer(int TPS) {
        this.startTimer(TPS);
    }

    @SuppressWarnings("all")
    public void startTimer(int TPS) {
        try {
            Game.getGame().getLogManager().debug("Starting physics!");
            Game.getGame().getProfiler().startSection(SectionManager.physX);
            this.getWorld().onWorldStart();
            synchronized (Game.EngineSystem.logicLocker) {
                Game.EngineSystem.logicLocker.wait();
            }
            long i = System.currentTimeMillis();
            long l = 0L;
            final btDiscreteDynamicsWorld discreteDynamicsWorld1 = this.getDiscreteDynamicsWorld();
            final World world1 = this.getWorld();
            while (!Game.getGame().isShouldBeClosed()) {
                long j = System.currentTimeMillis();
                long k = j - i;
                l += k;
                i = j;
                while (l > PhysicThreadManager.getTicksForUpdate(TPS)) {
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.wait();
                    }
                    this.update1(world1);
                    this.update2(discreteDynamicsWorld1);
                    l -= PhysicThreadManager.getTicksForUpdate(TPS);
                    GameWorldTimer.TPS += 1;
                }
                Thread.sleep(Math.max(1L, PhysicThreadManager.getTicksForUpdate(TPS) - l));
            }
            this.getWorld().onWorldEnd();
            Game.getGame().getProfiler().endSection(SectionManager.physX);
            Game.getGame().getLogManager().debug("Stopping physics!");
        } catch (InterruptedException | GameException e) {
            throw new RuntimeException(e);
        } finally {
            this.cleanResources();
        }
    }

    private void update1(final World world1) throws InterruptedException {
        world1.onWorldUpdate();
    }

    private void update2(final btDiscreteDynamicsWorld discreteDynamicsWorld1) {
        final double step = PhysicThreadManager.getFrameTime();
        final int explicit = 16;
        Timer.syncUp();
        discreteDynamicsWorld1.stepSimulation(step, explicit, step / (double) explicit);
        Game.getGame().getScreen().getRenderWorld().test();
        Timer.syncDown();
    }

    public void cleanResources() {
        Game.getGame().getLogManager().log("Cleaning game world resources...");
        List<WorldItem> worldItems = this.getWorld().getAllWorldItems();
        Iterator<WorldItem> worldItemIterator = worldItems.iterator();
        while (worldItemIterator.hasNext()) {
            WorldItem worldItem = worldItemIterator.next();
            worldItem.onDestroy(this.getWorld());
            worldItemIterator.remove();
        }
        this.getDiscreteDynamicsWorld().deallocate();
    }

    public synchronized JBDebugDraw getJbDebugDraw() {
        return this.jbDebugDraw;
    }

    public synchronized final btCollisionWorld collisionWorld() {
        return this.getDiscreteDynamicsWorld().getCollisionWorld();
    }

    public synchronized void addRigidBodyInWorld(@NotNull btRigidBody rigidBody) {
        this.getDiscreteDynamicsWorld().addRigidBody(rigidBody);
    }

    public synchronized void addCollisionObjectInWorld(@NotNull btCollisionObject collisionObject) {
        this.getDiscreteDynamicsWorld().addCollisionObject(collisionObject);
    }

    public synchronized void removeRigidBodyFromWorld(@NotNull btRigidBody rigidBody) {
        this.getDiscreteDynamicsWorld().removeRigidBody(rigidBody);
    }

    public synchronized void removeCollisionObjectFromWorld(@NotNull btCollisionObject collisionObject) {
        this.getDiscreteDynamicsWorld().removeCollisionObject(collisionObject);
    }

    public synchronized void updateRigidBodyAabb(@NotNull btRigidBody rigidBody) {
        this.getDiscreteDynamicsWorld().updateSingleAabb(rigidBody);
    }

    public synchronized btDiscreteDynamicsWorld getDiscreteDynamicsWorld() {
        return this.discreteDynamicsWorld;
    }

    public synchronized btConstraintSolver getConstraintSolver() {
        return this.constraintSolve;
    }

    public synchronized btBroadphaseInterface getBroadcaster() {
        return this.broadcaster;
    }

    public synchronized btCollisionConfiguration getCollisionConfiguration() {
        return this.collisionConfiguration;
    }

    public synchronized btCollisionDispatcher getCollisionDispatcher() {
        return this.collisionDispatcher;
    }

    public World getWorld() {
        return this.world;
    }
}
