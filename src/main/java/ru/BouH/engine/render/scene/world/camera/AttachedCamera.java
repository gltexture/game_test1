package ru.BouH.engine.render.scene.world.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.scene_render.WorldRender;

public class AttachedCamera extends Camera {
    private WorldItem worldItem;

    public AttachedCamera(@NotNull WorldItem worldItem, IController controller) {
        super(controller, worldItem.getPosition(), worldItem.getRotation());
        Game.getGame().getLogManager().log("Created attached camera to: " + worldItem.getItemName());
        this.worldItem = worldItem;
    }

    @Override
    public void updateCameraPosition(double partialTicks) {
        WorldItem worldItem1 = this.getWorldItem();
        if (worldItem1 != null) {
            final Vector3d pos = new Vector3d(WorldRender.physXObject.getRenderPosition()).add(this.cameraOffset());
            this.setCameraPos(pos);
        }
    }

    @Override
    public void updateCameraRotation(double partialTicks) {
        WorldItem worldItem1 = this.getWorldItem();
        if (worldItem1 != null) {
            final Vector3d rot = new Vector3d(worldItem1.getRotation());
            this.setCameraRot(worldItem1 instanceof IRemoteController ? rot : this.getCamRotation().lerp(rot, partialTicks));
        }
    }

    private void moveCamera(Vector3d direction) {
        double moveX = 0.0d;
        double moveY = 0.0d;
        double moveZ = 0.0d;
        if (direction.z != 0) {
            moveX += Math.sin(Math.toRadians(this.getCamRotation().y)) * -1.0f * direction.z * FreeCamera.CAM_SPEED;
            moveZ += Math.cos(Math.toRadians(this.getCamRotation().y)) * direction.z * FreeCamera.CAM_SPEED;
        }
        if (direction.x != 0) {
            moveX += Math.sin(Math.toRadians(this.getCamRotation().y - 90)) * -1.0f * direction.x * FreeCamera.CAM_SPEED;
            moveZ += Math.cos(Math.toRadians(this.getCamRotation().y - 90)) * direction.x * FreeCamera.CAM_SPEED;
        }
        if (direction.y != 0) {
            moveY += direction.y * FreeCamera.CAM_SPEED * 0.675f;
        }
        this.addCameraPos(new Vector3d(moveX, moveY, moveZ));
    }

    public Vector3d cameraOffset() {
        Vector3d vector3d = new Vector3d(0.0d);
        if (this.getWorldItem() != null && this.getWorldItem() instanceof EntityPlayerSP) {
            EntityPlayerSP entityPlayerSP = (EntityPlayerSP) this.getWorldItem();
            vector3d.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3d;
    }

    public void attachCameraToItem(WorldItem worldItem) {
        Game.getGame().getLogManager().log("Attached camera to: " + worldItem.getItemName());
        this.setCameraPos(worldItem.getPosition());
        this.setCameraRot(worldItem.getRotation());
        this.worldItem = worldItem;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }
}
