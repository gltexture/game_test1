package ru.BouH.engine.proxy.lights.env;

import org.joml.*;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.render.scene.world.RenderWorld;

public class DirectionalLight extends Light {
    private Vector3d direction;

    public DirectionalLight(RenderWorld renderWorld) {
        super(renderWorld, LightType.DIRECTIONAL_LIGHT);
    }

    public Vector3d getDirection() {
        Vector3d dir = new Vector3d(this.direction);
        Vector4d aux = new Vector4d(dir, 0.0f);
        Matrix4d matrix4d = this.getRenderWorld().getRenderManager().getTransform().getViewMatrix(this.getRenderWorld().getCamera());
        aux.mul(matrix4d);
        return dir.set(new Vector3d(aux.x, aux.y, aux.z));
    }

    public void setDirection(Vector3d direction) {
        this.direction = direction;
    }
}
