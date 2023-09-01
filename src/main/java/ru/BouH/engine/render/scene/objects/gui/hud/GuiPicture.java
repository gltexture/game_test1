package ru.BouH.engine.render.scene.objects.gui.hud;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.fabric.RenderFabric;
import ru.BouH.engine.render.scene.fabric.RenderGui;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;

import java.util.ArrayList;
import java.util.List;

public class GuiPicture extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private PNGTexture PNGTexture;
    private float width;
    private float height;

    public GuiPicture(@NotNull PNGTexture PNGTexture, int x, int y, float w, float h, int zLevel) {
        super("gui_picture", zLevel);
        this.setPicture(PNGTexture, x, y, w, h);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, int x, int y, float w, float h) {
        this(PNGTexture, x, y, w, h, 0);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, int x, int y, int zLevel) {
        super("gui_picture", zLevel);
        this.setPicture(PNGTexture, x, y);
    }

    public GuiPicture(@NotNull PNGTexture PNGTexture, int x, int y) {
        this(PNGTexture, x, y, 0);
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
            this.setModel2DInfo(this.createModel(PNGTexture));
            this.getModel2DInfo().setPosition(x, y);
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

    private Model2D createModel(PNGTexture PNGTexture) {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        positions.add(0.0f);
        positions.add(0.0f);
        positions.add((float) this.getzLevel());
        textureCoordinates.add(0.0f);
        textureCoordinates.add(0.0f);

        positions.add(this.getWidth());
        positions.add(0.0f);
        positions.add((float) this.getzLevel());
        textureCoordinates.add(1.0f);
        textureCoordinates.add(0.0f);

        positions.add(0.0f);
        positions.add(this.getHeight());
        positions.add((float) this.getzLevel());
        textureCoordinates.add(0.0f);
        textureCoordinates.add(1.0f);

        positions.add(this.getWidth());
        positions.add(this.getHeight());
        positions.add((float) this.getzLevel());
        textureCoordinates.add(1.0f);
        textureCoordinates.add(1.0f);

        indices.add(1);
        indices.add(2);
        indices.add(3);
        indices.add(0);
        indices.add(2);
        indices.add(1);

        float[] f1 = new float[positions.size()];
        int[] i1 = new int[indices.size()];
        float[] f2 = new float[textureCoordinates.size()];

        for (int i = 0; i < f1.length; i++) {
            f1[i] = positions.get(i);
        }

        for (int i = 0; i < i1.length; i++) {
            i1[i] = indices.get(i);
        }

        for (int i = 0; i < f2.length; i++) {
            f2[i] = textureCoordinates.get(i);
        }

        return new Model2D(new MeshModel(f1, i1, f2));
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
