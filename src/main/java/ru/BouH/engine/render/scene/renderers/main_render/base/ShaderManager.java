package ru.BouH.engine.render.scene.renderers.main_render.base;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderManager {
    private final Set<String> uniformsSet;
    private final Map<String, Integer> uniformBuffersSet;
    private final RenderGroup renderGroup;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private UniformBufferProgram uniformBufferProgram;

    public ShaderManager(RenderGroup renderGroup) {
        this.uniformsSet = new HashSet<>();
        this.uniformBuffersSet = new HashMap<>();
        this.renderGroup = renderGroup;
    }

    public boolean checkUniform(String u) {
        return this.uniformsSet.contains(u);
    }

    public boolean checkUniformBuffer(String u) {
        return this.uniformBuffersSet.containsKey(u);
    }

    public void addUniform(String s) {
        this.uniformsSet.add(s);
    }

    public void addUniformBuffer(String s, int sizeBytes) {
        this.uniformBuffersSet.put(s, sizeBytes);
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

    public UniformBufferProgram getUniformBufferProgram() {
        return this.uniformBufferProgram;
    }

    public void performUniform(String uniform, Object o) {
        if (!this.getUniformProgram().setUniform(uniform, o)) {
            Game.getGame().getLogManager().warn("Wrong arguments! U: " + uniform);
        }
    }

    public void performUniformBuffer(String uniform, float[] data) {
        this.getUniformBufferProgram().setUniformBufferData(uniform, data);
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
            this.getUniformProgram().createUniform(s);
        }
        if (!this.uniformBuffersSet.isEmpty()) {
            this.initUniformBuffers(new UniformBufferProgram());
        }
    }

    private void initUniformBuffers(UniformBufferProgram uniformBufferProgram) {
        this.uniformBufferProgram = uniformBufferProgram;
        for (Map.Entry<String, Integer> s : this.uniformBuffersSet.entrySet()) {
            this.getUniformBufferProgram().createUniformBuffer(s.getKey(), s.getValue());
        }
    }
}
