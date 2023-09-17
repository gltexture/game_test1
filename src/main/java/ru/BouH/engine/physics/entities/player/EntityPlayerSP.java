package ru.BouH.engine.physics.entities.player;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btConvexShape;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.g_static.binding.BindingList;
import ru.BouH.engine.game.g_static.render.RenderResources;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.collision.objects.OBB;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.PhysDynamicEntity;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.PointLight;

public class EntityPlayerSP extends PhysDynamicEntity implements IRemoteController {
    private final Vector3d cameraRotation;
    private final double eyeHeight;

    private IController controller;
    private Vector3d inputMotion;
    private boolean isOnGround;
    private int ticksBeforeCanJump;
    private double speed;

    public EntityPlayerSP(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.inputMotion = new Vector3d(0.0d);
        this.cameraRotation = new Vector3d();
        this.eyeHeight = 0.425d;
        this.setSpeed(1.0f);
        this.getPhysicsProperties().setWeight(3.0d);
        this.getPhysicsProperties().setFriction(3.0d);
    }

    public EntityPlayerSP(World world, Vector3d pos, String name) {
        this(world, pos, new Vector3d(0.0d), name);
    }

    public EntityPlayerSP(World world, Vector3d pos) {
        this(world, pos, new Vector3d(0.0d), "phys_playerSP");
    }

    public EntityPlayerSP(World world, Vector3d pos, Vector3d rot) {
        this(world, pos, rot, "phys_playerSP");
    }

