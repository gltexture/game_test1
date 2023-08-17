package ru.BouH.engine.render.scene.programs;

import org.lwjgl.opengl.GL20;
import ru.BouH.engine.game.Game;

public class ShaderProgram {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram() {
        this.programId = GL20.glCreateProgram();
        if (this.programId == 0) {
            Game.getGame().getLogManager().error("Could not create shader program!");
        }
    }

    public void createVertexShader(String shader) {
        this.vertexShaderId = this.createShader(shader, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shader) {
        this.fragmentShaderId = this.createShader(shader, GL20.GL_FRAGMENT_SHADER);
    }

    private int createShader(String shader, int type) {
        int id = GL20.glCreateShader(type);
        if (id == 0) {
            Game.getGame().getLogManager().error("Could not create Shader: " + type);
        }
        GL20.glShaderSource(id, shader);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == 0) {
            Game.getGame().getLogManager().error("Compile shader error: " + GL20.glGetShaderInfoLog(id, 1024));
        }
        GL20.glAttachShader(this.programId, id);
        return id;
    }

    public void link() {
        GL20.glLinkProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0) {
            Game.getGame().getLogManager().error("Could not link Shader " + GL20.glGetShaderInfoLog(this.programId, 1024));
        }
        if (this.vertexShaderId != 0) {
            GL20.glDetachShader(this.programId, this.vertexShaderId);
        }
        if (this.fragmentShaderId != 0) {
            GL20.glDetachShader(this.programId, this.fragmentShaderId);
        }
        GL20.glValidateProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_VALIDATE_STATUS) == 0) {
            Game.getGame().getLogManager().warn("Could not validate Shader " + GL20.glGetShaderInfoLog(this.programId, 1024));
        }
    }

    public void bind() {
        GL20.glUseProgram(this.programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public int getProgramId() {
        return this.programId;
    }

    public void clean() {
        this.unbind();
        if (this.programId != 0) {
            GL20.glDeleteProgram(this.programId);
        }
    }
}
