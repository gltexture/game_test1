package ru.BouH.engine.physx.entities.living.player;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.game.init.controller.Controller;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysMoving;
import ru.BouH.engine.physx.entities.prop.PhysEntityLamp;
import ru.BouH.engine.physx.entities.prop.PhysEntityProp;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.proxy.init.EntitiesInit;
import ru.BouH.engine.proxy.init.KeysInit;
import ru.BouH.engine.render.RenderManager;

public class EntityPlayerSP extends PhysMoving {
    private final double eyeHeight;
    public PhysEntityProp entityPropInfo;
    public PhysEntityProp entityPropInfo2;
    private boolean flying;

    public EntityPlayerSP(World world) {
        super(world, "local_player");
        this.setCollisionBox3D(new CollisionBox3D(this, 0.25f, 1.25f));
        this.eyeHeight = 0.82d;
        this.flying = false;
    }

    @Override
    public void onSpawn() {
        entityPropInfo = new PhysEntityProp(this.getWorld());
        entityPropInfo.getPosition().set(-1, -1, -1);
        Game.getGame().getProxy().addEntityInWorlds(entityPropInfo, EntitiesInit.entityCube);

        entityPropInfo2 = new PhysEntityProp(this.getWorld());
        entityPropInfo.getPosition().set(3, 3, 3);
        Game.getGame().getProxy().addEntityInWorlds(entityPropInfo2, EntitiesInit.entityLamp);
    }

    public void updateEntity() {
        super.updateEntity();
        this.getCollisionBox3D().setBox(this, 0.25f, 1.25f);
    }

    protected void detectCollisions() {
        if (!flying) {
            super.detectCollisions();
        }
    }

    public void performController(Controller controller) {
        this.movePlayerPost(controller.getCamInput().x * RenderManager.CAM_SPEED, controller.getCamInput().y * RenderManager.CAM_SPEED, controller.getCamInput().z * RenderManager.CAM_SPEED);
        if (controller.isLeftButtonPressed() || controller.isRightButtonPressed()) {
            Vector2d rotationVec = controller.getDisplayVec();
            this.getRotation().add(rotationVec.x * RenderManager.CAM_SENS, rotationVec.y * RenderManager.CAM_SENS, 0);
            if (this.getRotation().x > 90) {
                this.getRotation().set(90, this.getRotation().y, this.getRotation().z);
            }
            if (this.getRotation().x < -90) {
                this.getRotation().set(-90, this.getRotation().y, this.getRotation().z);
            }
        }
        if (KeysInit.keyPlaceBlock2.isClicked()) {
            PhysEntityProp entityPropInfo = new PhysEntityProp(this.getWorld());
            entityPropInfo.getPosition().set(this.getPosition());
            entityPropInfo.setScale(5.0f);
            Game.getGame().getProxy().addEntityInWorlds(entityPropInfo, EntitiesInit.entityCube3);
        }
        if (KeysInit.keyPlaceBlock.isClicked()) {
            PhysEntityProp entityPropInfo = new PhysEntityProp(this.getWorld());
            entityPropInfo.getPosition().set(this.getPosition());
            entityPropInfo.setScale(10.0f);
            Game.getGame().getProxy().addEntityInWorlds(entityPropInfo, EntitiesInit.entityCube2);
        }
        if (KeysInit.keyPlaceLamp.isClicked()) {
            PhysEntityLamp entityPropInfo = new PhysEntityLamp(this.getWorld());
            entityPropInfo.getPosition().set(new Vector3d(0.0d));
            Game.getGame().getProxy().addEntityInWorlds(entityPropInfo, EntitiesInit.entityLamp);
        }
        if (KeysInit.keyFly.isClicked()) {
            Game.getGame().getScreen().getScene().getEntityRender().unBindProgram();
            Game.getGame().getScreen().getScene().getEntityRender().onStartRender();
            Game.getGame().getScreen().getScene().getEntityRender().bindProgram();
            //this.flying = !this.flying;
        }
    }

    protected void movingStop() {
        super.movingStop();
    }

    public void movePlayerPost(double x, double y, double z) {
        double moveX = 0.0d;
        double moveY = 0.0d;
        double moveZ = 0.0d;
        double speed = 2.0f;
        if (z != 0) {
            moveX += Math.sin(Math.toRadians(this.getRotation().y)) * -1.0f * z * speed;
            moveZ += Math.cos(Math.toRadians(this.getRotation().y)) * z * speed;
        }
        if (x != 0) {
            moveX += Math.sin(Math.toRadians(this.getRotation().y - 90)) * -1.0f * x * speed;
            moveZ += Math.cos(Math.toRadians(this.getRotation().y - 90)) * x * speed;
        }
        if (y != 0) {
            moveY += y * speed;
        }
        this.motionX = moveX;
        this.motionY = moveY;
        this.motionZ = moveZ;
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public double getEyeHeight() {
        return this.getPosition().y + this.eyeHeight;
    }
}
