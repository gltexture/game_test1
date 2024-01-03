package ru.BouH.engine.game.resource.assets.shaders;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.texture.PictureSample;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformBufferProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ShaderManager {
    private final Map<UniformBufferObject, UniformBufferProgram> uniformBufferProgramMap;
    private final Set<UniformBufferObject> uniformBufferObjects;
    private final ShaderGroup shaderGroup;
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private final ShaderUtils shaderUtils;

    public ShaderManager(ShaderGroup shaderGroup) {
        this.shaderGroup = shaderGroup;
        this.shaderUtils = new ShaderUtils();
        this.uniformBufferProgramMap = new HashMap<>();
        this.uniformBufferObjects = new HashSet<>();
    }

    public ShaderManager copy() {
        return new ShaderManager(this.getShaderGroup());
    }

    private boolean checkUniformInGroup(String uniform) {
        for (Uniform u : this.getShaderGroup().getUniformsFullSet()) {
            if (u.getId().equals(uniform)) {
                return true;
            }
        }
        return false;
    }

    public ShaderUtils getUtils() {
        return this.shaderUtils;
    }

    public ShaderManager addUBO(UniformBufferObject uniformBufferObject) {
        this.getUniformBufferObjects().add(uniformBufferObject);
        return this;
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

    public ShaderGroup getShaderGroup() {
        return this.shaderGroup;
    }

    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }

    public UniformBufferProgram getUniformBufferProgram(@NotNull UniformBufferObject uniformBufferObject) {
        UniformBufferProgram uniformBufferProgram = this.uniformBufferProgramMap.get(uniformBufferObject);
        if (uniformBufferProgram == null) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown UBO " + uniformBufferObject);
        }
        return uniformBufferProgram;
    }

    public void performUniform(String uniform, int arrayPos, Object o) {
        if (!this.checkUniformInGroup(uniform)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Unknown uniform " + uniform);
            return;
        }
        if (arrayPos >= 0) {
            uniform += "[" + arrayPos + "]";
        }
        if (!this.getUniformProgram().setUniform(uniform, o)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Wrong arguments! U: " + uniform);
        }
    }

    public void performUniform(String uniform, Object o) {
        this.performUniform(uniform, -1, o);
    }

    public void performUniformBuffer(UniformBufferObject uniform, ByteBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniform, FloatBuffer data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniform, float[] data) {
        this.performUniformBuffer(uniform, 0, data);
    }

    public void performUniformBuffer(UniformBufferObject uniformBufferObject, int offset, ByteBuffer data) {
        UniformBufferProgram uniformBufferProgram = this.getUniformBufferProgram(uniformBufferObject);
        if (uniformBufferProgram != null) {
            uniformBufferProgram.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, int offset, FloatBuffer data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public void performUniformBuffer(UniformBufferObject uniform, int offset, float[] data) {
        UniformBufferProgram uniformBufferObject = this.getUniformBufferProgram(uniform);
        if (uniformBufferObject != null) {
            uniformBufferObject.setUniformBufferData(offset, data);
        }
    }

    public Set<UniformBufferObject> getUniformBufferObjects() {
        return this.uniformBufferObjects;
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createShader(this.getShaderGroup());
        if (shaderProgram.link()) {
            Game.getGame().getLogManager().log("Shader " + this.getShaderGroup().getId() + " successfully linked");
        } else {
            Game.getGame().getLogManager().error("Found problems in shader " + this.getShaderGroup().getId());
        }
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    @SuppressWarnings("all")
    private boolean tryCreateUniform(String value) {
        if (!this.getUniformProgram().createUniform(value)) {
            Game.getGame().getLogManager().warn("[" + this.getShaderGroup().getId() + "] Could not find uniform " + value);
            return false;
        }
        return true;
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        if (this.getShaderGroup().getUniformsFullSet().isEmpty()) {
            Game.getGame().getLogManager().warn("Warning! No Uniforms found in: " + this.getShaderGroup().getId());
        }
        for (Uniform uniform : this.getShaderGroup().getUniformsFullSet()) {
            if (uniform.getArraySize() > 1) {
                for (int i = 0; i < uniform.getArraySize(); i++) {
                    this.tryCreateUniform(uniform.getId() + "[" + i + "]");
                }
            } else {
                this.tryCreateUniform(uniform.getId());
            }
        }
        this.initUniformBuffers();
    }

    private void initUniformBuffers() {
        for (UniformBufferObject uniformBufferObject : this.getUniformBufferObjects()) {
            UniformBufferProgram uniformBufferProgram = new UniformBufferProgram(this.shaderProgram.getProgramId(), uniformBufferObject.getId());
            if (uniformBufferProgram.createUniformBuffer(uniformBufferObject.getBinding(), uniformBufferObject.getBufferSize())) {
                Game.getGame().getLogManager().log("[" + this.getShaderGroup().getId() + "] Linked UBO " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            } else {
                Game.getGame().getLogManager().error("[" + this.getShaderGroup().getId() + "] Couldn't link " + uniformBufferObject.getId() + " at " + uniformBufferObject.getBinding());
            }
            this.uniformBufferProgramMap.put(uniformBufferObject, uniformBufferProgram);
        }
    }

    public class ShaderUtils {
        public ShaderUtils() {

        }

        public void disableMsaa() {
            GL30.glDisable(GL30.GL_MULTISAMPLE);
        }

        public void enableMsaa() {
            GL30.glEnable(GL30.GL_MULTISAMPLE);
        }

        public void disableLight() {
            ShaderManager.this.performUniform(UniformConstants.enable_light, 0);
        }

        public void enableLight() {
            ShaderManager.this.performUniform(UniformConstants.enable_light, 1);
        }

        public void disableLightAndMsaa() {
            this.disableMsaa();
            this.disableLight();
        }

        public void enableLightAndMsaa() {
            this.enableMsaa();
            this.enableLight();
        }

        public void performProjectionMatrix() {
            ShaderManager.this.performUniform(UniformConstants.projection_matrix, RenderManager.instance.getProjectionMatrix());
        }

        public void performModelViewMatrix3d(Model3D model3D) {
            ShaderManager.this.performUniform(UniformConstants.model_view_matrix, RenderManager.instance.getModelViewMatrix(model3D));
        }

        public void performModelViewMatrix3d(Matrix4d matrix4d) {
            ShaderManager.this.performUniform(UniformConstants.model_view_matrix, matrix4d);
        }

        public void performViewMatrix3d(Matrix4d matrix4d) {
            ShaderManager.this.performUniform(UniformConstants.view_matrix, matrix4d);
        }

        public void performModelMatrix3d(Model3D model3D) {
            ShaderManager.this.performUniform(UniformConstants.model_matrix, RenderManager.instance.getModelMatrix(model3D));
        }

        public void performProperties(RenderData.RenderProperties renderProperties) {
            ShaderManager.this.performUniform(UniformConstants.texture_scaling, renderProperties.getTextureScaling());
            if (renderProperties.isLightExposed()) {
                this.enableLight();
            } else {
                this.disableLight();
            }
        }

        public void setNormalMap(WorldItemTexture worldItemTexture) {
            this.setNormalMap(1, worldItemTexture);
        }

        public void setNormalMap(int code, WorldItemTexture worldItemTexture) {
            if (worldItemTexture != null && worldItemTexture.hasNormalMap()) {
                PictureSample pngTexture = worldItemTexture.getNormalMap();
                ShaderManager.this.performUniform(UniformConstants.normal_map, code);
                ShaderManager.this.performUniform(UniformConstants.use_normal_map, 1);
                pngTexture.performTexture(GL30.GL_TEXTURE0 + code);
            } else {
                ShaderManager.this.performUniform(UniformConstants.use_normal_map, 0);
            }
        }

        public void setCubeMapTexture(CubeMapProgram cubeMapTexture) {
            this.setCubeMapTexture(GL30.GL_TEXTURE0, cubeMapTexture);
        }

        public void setCubeMapTexture(int code, CubeMapProgram cubeMapTexture) {
            if (cubeMapTexture == null) {
                Game.getGame().getLogManager().warn("CubeMap is NULL!");
                return;
            }
            ShaderManager.this.performUniform(UniformConstants.cube_map_sampler, code);
            GL30.glActiveTexture(GL30.GL_TEXTURE0 + code);
            GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP, cubeMapTexture.getTextureId());
        }

        public void setTexture(WorldItemTexture worldItemTexture) {
            this.setNormalMap(1, worldItemTexture);
            if (worldItemTexture.getSample() instanceof PictureSample) {
                this.setPngTexture(worldItemTexture, (PictureSample) worldItemTexture.getSample());
                return;
            }
            ShaderManager.this.performUniform(UniformConstants.use_texture, worldItemTexture.getRenderID());
            if (worldItemTexture.hasValueToPass()) {
                for (WorldItemTexture.PassUniValue passUniValue : worldItemTexture.getValues()) {
                    ShaderManager.this.performUniform(passUniValue.getName(), passUniValue.getO());
                }
            }
        }

        public void setPngTexture(WorldItemTexture worldItemTexture, PictureSample sample) {
            if (sample != null && sample.isValid()) {
                ShaderManager.this.performUniform(UniformConstants.use_texture, worldItemTexture.getRenderID());
                sample.performTexture(GL30.GL_TEXTURE0);
            } else {
                this.setTexture(WorldItemTexture.standardError);
            }
        }
    }
}
