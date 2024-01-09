package ru.BouH.engine.game;

import org.joml.Vector3d;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.render.scene.Scene;

public class GameEvents {

    public static void populate(World world) {
        GameEvents.addBrushes(world);
        GameEvents.addEntities(world);
        GameEvents.addTriggers(world);
    }

    public static void addEntities(World world) {
        PhysEntityCube entityPropInfo = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 20.0d), new Vector3d(1, 1, 1), 1.0d, new Vector3d(0.0d, 15.0d, 10.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderAssets.entityCube);

        PhysEntityCube entityPropInfo2 = new PhysEntityCube(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 100.0d), new Vector3d(1, 1, 1), 100.0d, new Vector3d(0.0d, 120.0d, 300.0d), new Vector3d(0.0d));
        Game.getGame().getProxy().addItemInWorlds(entityPropInfo2, ResourceManager.renderAssets.entityLargeCube);
    }

    public static void addTriggers(World world) {
        world.createSimpleTriggerZone(new ITriggerZone.Zone(new Vector3d(350.0d, 0.0d, 0.0d), new Vector3d(5.0d, 5.0d, 5.0d)), (e) -> {
            if (e instanceof EntityPlayerSP) {
                Scene.testTrigger = true;
            }
        }, (e) -> {
            if (e instanceof EntityPlayerSP) {
                Scene.testTrigger = false;
            }
        });
    }

    public static void addBrushes(World world) {
        final int size = 500;
        final int wallH = 100;

        Plane4dBrush plane4dBrush = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(-size, 0, size), new Vector3d(size, 0, -size), new Vector3d(size, 0, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush, ResourceManager.renderAssets.planeBrick);

        Plane4dBrush plane4dBrush2 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size), new Vector3d(-size, 0, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush2, ResourceManager.renderAssets.plane);

        Plane4dBrush plane4dBrush3 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(-size, 0, -size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size), new Vector3d(-size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush3, ResourceManager.renderAssets.plane);

        Plane4dBrush plane4dBrush4 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(-size, 0, size), new Vector3d(-size, wallH, size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush4, ResourceManager.renderAssets.plane);

        Plane4dBrush plane4dBrush5 = new Plane4dBrush(world, RigidBodyObject.PhysProperties.createProperties(Materials.brickCube), new Vector3d[]{new Vector3d(size, 0, size), new Vector3d(size, wallH, size), new Vector3d(size, 0, -size), new Vector3d(size, wallH, -size)});
        Game.getGame().getProxy().addItemInWorlds(plane4dBrush5, ResourceManager.renderAssets.plane);
    }
}