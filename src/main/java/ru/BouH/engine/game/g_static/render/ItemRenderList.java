package ru.BouH.engine.game.g_static.render;

import ru.BouH.engine.render.scene.fabric.RenderBrushPlane;
import ru.BouH.engine.render.scene.fabric.RenderEntity;
import ru.BouH.engine.render.scene.fabric.RenderNull;
import ru.BouH.engine.render.scene.objects.data.RenderMiscData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.objects.items.BrushPlanePhysXObject;
import ru.BouH.engine.render.scene.objects.items.EntityPhysXObject;
import ru.BouH.engine.render.scene.objects.texture.samples.DevGround;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.TestGradientSample;
import ru.BouH.engine.render.utils.Utils;

public class ItemRenderList {
    public static RenderModeledData entityCube;
    public static RenderModeledData entityCube2;
    public static RenderModeledData entityCube3;
    public static RenderModeledData entityTerrain;
    public static RenderModeledData entityLamp;
    public static RenderMiscData player;
    public static RenderMiscData plane;

    public static void init() {
        PNGTexture pngTexture1 = PNGTexture.createTexture("props/cube.png");
        PNGTexture pngTexture2 = PNGTexture.createTexture("props/cube3.png");
        TestGradientSample testGradientSample = new TestGradientSample();
        DevGround devGround = new DevGround();

        ItemRenderList.entityCube = new RenderModeledData(new RenderEntity(), pngTexture1, EntityPhysXObject.class, Utils.loadMesh("prop/cube.obj"));
        ItemRenderList.entityCube2 = new RenderModeledData(new RenderEntity(), testGradientSample, EntityPhysXObject.class, Utils.loadMesh("prop/cube.obj"));
        ItemRenderList.entityCube3 = new RenderModeledData(new RenderEntity(), pngTexture2, EntityPhysXObject.class, Utils.loadMesh("prop/cube.obj"));
        ItemRenderList.entityLamp = new RenderModeledData(new RenderEntity(), EntityPhysXObject.class, Utils.loadMesh("prop/sphere.obj"));
        ItemRenderList.player = new RenderMiscData(new RenderNull(), EntityPhysXObject.class);
        ItemRenderList.plane = new RenderMiscData(new RenderBrushPlane(), devGround, BrushPlanePhysXObject.class);
    }
}
