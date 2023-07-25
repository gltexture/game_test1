package ru.BouH.engine.render.scene.renderers.main_render.base;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class ShaderManager {
    private final Set<String> uniformsSet;
    private final RenderGroup renderGroup;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;

    public ShaderManager(RenderGroup renderGroup) {
        this.uniformsSet = new HashSet<>();
        this.renderGroup = renderGroup;
    }

    public boolean checkUniform(String u) {
        return this.uniformsSet.contains(u);
    }

    public void addUniform(String s) {
        this.uniformsSet.add(s);
    }

    public void startProgram() {
        this.initShaders(new ShaderProgram());
    }

    public void destroyProgram() {
        if (this.shaderProgram != null) {
            this.shaderProgram.clean();
        }
    }

    public void bind() {
        this.getShaderProgram().bind();
    }

    public void unBind() {
        this.getShaderProgram().unbind();
    }

    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }

    public void performUniform(String uniform, Object o) {
        if (!this.uniformProgram.setUniform(uniform, o)) {
            Game.getGame().getLogManager().warn("Wrong arguments! U: " + uniform);
        }
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createFragmentShader(Utils.loadShader(this.renderGroup.getPath() + "/fragment.frag"));
        this.shaderProgram.createVertexShader(Utils.loadShader(this.renderGroup.getPath() + "/vertex.vert"));
        this.shaderProgram.link();
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        if (this.uniformsSet.isEmpty()) {
            Game.getGame().getLogManager().warn("Warning! No Uniforms found in: " + this.renderGroup.getPath() + " (Group)");
        }
        for (String s : this.uniformsSet) {
            this.uniformProgram.createUniform(s);
        }
    }
}