    @Override
    protected AbstractCollision constructCollision(double scale) {
        return new OBB(this.getScale(), new Vector3d(0.75d, 1.5d, 0.75d), 5.0f, this.defaultMotionState, new btVector3(0.0f, 0.0f, 0.0f));
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.getRigidBody() != null) {
            if (this.isValidController()) {
                if (this.isOnGround()) {
                    if (this.ticksBeforeCanJump-- > 0) {
                        this.ticksBeforeCanJump -= 1;
                    } else {
                        this.canJump = true;
                    }
                } else {
                    if (this.canJump) {
                        this.ticksBeforeCanJump = 50;
                    }
                    this.canJump = false;
                }
                this.getRigidBody().activate();
            }
        }
    }

    public void onJBUpdate() {
        super.onJBUpdate();
        if (this.getRigidBody() != null) {
            this.groundCheck();
            if (this.isValidController()) {
                this.speed = 3.0d;
                this.makeStep(this.getStepVelocityVector(this.calcControllerMotion()), this.getPhysicsProperties().getFriction());
                Vector3d vector3d = this.calcControllerMotion();
                if (vector3d.y > 0) {
                    this.jump();
                }
            }
        }
    }

    private void makeStep(Vector3d vector3d, double thurst) {
        this.setVelocityVector(vector3d);
    }

    private Vector3d getStepVelocityVector(Vector3d motion) {
        double speed = this.getObjectSpeed();
        Vector3d v1 = this.getMotionVector(motion).mul(this.getSpeed());
        if (speed > 20) {
            v1.div(speed);
        }
        if (!this.isOnGround()) {
            v1.mul(0.1d);
        }
        return v1;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    protected void groundCheck() {
        if (!this.getRigidBody().isInWorld() || this.getRigidBody() == null) {
            this.isOnGround = false;
            return;
        }

        btVector3 v1 = new btVector3();
        btVector3 v2 = new btVector3();
        this.getRigidBody().getAabb(v1, v2);

        btTransform transform_m = this.getRigidBody().getWorldTransform();
        btTransform transform1 = new btTransform(transform_m);
        btTransform transform2 = new btTransform(transform_m);
        final double f1 = Math.min(v2.getY() - v1.getY(), 0.03f);

        btVector3 v3 = new btVector3(Math.abs(v2.getX() - v1.getX()) * 0.5f - 0.01f, f1, Math.abs(v2.getZ() - v1.getZ()) * 0.5f - 0.01f);

        transform1.setOrigin(new btVector3(this.getPosition().x, v1.getY() + f1 * 2, this.getPosition().z));
        transform2.setOrigin(new btVector3(this.getPosition().x, v1.getY(), this.getPosition().z));

        btConvexShape convexShape = new btBoxShape(v3);
        btCollisionWorld.ConvexResultCallback closestConvexResultCallback = new btCollisionWorld.ClosestConvexResultCallback(transform1.getOrigin(), transform2.getOrigin());
        this.getWorld().getBulletTimer().dynamicsWorld().convexSweepTest(convexShape, transform1, transform2, closestConvexResultCallback);

        v1.deallocate();
        v2.deallocate();
        v3.deallocate();
        convexShape.deallocate();
        transform1.deallocate();
        transform2.deallocate();

        this.isOnGround = closestConvexResultCallback.hasHit();
        closestConvexResultCallback.deallocate();
    }

    public void jump() {
        if (this.canJump) {
            this.addObjectVelocity(new Vector3d(0.0d, 5.0d, 0.0d));
            this.ticksBeforeCanJump = 20;
            this.canJump = false;
        }
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    protected Vector3d getMotionVector(Vector3d vector3d) {
        Vector3d vector3d1 = vector3d.mul(new Vector3d(1, 0, 1));
        if (vector3d1.length() > 0) {
            vector3d1.normalize();
        }
        return vector3d1;
    }

    private Vector3d calcControllerMotion() {
        double[] motion = new double[3];
        double[] input = new double[3];
        input[0] = this.inputMotion.x;
        input[1] = this.inputMotion.y;
        input[2] = this.inputMotion.z;
        if (input[2] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getRotation().y)) * -1.0f * input[2];
            motion[2] += Math.cos(Math.toRadians(this.getRotation().y)) * input[2];
        }
        if (input[0] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getRotation().y - 90)) * -1.0f * input[0];
            motion[2] += Math.cos(Math.toRadians(this.getRotation().y - 90)) * input[0];
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        return new Vector3d(motion[0], motion[1], motion[2]);
    }

    public Vector3d getCameraRotation() {
        return this.cameraRotation;
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public double getEyeHeight() {
        return this.eyeHeight;
    }

    public void setController(IController iController) {
        this.controller = iController;
    }

    public Vector3d getRotation() {
        return this.isValidController() ? this.getCameraRotation() : super.getRotation();
    }

    @Override
    public IController currentController() {
        return this.controller;
    }

    @Override
    public void performController(Vector2d rotationInput, Vector3d xyzInput) {
        if (BindingList.instance.keyBlock1.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysEntityCube(this.getWorld(), new Vector3d(1.0d), this.getPosition().add(this.getLookVector().mul(2.0f)));
            entityPropInfo.setScale(1.5d);
            entityPropInfo.setMaterial(Materials.brickCube);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, RenderResources.entityCube);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(30.0f));
        }
        if (BindingList.instance.keyBlock2.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysLightCube(this.getWorld(), new Vector3d(1.0d), this.getPosition().add(this.getLookVector().mul(2.0f)));
            entityPropInfo.setScale(0.25d);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, RenderResources.entityLamp);
            int a1 = Game.random.nextInt(3);
            entityPropInfo.setLight(new PointLight(new Vector3d(a1 == 0 ? 1.0d : Game.random.nextFloat(), a1 == 1 ? 1.0d : Game.random.nextFloat() * 0.5f, a1 == 2 ? 1.0d : Game.random.nextFloat() * 0.5f), 6.5d));
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(20.0f));
        }
        if (BindingList.instance.keyClear.isClicked()) {
            this.getWorld().clearAllItems();
        }
        this.getCameraRotation().add(new Vector3d(rotationInput, 0.0d));
        this.inputMotion = new Vector3d(xyzInput);
        this.clampCameraRotation();
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > 90) {
            this.getCameraRotation().set(new Vector3d(90, this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -90) {
            this.getCameraRotation().set(new Vector3d(-90, this.getRotation().y, this.getRotation().z));
        }
    }
}
