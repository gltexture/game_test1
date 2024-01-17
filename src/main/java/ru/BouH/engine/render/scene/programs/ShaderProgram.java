package ru.BouH.engine.render.scene.programs;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.shaders.ShaderGroup;

public class ShaderProgram {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private int geometricShaderId;

    public ShaderProgram() {
        this.programId = GL20.glCreateProgram();
        if (this.programId == 0) {
            Game.getGame().getLogManager().error("Could not create shader program!");
        }
    }

    public void createShader(ShaderGroup shaderGroup) {
        if (shaderGroup.getFragmentShader() != null) {
            this.createFragmentShader(shaderGroup.getFragmentShader().getShaderText());
        }
        if (shaderGroup.getVertexShader() != null) {
            this.createVertexShader(shaderGroup.getVertexShader().getShaderText());
        }
        if (shaderGroup.getGeometricShader() != null) {
            this.createGeometricShader(shaderGroup.getGeometricShader().getShaderText());
        }
    }

    public void createVertexShader(String shader) {
        this.vertexShaderId = this.createShader(shader, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shader) {
        this.fragmentShaderId = this.createShader(shader, GL20.GL_FRAGMENT_SHADER);
    }

    public void createGeometricShader(String shader) {
        this.geometricShaderId = this.createShader(shader, GL43.GL_GEOMETRY_SHADER);
    }

    private int createShader(String shader, int type) {
        int id = GL20.glCreateShader(type);
        if (id == 0) {
            Game.getGame().getLogManager().error("Could not create Shader: " + type);
        }
        GL20.glShaderSource(id, shader);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == 0) {
            Game.getGame().getLogManager().error("Compile shader error: " + GL20.glGetShaderInfoLog(id, 4096));
            Game.getGame().getLogManager().debug(shader);
        }
        GL20.glAttachShader(this.programId, id);
        return id;
    }

    public boolean link() {
        GL20.glLinkProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0) {
            Game.getGame().getLogManager().warn("Could not link Shader " + GL20.glGetShaderInfoLog(this.programId, 4096));
            return false;
        }
        if (this.vertexShaderId != 0) {
            GL20.glDetachShader(this.programId, this.vertexShaderId);
        }
        if (this.fragmentShaderId != 0) {
            GL20.glDetachShader(this.programId, this.fragmentShaderId);
        }
        if (this.geometricShaderId != 0) {
            GL20.glDetachShader(this.programId, this.geometricShaderId);
        }
        GL20.glValidateProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_VALIDATE_STATUS) == 0) {
            Game.getGame().getLogManager().warn("Could not validate Shader " + GL20.glGetShaderInfoLog(this.programId, 4096));
            return false;
        }
        return true;
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
