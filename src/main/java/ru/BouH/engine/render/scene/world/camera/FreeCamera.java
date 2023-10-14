package ru.BouH.engine.render.scene.world.camera;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.IController;

public class FreeCamera extends Camera {
    public static final double CAM_SPEED = 180.0d;

    public FreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        super(controller, pos, rot);
        Game.getGame().getLogManager().log("Created free camera at: " + pos);
    }

    public void setCameraPos(Vector3d vector3d) {
        super.setCameraPos(vector3d);
    }

    public void setCameraRot(Vector3d vector3d) {
        super.setCameraRot(vector3d);
    }

    @Override
    public void updateCameraPosition(double partialTicks) {
        if (this.getController() != null) {
            this.moveCamera(ControllerDispatcher.getOptionedXYZVec(this.getController()).mul(partialTicks));
        }
    }

    @Override
    public void updateCameraRotation(double partialTicks) {
        if (this.getController() != null) {
            this.move2dCameraRot(ControllerDispatcher.getOptionedDisplayVec(this.getController()));
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
}
