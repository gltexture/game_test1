package ru.BouH.engine.render.scene.programs.shaders;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.assets.utils.AssetsHelper;
import ru.BouH.engine.math.IntPair;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderManager {
    private final Set<String> uniformsSet;
    private final Map<String, IntPair> uniformBuffersSet;
    private final Map<String, UniformBufferProgram> uniformBufferProgramMap;
    private final String path;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;

    public ShaderManager(RenderGroup renderGroup) {
        this(renderGroup.getPath());
    }

    public ShaderManager(String path) {
        this.uniformsSet = new HashSet<>();
        this.uniformBuffersSet = new HashMap<>();
        this.uniformBufferProgramMap = new HashMap<>();
        this.path = path;
    }

    public boolean checkUniform(String u) {
        return this.uniformsSet.contains(u);
    }

    @SuppressWarnings("all")
    public boolean checkUniformBuffer(String u) {
        return this.uniformBuffersSet.containsKey(u);
    }

    public void addUniform(String s) {
        this.uniformsSet.add(s);
    }

    public void addUniformBuffer(UniformBufferUtils.UBO_DATA uboData) {
        this.uniformBuffersSet.put(uboData.getName(), uboData.getIntPair());
    }

    public void addUniformBuffer(String s, IntPair intPair) {
        this.uniformBuffersSet.put(s, intPair);
    }

    public int getUBOIndex(String name) {
        return this.getUniformBufferProgram(name).getLocation();
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

    public UniformBufferProgram getUniformBufferProgram(String name) {
        return this.uniformBufferProgramMap.get(name);
    }

    public void performUniform(String uniform, Object o) {
        if (!this.getUniformProgram().setUniform(uniform, o)) {
            Game.getGame().getLogManager().warn("[" + this.getPath() + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniformBuffer(String uniform, ByteBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(String uniform, FloatBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(String uniform, float[] data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(String uniform, int offset, ByteBuffer data) {
        this.getUniformBufferProgram(uniform).setUniformBufferData(offset, data);
    }

    public void performUniformBuffer(String uniform, int offset, FloatBuffer data) {
        this.getUniformBufferProgram(uniform).setUniformBufferData(offset, data);
    }

    public void performUniformBuffer(String uniform, int offset, float[] data) {
        this.getUniformBufferProgram(uniform).setUniformBufferData(offset, data);
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createVertexShader(AssetsHelper.loadShader(this.getPath() + "/vertex.vert"));
        this.shaderProgram.createFragmentShader(AssetsHelper.loadShader(this.getPath() + "/fragment.frag"));
        shaderProgram.link();
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
        this.initUniformBuffers(this.uniformBuffersSet);
    }

    private String getPath() {
        return this.path;
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        if (this.uniformsSet.isEmpty()) {
            Game.getGame().getLogManager().warn("Warning! No Uniforms found in: " + this.getPath());
        }
        for (String s : this.uniformsSet) {
            if (!this.getUniformProgram().createUniform(s)) {
                Game.getGame().getLogManager().warn("[" + this.getPath() + "] Could not find uniform " + s);
            }
        }
    }

    private void initUniformBuffers(Map<String, IntPair> bufferPrograms) {
        for (Map.Entry<String, IntPair> s : bufferPrograms.entrySet()) {
            String name = s.getKey();
            IntPair intPair = s.getValue();
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(this.shaderProgram.getProgramId(), name);
            uniformBufferProgram.createUniformBuffer(intPair.getA1(), intPair.getA2());
            this.uniformBufferProgramMap.put(name, uniformBufferProgram);
        }
    }
}
