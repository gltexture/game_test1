package ru.BouH.engine.render.scene.renderers.items.gui.hud.text;

import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model2DInfo;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.RenderGui;
import ru.BouH.engine.render.scene.renderers.items.gui.AbstractGui;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.font.FontCode;
import ru.BouH.engine.render.scene.renderers.items.gui.hud.font.FontTexture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiTextItem extends AbstractGui {
    private static final RenderGui renderGui = new RenderGui();
    private static final FontTexture standardFont = new FontTexture(new Font("Arial", Font.PLAIN, 24), FontCode.Window);
    private final FontTexture fontTexture;
    private String text;
    private float width;

    public GuiTextItem(String text, FontTexture fontTexture, int zLevel) {
        super("gui_text", zLevel);
        this.fontTexture = fontTexture;
        this.setText(text);
    }

    public GuiTextItem(String text, int zLevel) {
        this(text, GuiTextItem.standardFont, zLevel);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        if (this.getModel2DInfo() != null) {
            this.getModel2DInfo().getModel2D().cleanMesh();
        }
        this.setModel2DInfo(this.createModel());
    }

    public FontTexture getFontTexture() {
        return this.fontTexture;
    }

    public float getWidth() {
        return this.width;
    }

    private Model2DInfo createModel() {
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

        return new Model2DInfo(new Model2D(f1, i1, f2));
    }

    @Override
    public IRenderFabric renderFabric() {
        return GuiTextItem.renderGui;
    }
}
