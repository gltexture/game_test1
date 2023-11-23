package ru.BouH.engine.physics.world.object;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.math.MathHelper;
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

    public WorldItem(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        this.itemName = itemName;
        this.rotation = new Vector3d(rot);
        this.position = new Vector3d(pos);
        this.prevPosition = new Vector3d(this.position);
        this.scale = scale;
        this.world = world;
        this.iLight = null;
        this.isDead = false;
        this.itemId = WorldItem.globalId++;
    }

    public WorldItem(World world, double scale, Vector3d pos, String itemName) {
        this(world, scale, pos, new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, double scale, String itemName) {
        this(world, scale, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        this(world, 1.0d, pos, rot, itemName);
    }

    public WorldItem(World world, Vector3d pos, String itemName) {
        this(world, 1.0d, pos, new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, String itemName) {
        this(world, 1.0d, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
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

    public void setPrevPosition(Vector3d vector3d) {
        this.prevPosition.set(vector3d);
    }

    public Vector3d getPrevPosition() {
        return new Vector3d(this.prevPosition);
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

    public Vector3d getLookVector() {
        double x = Math.toRadians(this.getRotation().x);
        double y = Math.toRadians(this.getRotation().y);
        double lX = MathHelper.sin(y) * MathHelper.cos(x);
        double lY = -MathHelper.sin(x);
        double lZ = -MathHelper.cos(y) * MathHelper.cos(x);
        return new Vector3d(lX, lY, lZ);
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
