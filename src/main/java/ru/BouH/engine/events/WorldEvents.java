package ru.BouH.engine.events;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.g_static.render.ItemRenderList;
import ru.BouH.engine.physx.brush.Plane4dBrush;
import ru.BouH.engine.physx.entities.prop.PhysEntityCube;
import ru.BouH.engine.physx.world.World;

public class WorldEvents {
    public static void addEntities(World world) {
        PhysEntityCube entityPropInfo = new PhysEntityCube(world, new Vector3d(1, 1, 1), new Vector3d(0.0d, 15.0d, 10.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ItemRenderList.entityCube);

        PhysEntityCube entityPropInfo2 = new PhysEntityCube(world, new Vector3d(1, 1, 1), new Vector3d(100.0d, 60.0d, 100.0d));
        entityPropInfo2.setScale(100);
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo2, ItemRenderList.entityCube);
    }

    public static void addBrushes(World world) {
        Plane4dBrush plane4dBrush = new Plane4dBrush(world, new Vector3d[]{new Vector3d(-150, 0, -150), new Vector3d(1350, 0, -150), new Vector3d(-150, 0, 1350), new Vector3d(1350, 0, 1350)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush, ItemRenderList.plane);

        Plane4dBrush plane4dBrush2 = new Plane4dBrush(world, new Vector3d[]{new Vector3d(-150, 0, -150), new Vector3d(-150, 0, 150), new Vector3d(-150, 20, -150), new Vector3d(-150, 20, 150)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush2, ItemRenderList.plane);

        Plane4dBrush plane4dBrush3 = new Plane4dBrush(world, new Vector3d[]{new Vector3d(-150, 0, -150), new Vector3d(150, 0, -150), new Vector3d(-150, 20, -150), new Vector3d(150, 20, -150)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush3, ItemRenderList.plane);
    }
}