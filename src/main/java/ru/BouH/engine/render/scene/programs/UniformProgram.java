package ru.BouH.engine.render.scene.programs;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import ru.BouH.engine.game.init.Game;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class UniformProgram {
    private final int programId;
    private final Map<String, Integer> uniforms;

    public UniformProgram(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = GL20.glGetUniformLocation(this.programId, uniformName);
        if (uniformLocation < 0) {
            Game.getGame().getLogManager().warn("Could not find uniform " + uniformName);
        }
        this.uniforms.put(uniformName, uniformLocation);
    }

    public boolean setUniform(String uniformName, Object value) {
        if (value instanceof Vector2d) {
            this.setUniform(uniformName, (Vector2d) value);
            return true;
        } else if (value instanceof Vector4d) {
            this.setUniform(uniformName, (Vector4d) value);
            return true;
        } else if (value instanceof Matrix4d) {
            this.setUniform(uniformName, (Matrix4d) value);
            return true;
        } else if (value instanceof Vector3d) {
            this.setUniform(uniformName, (Vector3d) value);
            return true;
        } else if (value instanceof Integer) {
            this.setUniform(uniformName, (int) value);
            return true;
        } else if (value instanceof Float) {
            this.setUniform(uniformName, (float) value);
            return true;
        }
        return false;
    }

    public void setUniform(String uniformName, Matrix4d value) {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = memoryStack.mallocFloat(16);
            value.get(floatBuffer);
            GL20.glUniformMatrix4fv(this.uniforms.get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(String uniformName, Vector2d value) {
        GL20.glUniform2f(this.uniforms.get(uniformName), (float) value.x, (float) value.y);
    }

    public void setUniform(String uniformName, Vector4d value) {
        GL20.glUniform4f(this.uniforms.get(uniformName), (float) value.x, (float) value.y, (float) value.z, (float) value.w);
    }

    public void setUniform(String uniformName, Vector3d value) {
        GL20.glUniform3f(this.uniforms.get(uniformName), (float) value.x, (float) value.y, (float) value.z);
    }

    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(this.uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(this.uniforms.get(uniformName), value);
    }
}
