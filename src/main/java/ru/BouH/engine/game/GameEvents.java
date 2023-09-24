package ru.BouH.engine.game;

import org.joml.Vector3d;
import ru.BouH.engine.game.g_static.render.RenderResources;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;

public class GameEvents {

    public static void populate(World world) {
        GameEvents.addBrushes(world);
        GameEvents.addEntities(world);
    }

    public static void addEntities(World world) {
        PhysEntityCube entityPropInfo = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 20.0d), new Vector3d(1, 1, 1), 1.0d, new Vector3d(0.0d, 15.0d, 10.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, RenderResources.entityCube);

        PhysEntityCube entityPropInfo2 = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 100.0d), new Vector3d(1, 1, 1), 100.0d, new Vector3d(0.0d, 120.0d, 300.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo2, RenderResources.entityCube);
    }

    public static void addBrushes(World world) {
        final int size = 500;
        final int wallH = 100;

        Plane4dBrush plane4dBrush = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(-size, 0, size), new Vector3d(size, 0, -size), new Vector3d(size, 0, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush, RenderResources.planeBrick);

        Plane4dBrush plane4dBrush2 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size), new Vector3d(-size, 0, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush2, RenderResources.plane);

        Plane4dBrush plane4dBrush3 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush3, RenderResources.plane);

        Plane4dBrush plane4dBrush4 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush4, RenderResources.plane);

        Plane4dBrush plane4dBrush5 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush5, RenderResources.plane);
    }
}