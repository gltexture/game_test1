package ru.BouH.engine.render.gui.hud.font;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.gui.AbstractGui;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model2DInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiText extends AbstractGui {
    private String text;
    private float width;
    private static final FontTexture standardFont = new FontTexture(new Font("Arial", Font.PLAIN, 24), FontCode.Window);
    private final FontTexture fontTexture;

    public GuiText(String text, FontTexture fontTexture, int zLevel) {
        super("gui_text", zLevel);
        this.fontTexture = fontTexture;
        this.setText(text);
    }

    public GuiText(String text, int zLevel) {
        this(text, GuiText.standardFont, zLevel);
    }

    public void setText(String text) {
        this.text = text;
        if (this.getModel2DInfo() != null) {
            this.getModel2DInfo().getMesh().cleanMesh();
        }
        this.setModel2DInfo(this.createModel());
    }

    public String getText() {
        return this.text;
    }

    @Override
    public void render() {
        this.fontTexture.getTexture().performTexture();
        GL30.glBindVertexArray(this.getModel2DInfo().getMesh().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glDrawElements(GL30.GL_TRIANGLES, this.getModel2DInfo().getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
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
}
