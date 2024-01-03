package ru.BouH.engine.render.scene.objects.gui.hud;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.fabric.base.RenderFabric;
import ru.BouH.engine.render.scene.fabric.RenderGui;
import ru.BouH.engine.render.scene.objects.gui.AbstractGui;
import ru.BouH.engine.render.scene.objects.gui.font.FontTexture;

import java.util.ArrayList;
import java.util.List;

public class GuiText extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private final FontTexture fontTexture;
    private String text;
    private float width;

    public GuiText(String text, ShaderManager shaderManager, int x, int y) {
        this(text, shaderManager, ResourceManager.renderAssets.standardFont, x, y, 0);
    }

    public GuiText(String text, ShaderManager shaderManager, FontTexture fontTexture, int x, int y) {
        this(text, shaderManager, fontTexture, x, y, 0);
    }

    public GuiText(String text, ShaderManager shaderManager, FontTexture fontTexture, int x, int y, int zLevel) {
        super("gui_text: " + text, shaderManager, zLevel);
        this.fontTexture = fontTexture;
        this.setText(text);
        this.getModel2DInfo().setPosition(x, y);
    }

    public GuiText(String text, ShaderManager shaderManager, int x, int y, int zLevel) {
        this(text, shaderManager, ResourceManager.renderAssets.standardFont, x, y, zLevel);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        if (this.getModel2DInfo() != null) {
            this.getModel2DInfo().clean();
        }
        this.setModel2DInfo(this.createModel());
    }

    public FontTexture getFontTexture() {
        return this.fontTexture;
    }

    public float getWidth() {
        return this.width;
    }

    private Model2D createModel() {
        char[] chars = this.getText().toCharArray();
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float startX = 0.0f;
        for (int i = 0; i < chars.length; i++) {
            FontTexture.CharInfo charInfo = this.fontTexture.getCharInfo(chars[i]);

            positions.add(startX);
            positions.add(0.0f);
            positions.add((float) this.getzLevel());
            textureCoordinates.add((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
            textureCoordinates.add(0.0f);
            indices.add(i * 4);

            positions.add(startX);
            positions.add((float) this.fontTexture.getHeight());
            positions.add((float) this.getzLevel());
            textureCoordinates.add((float) charInfo.getStartX() / (float) this.fontTexture.getWidth());
            textureCoordinates.add(1.0f);
            indices.add(i * 4 + 1);

            positions.add(startX + charInfo.getWidth());
            positions.add((float) this.fontTexture.getHeight());
            positions.add((float) this.getzLevel());
            textureCoordinates.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.fontTexture.getWidth());
            textureCoordinates.add(1.0f);
            indices.add(i * 4 + 2);

            positions.add(startX + charInfo.getWidth());
            positions.add(0.0f);
            positions.add((float) this.getzLevel());
            textureCoordinates.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) this.fontTexture.getWidth());
            textureCoordinates.add(0.0f);
            indices.add(i * 4 + 3);

            indices.add(i * 4);
            indices.add(i * 4 + 2);

            startX += charInfo.getWidth();
        }

        this.width = startX;

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
        return GuiText.renderGui;
    }

    @Override
    public boolean isHasRender() {
        return this.getModel2DInfo() != null;
    }

    @Override
    public void performGuiTexture() {
        this.getFontTexture().getTexture().performTexture(GL30.GL_TEXTURE0);
    }
}
