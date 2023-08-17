package ru.BouH.engine.events;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physx.brush.Plane4dBrush;
import ru.BouH.engine.physx.entities.prop.PhysEntityCube;
import ru.BouH.engine.physx.world.World;
import ru.BouH.engine.game.g_static.render.ItemRenderList;

public class WorldEvents {
    public static void addEntities(World world) {
        PhysEntityCube entityPropInfo = new PhysEntityCube(world, new Vector3d(1, 1, 1), new Vector3d(1.0d, 5.0d, 1.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ItemRenderList.entityCube);
    }

    public static void addBrushes(World world) {
        Plane4dBrush plane4dBrush = new Plane4dBrush(world, new Vector3d[]{new Vector3d(-150, 0, -150), new Vector3d(150, 0, -150), new Vector3d(-150, 0, 150), new Vector3d(150, 0, 150)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush, ItemRenderList.plane);
    }
}