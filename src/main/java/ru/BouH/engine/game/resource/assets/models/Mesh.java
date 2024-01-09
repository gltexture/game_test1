package ru.BouH.engine.game.resource.assets.models;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.resource.assets.models.formats.IFormat;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Mesh <T extends IFormat> {
    private boolean baked;
    private final T format;
    private final List<Integer> indexes;

    private final List<Float> attributePositions;
    private final List<Float> attributeTextureCoordinates;
    private final List<Float> attributeNormals;
    private final List<Float> attributeTangents;
    private final List<Float> attributeBitangents;

    private int totalVertices;

    private int vao;
    private int indexVbo;
    private int positionVbo;
    private int textureCoordinatesVbo;
    private int normalsVbo;
    private int tangentsVbo;
    private int bitangentsVbo;

    public Mesh(@NotNull T t) {
        this.format = t;
        this.indexes = new ArrayList<>();
        this.attributePositions = new ArrayList<>();
        this.attributeTextureCoordinates = new ArrayList<>();
        this.attributeNormals = new ArrayList<>();
        this.attributeTangents = new ArrayList<>();
        this.attributeBitangents = new ArrayList<>();
        this.totalVertices = 0;
        this.vao = 0;
        this.indexVbo = 0;
        this.positionVbo = 0;
        this.textureCoordinatesVbo = 0;
        this.normalsVbo = 0;
        this.tangentsVbo = 0;
        this.bitangentsVbo = 0;
        this.baked = false;
    }

    public void putPositionValue(float position) {
        this.attributePositions.add(position);
    }

    public void putTextureCoordinateValue(float texCoord) {
        this.attributeTextureCoordinates.add(texCoord);
    }

    public void putNormalValue(float normal) {
        this.attributeNormals.add(normal);
    }

    public void putTangentValue(float tangent) {
        this.attributeTangents.add(tangent);
    }

    public void putBitangentValue(float bitangent) {
        this.attributeBitangents.add(bitangent);
    }

    public void putIndexValue(int index) {
        this.indexes.add(index);
    }

    public void putPositionValues(float[] positions) {
        for (float f : positions) {
            this.attributePositions.add(f);
        }
    }

    public void putTextureCoordinateValues(float[] texCoordinates) {
        for (float f : texCoordinates) {
            this.attributeTextureCoordinates.add(f);
        }
    }

    public void putNormalValues(float[] normals) {
        for (float f : normals) {
            this.attributeNormals.add(f);
        }
    }

    public void putTangentValues(float[] tangents) {
        for (float f : tangents) {
            this.attributeTangents.add(f);
        }
    }

    public void putBitangentValues(float[] bitangents) {
        for (float f : bitangents) {
            this.attributeBitangents.add(f);
        }
    }

    public void putIndexValues(int[] indexes) {
        for (int f : indexes) {
            this.indexes.add(f);
        }
    }

    public void bakeMesh() {
        if (this.baked) {
            throw new GameException("Tried to bake model, that is already had been baked!");
        }
        int[] index = this.reorderIntsArray(this.indexes);

        float[] position = this.reorderFloatsArray(this.attributePositions);
        float[] texCoord = this.reorderFloatsArray(this.attributeTextureCoordinates);
        float[] normals = this.reorderFloatsArray(this.attributeNormals);
        float[] tangent = this.reorderFloatsArray(this.attributeTangents);
        float[] bitangent = this.reorderFloatsArray(this.attributeBitangents);

        this.totalVertices = index.length;
        FloatBuffer posBuffer = null;
        FloatBuffer texBuffer = null;
        FloatBuffer normalsBuffer = null;
        FloatBuffer tangentBuffer = null;
        FloatBuffer bitangentBuffer = null;

        IntBuffer inxBuffer = MemoryUtil.memAllocInt(index.length);
        inxBuffer.put(index).flip();

        if (position != null) {
            posBuffer = MemoryUtil.memAllocFloat(position.length);
            posBuffer.put(position).flip();
        }

        if (texCoord != null) {
            texBuffer = MemoryUtil.memAllocFloat(texCoord.length);
            texBuffer.put(texCoord).flip();
        }

        if (normals != null) {
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
        }

        if (tangent != null) {
            tangentBuffer = MemoryUtil.memAllocFloat(tangent.length);
            tangentBuffer.put(tangent).flip();
        }

        if (bitangent != null) {
            bitangentBuffer = MemoryUtil.memAllocFloat(bitangent.length);
            bitangentBuffer.put(bitangent).flip();
        }

        this.vao = GL30.glGenVertexArrays();
        this.indexVbo = GL30.glGenBuffers();

        GL30.glBindVertexArray(this.getVao());
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.getIndexVbo());
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, inxBuffer, GL30.GL_STATIC_DRAW);

        if (posBuffer != null) {
            this.positionVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getPositionVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, posBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        }

        if (texCoord != null) {
            this.textureCoordinatesVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getTextureCoordinatesVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, texBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
        }

        if (normals != null) {
            this.normalsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getNormalsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, normalsBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, 0, 0);
        }

        if (tangent != null) {
            this.tangentsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getTangentsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, tangentBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(3, 3, GL30.GL_FLOAT, false, 0, 0);
        }

        if (bitangent != null) {
            this.bitangentsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getBitangentsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bitangentBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(4, 3, GL30.GL_FLOAT, false, 0, 0);
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        this.indexes.clear();
        this.attributePositions.clear();
        this.attributeTextureCoordinates.clear();
        this.attributeNormals.clear();
        this.attributeTangents.clear();
        this.attributeBitangents.clear();

        this.memFree(inxBuffer);
        this.memFree(posBuffer);
        this.memFree(texBuffer);
        this.memFree(normalsBuffer);
        this.memFree(tangentBuffer);
        this.memFree(bitangentBuffer);
        this.baked = true;
    }

    public void clean() {
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(this.getIndexVbo());
        GL30.glDeleteBuffers(this.getPositionVbo());
        GL30.glDeleteBuffers(this.getTextureCoordinatesVbo());
        GL30.glDeleteBuffers(this.getNormalsVbo());
        GL30.glDeleteBuffers(this.getTangentsVbo());
        GL30.glDeleteBuffers(this.getBitangentsVbo());
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.getVao());
    }

    private void memFree(Buffer buffer) {
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
        }
    }

    public int getVao() {
        return this.vao;
    }

    public int getIndexVbo() {
        return this.indexVbo;
    }

    public int getPositionVbo() {
        return this.positionVbo;
    }

    public int getTextureCoordinatesVbo() {
        return this.textureCoordinatesVbo;
    }

    public int getNormalsVbo() {
        return this.normalsVbo;
    }

    public int getTangentsVbo() {
        return this.tangentsVbo;
    }

    public int getBitangentsVbo() {
        return this.bitangentsVbo;
    }

    public int getTotalVertices() {
        return this.totalVertices;
    }

    public List<Float> getAttributePositions() {
        return this.attributePositions;
    }

    public List<Integer> getIndexes() {
        return this.indexes;
    }

    public List<Float> getAttributeTextureCoordinates() {
        return this.attributeTextureCoordinates;
    }

    public List<Float> getAttributeBitangents() {
        return this.attributeBitangents;
    }

    public List<Float> getAttributeTangents() {
        return this.attributeTangents;
    }

    public List<Float> getAttributeNormals() {
        return this.attributeNormals;
    }

    private int[] reorderIntsArray(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int[] a = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }

    private float[] reorderFloatsArray(List<Float> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        float[] a = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }

    public T getFormat() {
        return this.format;
    }
}
