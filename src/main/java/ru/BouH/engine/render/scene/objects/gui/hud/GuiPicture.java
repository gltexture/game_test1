package ru.BouH.engine.render.scene.objects.gui.hud;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resource.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.fabric.RenderGui;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;

public class GuiPicture extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private PNGTexture PNGTexture;
    private float width;
    private float height;

    public GuiPicture(@NotNull PNGTexture PNGTexture, ShaderManager shaderManager, int x, int y, float w, float h, int zLevel) {
        super("gui_picture", shaderManager, zLevel);
        this.setPicture(PNGTexture, x, y, w, h);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, ShaderManager shaderManager, int x, int y, float w, float h) {
        this(PNGTexture, shaderManager, x, y, w, h, 0);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, ShaderManager shaderManager, int x, int y, int zLevel) {
        super("gui_picture", shaderManager, zLevel);
        this.setPicture(PNGTexture, x, y);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, ShaderManager shaderManager, int x, int y) {
        this(PNGTexture, shaderManager, x, y, 0);
    }

    public void setPicture(PNGTexture PNGTexture, int x, int y) {
        this.setPicture(PNGTexture, x, y, PNGTexture.getWidth(), PNGTexture.getHeight());
    }

    public void setPicture(PNGTexture PNGTexture, int x, int y, float w, float h) {
        if (PNGTexture.isValid()) {
            this.PNGTexture = PNGTexture;
            if (this.getModel2DInfo() != null) {
                this.getModel2DInfo().clean();
            }
            this.width = w;
            this.height = h;
            this.setModel2DInfo(MeshHelper.generateVector2DMesh(new Vector2d(x, y), new Vector2d(x + w, y + h)));
        }
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public PNGTexture getTexture() {
        return this.PNGTexture;
    }

    @Override
    public RenderFabric renderFabric() {
        return GuiPicture.renderGui;
    }

    @Override
    public boolean isHasRender() {
        return this.getModel2DInfo() != null;
    }

    @Override
    public void performGuiTexture() {
        this.getTexture().performTexture(GL30.GL_TEXTURE0);
    }
}
