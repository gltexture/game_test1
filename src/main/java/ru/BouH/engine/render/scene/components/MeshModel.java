package ru.BouH.engine.render.scene.components;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class MeshModel implements IMesh {
    private final int vao;
    private final int idxVbo;
    private final int posVbo;
    private final int textureVbo;
    private final int normalsVbo;
    private final int vertexCount;

    public MeshModel(float[] pos, int[] inc, float[] textPos, float[] normals) {
        IntBuffer incBuffer = MemoryUtil.memAllocInt(inc.length);
        FloatBuffer vrtBuffer = MemoryUtil.memAllocFloat(pos.length);
        FloatBuffer txtPosBuffer = MemoryUtil.memAllocFloat(textPos.length);
        FloatBuffer normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
        this.vertexCount = inc.length;
        this.vao = GL30.glGenVertexArrays();

        vrtBuffer.put(pos).flip();
        incBuffer.put(inc).flip();
        txtPosBuffer.put(textPos).flip();
        normalsBuffer.put(normals).flip();

        this.idxVbo = GL30.glGenBuffers();
        this.posVbo = GL30.glGenBuffers();
        this.textureVbo = GL30.glGenBuffers();
        this.normalsVbo = GL30.glGenBuffers();

        GL30.glBindVertexArray(this.vao);

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.idxVbo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, incBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(incBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.posVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vrtBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(vrtBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.textureVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, txtPosBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(txtPosBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.normalsVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, normalsBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(normalsBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public MeshModel(float[] pos, int[] inc, float[] textPos) {
        IntBuffer incBuffer = MemoryUtil.memAllocInt(inc.length);
        FloatBuffer vrtBuffer = MemoryUtil.memAllocFloat(pos.length);
        FloatBuffer txtPosBuffer = MemoryUtil.memAllocFloat(textPos.length);
        this.vertexCount = inc.length;
        this.vao = GL30.glGenVertexArrays();

        vrtBuffer.put(pos).flip();
        incBuffer.put(inc).flip();
        txtPosBuffer.put(textPos).flip();

        this.idxVbo = GL30.glGenBuffers();
        this.posVbo = GL30.glGenBuffers();
        this.textureVbo = GL30.glGenBuffers();
        this.normalsVbo = -999;

        GL30.glBindVertexArray(this.vao);

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.idxVbo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, incBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(incBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.posVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vrtBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(vrtBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.textureVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, txtPosBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(txtPosBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public MeshModel(float[] pos, int[] inc) {
        IntBuffer incBuffer = MemoryUtil.memAllocInt(inc.length);
        FloatBuffer vrtBuffer = MemoryUtil.memAllocFloat(pos.length);
        this.vertexCount = inc.length;
        this.vao = GL30.glGenVertexArrays();

        vrtBuffer.put(pos).flip();
        incBuffer.put(inc).flip();

        this.idxVbo = GL30.glGenBuffers();
        this.posVbo = GL30.glGenBuffers();
        this.textureVbo = -999;
        this.normalsVbo = -999;

        GL30.glBindVertexArray(this.vao);

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.idxVbo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, incBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(incBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.posVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vrtBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(vrtBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public MeshModel(float[] pos) {
        FloatBuffer vrtBuffer = MemoryUtil.memAllocFloat(pos.length);
        this.vao = GL30.glGenVertexArrays();
        this.vertexCount = 0;
        vrtBuffer.put(pos).flip();

        this.idxVbo = GL30.glGenBuffers();
        this.posVbo = GL30.glGenBuffers();
        this.textureVbo = -999;
        this.normalsVbo = -999;

        GL30.glBindVertexArray(this.vao);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.posVbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vrtBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(vrtBuffer);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public int getVao() {
        return this.vao;
    }

    public int getIdxVbo() {
        return this.idxVbo;
    }

    public int getPosVbo() {
        return this.posVbo;
    }

    public int getTextureVbo() {
        return this.textureVbo;
    }

    public int getNormalsVbo() {
        return this.normalsVbo;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public void cleanMesh() {
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(this.getIdxVbo());
        GL30.glDeleteBuffers(this.getPosVbo());
        GL30.glDeleteBuffers(this.getTextureVbo());
        GL30.glDeleteBuffers(this.getNormalsVbo());
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.getVao());
    }
}
