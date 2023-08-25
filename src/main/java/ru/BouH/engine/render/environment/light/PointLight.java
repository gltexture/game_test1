package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import ru.BouH.engine.physx.world.object.IDynamic;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;

public class PointLight implements ILight, IDynamic {
    private WorldItem worldItem;
    private Vector3d offset;
    private Vector3d lightColor;
    private Vector3d lightPos;
    private double brightness;

    public PointLight() {
        this(new Vector3d(0.0d), new Vector3d(0.0d), 0.0d);
    }

    public PointLight(Vector3d lightColor, Vector3d lightPos, double brightness) {
        this.lightColor = lightColor;
        this.lightPos = lightPos;
        this.brightness = brightness;
        this.offset = new Vector3d(0.0d);
        this.worldItem = null;
    }

    public PointLight(Vector3d lightColor, WorldItem worldItem, double brightness) {
        this(lightColor, worldItem.getPosition(), brightness);
        this.worldItem = worldItem;
    }

    public PointLight(Vector3d lightColor, double brightness) {
        this(lightColor, new Vector3d(0.0d), brightness);
    }

    public void deactivate() {
        this.brightness = 0.0d;
        this.setLightColor(new Vector3d(0.0d));
        this.setLightPos(new Vector3d(0.0d));
    }

    public boolean isActive() {
        return this.getBrightness() > 0;
    }

    public void setOffset(Vector3d offset) {
        this.offset = offset;
    }

    @Override
    public LightType lightType() {
        return LightType.POINT_LIGHT;
    }

    public void setLightColor(Vector3d lightColor) {
        this.lightColor = lightColor;
    }

    public void setLightPos(Vector3d lightPos) {
        this.lightPos = lightPos;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public Vector3d getOffset() {
        return new Vector3d(this.offset);
    }

    public Vector3d getLightColor() {
        return new Vector3d(this.lightColor);
    }

    public Vector3d getNormalizedPointLightPos(Matrix4d viewMatrix) {
        Vector3d pos = this.getLightPos();
        Vector4d aux = new Vector4d(pos, 1.0d);
        aux.mul(viewMatrix);
        pos.set(new Vector3d(aux.x, aux.y, aux.z));
        return pos;
    }

    public Vector3d getLightPos() {
        return new Vector3d(this.lightPos).add(this.getOffset());
    }

    public double getBrightness() {
        return this.brightness;
    }

    public void doAttachTo(WorldItem worldItem) {
        this.worldItem = worldItem;
    }

    @Override
    public WorldItem attachedTo() {
        return this.worldItem;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isAttached()) {
            if (this.attachedTo().isDead()) {
                this.deactivate();
            }
            this.setLightPos(this.attachedTo().getPosition());
        }
    }
}
