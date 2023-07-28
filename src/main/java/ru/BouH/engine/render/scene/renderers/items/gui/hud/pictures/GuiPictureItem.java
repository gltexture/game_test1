package ru.BouH.engine.render.scene.renderers.items.gui.hud.pictures;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model2DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.RenderGui;
import ru.BouH.engine.render.scene.renderers.items.gui.AbstractGui;
import ru.BouH.engine.render.scene.renderers.items.gui.GUI;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.font.FontTexture;

import java.util.ArrayList;
import java.util.List;

public class GuiPictureItem extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private Texture texture;
    private float width;
    private float height;

    public GuiPictureItem(@NotNull Texture texture, int x, int y, float w, float h, int zLevel) {
        super("gui_picture: " + texture.getPath(), zLevel);
        this.setPicture(texture, x, y, w, h);
    }

    public GuiPictureItem(@NotNull Texture texture, int x, int y, float w, float h) {
        this(texture, x, y, w, h, 0);
    }

    public GuiPictureItem(@NotNull Texture texture, int x, int y, int zLevel) {
        super("gui_picture: " + texture.getPath(), zLevel);
        this.setPicture(texture, x, y);
    }

    public GuiPictureItem(@NotNull Texture texture, int x, int y) {
        this(texture, x, y, 0);
    }

    public void setPicture(Texture texture, int x, int y) {
        this.setPicture(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    public void setPicture(Texture texture, int x, int y, float w, float h) {
        if (texture.isValid()) {
            this.texture = texture;
            if (this.getModel2DInfo() != null) {
                this.getModel2DInfo().getModel2D().cleanMesh();
            }
            this.width = w;
            this.height = h;
            this.setModel2DInfo(this.createModel(texture));
            this.getModel2DInfo().setPosition(x, y);
        }
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Texture getTexture() {
        return this.texture;
    }

    private Model2DInfo createModel(Texture texture) {
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

        return new Model2DInfo(new Model2D(f1, i1, f2));
    }

    @Override
    public IRenderFabric renderFabric() {
        return GuiPictureItem.renderGui;
    }

    @Override
    public void performGuiTexture() {
        this.getTexture().performTexture();
    }
}
