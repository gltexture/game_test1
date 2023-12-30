package ru.BouH.engine.game.resource.assets;

import org.joml.Vector2d;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.game.resource.assets.utils.AssetsHelper;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.fabric.RenderBrushPlane;
import ru.BouH.engine.render.scene.fabric.RenderEntity;
import ru.BouH.engine.render.scene.fabric.RenderNull;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.data.RenderModeledData;
import ru.BouH.engine.render.scene.objects.data.StandardRenderData;
import ru.BouH.engine.render.scene.objects.gui.font.FontCode;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;
import ru.BouH.engine.render.scene.objects.items.BrushPlanePhysXObject;
import ru.BouH.engine.render.scene.objects.items.EntityPhysicsObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsColoredLamp;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.samples.Color3FA;
import ru.BouH.engine.render.scene.objects.texture.samples.CubeMapPNGTexture;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;

import java.awt.*;

public class RenderAssets implements IAssets {
    public FontTexture standardFont;
    public PNGTexture pngGuiPic1;
    public MeshModel cubeObj;
    public CubeMapPNGTexture skyboxCubeMap;
    public RenderData entityCube;
    public RenderData entityLargeCube;
    public RenderData entityLamp;
    public RenderData player;
    public RenderData plane;
    public RenderData planeBrick;
    public PictureSample bricksPng;
    public PictureSample grassPng;
    public PictureSample bricksNormalMap;

    public void load() {
        this.standardFont = new FontTexture(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        this.pngGuiPic1 = PNGTexture.createTexture("gui/pictures/meme2.png");
        this.bricksPng = PNGTexture.createTexture("props/bricks.png");
        this.grassPng = PNGTexture.createTexture("terrain/grass.png");
        this.bricksNormalMap = PNGTexture.createTexture("normals/bricks.png");
        this.cubeObj = AssetsHelper.loadMesh("prop/cube.obj");
        this.skyboxCubeMap = new CubeMapPNGTexture("skybox/sky1");

        this.entityCube = new RenderModeledData(new RenderEntity(), bricksPng, EntityPhysicsObject.class, cubeObj).attachNormalMap(ResourceManager.instance.getRenderAssets().bricksNormalMap);
        this.entityLargeCube = new RenderModeledData(new RenderEntity(), bricksPng, EntityPhysicsObject.class, cubeObj).setTextureScaling(new Vector2d(32.0f, 32.0f)).attachNormalMap(ResourceManager.instance.getRenderAssets().bricksNormalMap);
        this.entityLamp = new RenderModeledData(new RenderEntity(), new Color3FA(0xffffff), PhysicsColoredLamp.class, cubeObj);
        this.player = new StandardRenderData(new RenderNull(), EntityPhysicsObject.class, new RenderData.RenderProperties(true, true, false));
        this.plane = new StandardRenderData(new RenderBrushPlane(), bricksPng, BrushPlanePhysXObject.class).setTextureScaling(new Vector2d(32.0f, 4.0f)).attachNormalMap(ResourceManager.instance.getRenderAssets().bricksNormalMap);
        this.planeBrick = new StandardRenderData(new RenderBrushPlane(), grassPng, BrushPlanePhysXObject.class).setTextureScaling(new Vector2d(128.0f, 128.0f));
    }

    @Override
    public boolean parallelLoading() {
        return false;
    }
}
