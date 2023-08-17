package ru.BouH.engine.render.scene.world.camera;

import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.physx.entities.IRemoteController;
import ru.BouH.engine.physx.world.object.WorldItem;

public class AttachedCamera extends Camera {
    private WorldItem worldItem;
    private boolean interpolatePos;
    private boolean interpolateRot;

    public AttachedCamera(@NotNull WorldItem worldItem, boolean interpolatePos, boolean interpolateRot) {
        super(worldItem.getPosition(), worldItem.getRotation());
        this.attachCameraToItem(worldItem);
        this.setInterpolationParams(interpolatePos, interpolateRot);
    }

    public AttachedCamera(@NotNull WorldItem worldItem) {
        this(worldItem, true, true);
    }

    @Override
    public void updateCamera(double partialTicks) {
        this.lerpCamera(partialTicks);
    }

    private void lerpCamera(double partialTicks) {
        WorldItem worldItem1 = this.getWorldItem();
        if (worldItem1 != null) {
            boolean flag1 = this.interpolatePos;
            boolean flag2 = !worldItem1.isRemoteControlled() && this.interpolateRot;
            Vector3d pos = worldItem1.getPosition();
            Vector3d rot = worldItem1.getRotation();
            this.setCameraPos(flag1 ? this.getCamPosition().lerp(pos, partialTicks) : pos);
            this.setCameraRot(flag2 ? this.getCamRotation().lerp(rot, partialTicks) : rot);
        }
    }

    public void setInterpolationParams(boolean interpolatePos, boolean interpolateRot) {
        this.interpolatePos = interpolatePos;
        this.interpolateRot = interpolateRot;
    }

    public void attachCameraToItem(WorldItem worldItem) {
        Game.getGame().getLogManager().log("Attached camera to: " + worldItem.getItemName());
        this.worldItem = worldItem;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }
}
