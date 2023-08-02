package ru.BouH.engine.render.scene.programs;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.math.IntPair;

import java.util.HashMap;
import java.util.Map;

public class UniformBufferProgram {
    private final int uboBlock;
    private final Map<String, IntPair> uniforms;

    public UniformBufferProgram() {
        this.uboBlock = GL20.glGenBuffers();
        if (this.uboBlock == 0) {
            Game.getGame().getLogManager().error("Could not create uniform buffer program!");
        }
        this.uniforms = new HashMap<>();
    }

    public void createUniformBuffer(String uniformName, int bytes) {
        int uniformLocation = GL43.glGetUniformBlockIndex(this.uboBlock, uniformName);
        if (uniformLocation < 0) {
            Game.getGame().getLogManager().warn("Could not find uniform " + uniformName);
        }
        this.setupUniformBuffer(uniformName, uniformLocation, bytes, this.uniforms.size());
    }

    private void setupUniformBuffer(String uniformName, int uniformLocation, int bytes, int binding) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.uboBlock);
        GL43.glBindBufferBase(GL43.GL_UNIFORM_BUFFER, binding, this.uboBlock);
        GL43.glBufferData(GL43.GL_UNIFORM_BUFFER, bytes, GL43.GL_STATIC_DRAW);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
        this.uniforms.put(uniformName, new IntPair(uniformLocation, binding));
    }

    public void setUniformBufferData(String uniformBufferData, double[] values) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.uboBlock);
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, 0, values);
        GL43.glUniformBlockBinding(GL43.GL_UNIFORM_BUFFER, this.uniforms.get(uniformBufferData).getA1(), this.uniforms.get(uniformBufferData).getA2());
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(String uniformBufferData, float[] values) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.uboBlock);
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, 0, values);
        GL43.glUniformBlockBinding(GL43.GL_UNIFORM_BUFFER, this.uniforms.get(uniformBufferData).getA1(), this.uniforms.get(uniformBufferData).getA2());
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public int getUboBlock() {
        return this.uboBlock;
    }
}
