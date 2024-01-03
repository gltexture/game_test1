package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;

public class LocalPlayer {
    private final EntityPlayerSP entityPlayerSP;
    public static boolean VALID_PL = false;

    public LocalPlayer(World world, Vector3d pos) {
        this.entityPlayerSP = new EntityPlayerSP(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 10.0d), pos, new Vector3d(0.0d));
    }

    public EntityPlayerSP getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public void addPlayerInWorlds(Proxy proxy) {
        proxy.addItemInWorlds(this.getEntityPlayerSP(), ResourceManager.renderAssets.player);
        LocalPlayer.VALID_PL = true;
    }
}
