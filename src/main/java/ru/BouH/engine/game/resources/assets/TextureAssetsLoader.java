package ru.BouH.engine.game.resources.assets;

import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.render.scene.objects.gui.font.FontCode;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

import java.awt.*;

public class TextureAssetsLoader implements IAssetsLoader {
    public TextureSample bricksTexture;
    public TextureSample grassTexture;
    public TextureSample guiTestImage;
    public CubeMapProgram.CubeMapTextureArray skyboxCubeMap;
    public FontTexture standardFont;

    public void load(GameCache gameCache) {
        this.standardFont = new FontTexture(new Font("Cambria", Font.PLAIN, 18), FontCode.Window);
        gameCache.addObjectInBuffer("font1", this.standardFont.getTexture());

        this.guiTestImage = ResourceManager.createTexture("/textures/gui/pictures/meme2.png");
        this.bricksTexture = ResourceManager.createTexture("/textures/props/bricks.png");
        this.grassTexture = ResourceManager.createTexture("/textures/terrain/grass02.png");
        this.skyboxCubeMap = new CubeMapProgram.CubeMapTextureArray("skybox/sky1", ".png");
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 0;
    }
}
