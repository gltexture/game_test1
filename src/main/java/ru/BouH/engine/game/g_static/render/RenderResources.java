package ru.BouH.engine.game.g_static.render;

import org.joml.Vector2d;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.fabric.RenderBrushPlane;
import ru.BouH.engine.render.scene.fabric.RenderEntity;
import ru.BouH.engine.render.scene.fabric.RenderNull;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.objects.data.StandardRenderData;
import ru.BouH.engine.render.scene.objects.gui.font.FontCode;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;
import ru.BouH.engine.render.scene.objects.items.*;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.objects.texture.samples.CubeMapPNGTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.utils.Utils;

import java.awt.*;

public class RenderResources {
    public static FontTexture standardFont;
    public static PNGTexture pngGuiPic1;
    public static RenderModeledData entityCube;
    public static RenderModeledData entityLamp;
    public static StandardRenderData player;
    public static StandardRenderData plane;
    public static StandardRenderData planeBrick;
    public static PictureSample bricksPng;
    public static PictureSample grassPng;
    public static PictureSample bricksNormalMap;
    public static MeshModel cubeObj;
    public static CubeMapPNGTexture skyboxCubeMap;

    public void preLoad() {
        RenderResources.standardFont = new FontTexture(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        RenderResources.pngGuiPic1 = PNGTexture.createTexture("gui/pictures/meme2.png");
        RenderResources.bricksPng = PNGTexture.createTexture("props/bricks.png");
        RenderResources.grassPng = PNGTexture.createTexture("terrain/grass.png");
        RenderResources.bricksNormalMap = PNGTexture.createTexture("normals/bricks.png");
        RenderResources.cubeObj = Utils.loadMesh("prop/cube.obj");
        RenderResources.skyboxCubeMap = new CubeMapPNGTexture("skybox/sky1");

        RenderData.RenderProperties physicalObjectProperties = new RenderData.RenderProperties(true, true, false);
        RenderResources.entityCube = (RenderModeledData) new RenderModeledData(new RenderEntity(), bricksPng, EntityPhysicsObject.class, cubeObj, physicalObjectProperties).attachNormalMap(RenderResources.bricksNormalMap);
        RenderResources.entityLamp = new RenderModeledData(new RenderEntity(), new Color3FA(0xffffff), PhysicsColoredLamp.class, cubeObj, physicalObjectProperties);
        RenderResources.player = new StandardRenderData(new RenderNull(), EntityPhysicsObject.class);
        RenderResources.plane = (StandardRenderData) new StandardRenderData(new RenderBrushPlane(), bricksPng, BrushPlanePhysXObject.class).setTextureScaling(new Vector2d(32.0f, 4.0f)).attachNormalMap(RenderResources.bricksNormalMap);
        RenderResources.planeBrick = (StandardRenderData) new StandardRenderData(new RenderBrushPlane(), grassPng, BrushPlanePhysXObject.class).setTextureScaling(new Vector2d(128.0f, 128.0f));
    }
}
