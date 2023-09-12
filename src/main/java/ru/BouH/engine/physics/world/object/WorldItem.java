package ru.BouH.engine.physics.world.object;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.ILight;

public abstract class WorldItem implements IWorldObject {
    private static int globalId;
    protected final Vector3d position;
    protected final Vector3d rotation;
    private final World world;
    private final Vector3d prevPosition;
    private final String itemName;
    private final int itemId;
    private ILight iLight;
    private int spawnTick;
    private boolean isDead;
    private double scale;

    public WorldItem(World world, Vector3d pos, Vector3d rot, String itemName) {
        this.itemName = itemName;
        this.rotation = rot == null ? new Vector3d(0.0d) : rot;
        this.position = pos == null ? new Vector3d(0.0d) : pos;
        this.prevPosition = pos;
        this.scale = 1.0f;
        this.world = world;
        this.iLight = null;
        this.itemId = globalId++;
        this.isDead = false;
    }

    public WorldItem(World world, Vector3d pos, String itemName) {
        this(world, pos, new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, String itemName) {
        this(world, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
    }

    public void onSpawn(IWorld iWorld) {
        this.spawnTick = ((World) iWorld).getTicks();
        Game.getGame().getLogManager().log("Add entity in world - [ " + this + " ]");
    }

    public void onDestroy(IWorld iWorld) {
        Game.getGame().getLogManager().log("Removed entity from world - [ " + this + " ]");
    }

    public String toString() {
        return this.getItemName() + "(" + this.getItemId() + ")";
    }

    public Vector3d getPrevPosition() {
        return this.prevPosition;
    }

    public int getTicksExisted() {
        return this.getWorld().getTicks() - this.spawnTick;
    }

    public Vector3d getPosition() {
        return new Vector3d(this.position);
    }

    public void setPosition(Vector3d vector3d) {
        this.position.set(vector3d);
    }

    public Vector3d getRotation() {
        return new Vector3d(this.rotation);
    }

    public void setRotation(Vector3d vector3d) {
        this.rotation.set(vector3d);
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean canBeDestroyed() {
        return true;
    }


    public void setDead() {
        if (this.canBeDestroyed()) {
            this.isDead = true;
            this.getWorld().removeItem(this);
        }
    }

    public boolean isRemoteControlled() {
        return this instanceof IRemoteController && ((IRemoteController) this).isValidController();
    }

    public boolean hasLight() {
        return this.getLight() != null && this.getLight().isActive();
    }

    public ILight getLight() {
        return this.iLight;
    }

    public void setLight(ILight iLight) {
        this.iLight = iLight;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public World getWorld() {
        return this.world;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getItemName() {
        return this.itemName;
    }
}
