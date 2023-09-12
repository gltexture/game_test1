package ru.BouH.engine.physics.entities.player;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.g_static.binding.BindingList;
import ru.BouH.engine.game.g_static.render.ItemRenderList;
import ru.BouH.engine.math.BPVector3f;
import ru.BouH.engine.physics.collision.objects.AbstractCollision;
import ru.BouH.engine.physics.collision.objects.OBB;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.PointLight;

public class EntityPlayerSP extends PhysEntity implements IRemoteController {
    private final Vector3d cameraRotation;
    private final double eyeHeight;
    private IController controller;
    private Vector3d inputMotion;

    public EntityPlayerSP(World world, Vector3d pos, Vector3d rot, String name) {
        super(world, pos, rot, name);
        this.inputMotion = new Vector3d(0.0d);
        this.cameraRotation = new Vector3d();
        this.eyeHeight = 0.425d;
        this.setSpeed(3.0f);
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
        return new OBB(this.getScale(), new Vector3d(0.75d, 1.5d, 0.75d), 5.0f, this.defaultMotionState, new BPVector3f(0.0f, 0.0f, 0.0f));
    }

    public void onJBUpdate() {
        super.onJBUpdate();
        if (this.isValidController()) {
            Vector3d vector3d = this.calcControllerMotion();
            if (vector3d.y > 0) {
                this.jump();
            }
        }
    }

    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.isValidController()) {
            Vector3d vector3d = this.calcControllerMotion();
            this.setVelocityVector(this.getMotionVector(vector3d).mul(1.25d));
        }
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
            entityPropInfo.setScale(0.5d);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ItemRenderList.entityCube);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(30.0f));
        }
        if (BindingList.instance.keyBlock2.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysLightCube(this.getWorld(), new Vector3d(1.0d), this.getPosition().add(this.getLookVector().mul(2.0f)));
            entityPropInfo.setScale(0.25d);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ItemRenderList.entityLamp);
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
