package ru.BouH.engine.render.scene.world.camera;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

public class AttachedCamera extends Camera {
    private PhysicsObject physicsObject;

    public AttachedCamera(@NotNull PhysicsObject physicsObject) {
        this.physicsObject = physicsObject;
    }

    public Vector3d getCamPosition() {
        return super.getCamPosition();
    }

    @Override
    public void updateCamera(double partialTicks) {
        PhysicsObject physicsObject = this.getPhysXObject();
        if (physicsObject != null) {
            Vector3d pos = new Vector3d(this.getPhysXObject().getRenderPosition()).add(this.cameraOffset());
            Vector3d rot = new Vector3d(this.getPhysXObject().getRenderRotation());
            this.setCameraPos(pos);
            this.setCameraRot(rot);
        }
    }

    private Vector3d cameraOffset() {
        Vector3d vector3d = new Vector3d(0.0d);
        if (this.getPhysXObject() != null && this.getPhysXObject().getWorldItem() instanceof EntityPlayerSP) {
            EntityPlayerSP entityPlayerSP = (EntityPlayerSP) this.getPhysXObject().getWorldItem();
            vector3d.add(0, entityPlayerSP.getEyeHeight(), 0);
        }
        return vector3d;
    }

    public void attachCameraToItem(PhysicsObject physicsObject) {
        Game.getGame().getLogManager().log("Attached camera to: " + physicsObject.getWorldItem().getItemName());
        this.physicsObject = physicsObject;
    }

    public PhysicsObject getPhysXObject() {
        return this.physicsObject;
    }
}
