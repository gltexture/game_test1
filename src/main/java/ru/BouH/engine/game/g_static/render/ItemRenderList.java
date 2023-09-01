package ru.BouH.engine.game.g_static.render;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.fabric.RenderBrushPlane;
import ru.BouH.engine.render.scene.fabric.RenderEntity;
import ru.BouH.engine.render.scene.fabric.RenderNull;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.objects.data.StandardRenderData;
import ru.BouH.engine.render.scene.objects.items.BrushPlanePhysXObject;
import ru.BouH.engine.render.scene.objects.items.EntityPhysXObject;
import ru.BouH.engine.render.scene.objects.items.PhysXColoredLamp;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.objects.texture.samples.DefaultSample;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.utils.Utils;

public class ItemRenderList {
    public static RenderModeledData entityCube;
    public static RenderModeledData entityLamp;
    public static StandardRenderData player;
    public static StandardRenderData plane;
    public static PictureSample pictureSample = PNGTexture.createTexture("props/bricks.png");

    public static void init() {
        DefaultSample defaultSample = new DefaultSample(new Vector3d(0.75f, 0.75f, 0.75f), new Vector3d(0.3f, 0.3f, 0.3f), 16);
        DefaultSample defaultSample2 = new DefaultSample(new Vector3d(0.85f, 0.32f, 0.11f), new Vector3d(0.55f, 0.1f, 0.55f), 16);

        ItemRenderList.entityCube = new RenderModeledData(new RenderEntity(), pictureSample, EntityPhysXObject.class, Utils.loadMesh("prop/cube.obj"));
        ItemRenderList.entityCube.attachNormalMap("bricks.png");
        ItemRenderList.entityLamp = new RenderModeledData(new RenderEntity(), new Color3FA(0xffffff), PhysXColoredLamp.class, Utils.loadMesh("prop/cube.obj"));
        ItemRenderList.player = new StandardRenderData(new RenderNull(), EntityPhysXObject.class);
        ItemRenderList.plane = new StandardRenderData(new RenderBrushPlane(), defaultSample, BrushPlanePhysXObject.class);
    }
}
