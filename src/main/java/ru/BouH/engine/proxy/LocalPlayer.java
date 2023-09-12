package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.g_static.render.ItemRenderList;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.World;

public class LocalPlayer {
    private final EntityPlayerSP entityPlayerSP;

    public LocalPlayer(World world, Vector3d pos) {
        this.entityPlayerSP = new EntityPlayerSP(world, pos, new Vector3d(0.0d));
    }

    public EntityPlayerSP getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public void addPlayerInWorlds(Proxy proxy) {
        proxy.addItemInWorlds(this.getEntityPlayerSP(), ItemRenderList.player);
    }
}
