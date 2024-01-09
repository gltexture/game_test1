package ru.BouH.engine.game.resource.assets;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.assets.shaders.Shader;
import ru.BouH.engine.game.resource.assets.shaders.ShaderGroup;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.game.resource.assets.shaders.UniformBufferObject;
import ru.BouH.engine.render.environment.light.LightManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShaderAssets implements IAssets {
    public static final List<ShaderManager> allShaders = new ArrayList<>();
    public static final List<UniformBufferObject> allUniformBuffers = new ArrayList<>();

    public final UniformBufferObject SunLight;
    public final UniformBufferObject PointLights;
    public final UniformBufferObject Misc;

    public final ShaderManager gameUbo;
    public final ShaderManager guiShader;
    public final ShaderManager post_blur;
    public final ShaderManager post_render_1;
    public final ShaderManager skybox;
    public final ShaderManager world;
    public final ShaderManager debug;

    public ShaderAssets() {
        this.SunLight = this.createUBO("SunLight", 0, 20);
        this.PointLights = this.createUBO("PointLights", 1, 32 * LightManager.MAX_POINT_LIGHTS);
        this.Misc = this.createUBO("Misc", 2, 4);

        this.debug = this.createShaderManager("debug", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.guiShader = this.createShaderManager("gui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_blur = this.createShaderManager("post_blur", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_render_1 = this.createShaderManager("post_render_1", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);
        this.skybox = this.createShaderManager("skybox", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight);
        this.world = this.createShaderManager("world", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights);

        this.gameUbo = this.createShaderManager("gameubo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights);
    }

    public UniformBufferObject createUBO(String id, int binding, int bsize) {
        UniformBufferObject uniformBufferObject = new UniformBufferObject(id, binding, bsize);
        ShaderAssets.allUniformBuffers.add(uniformBufferObject);
        return uniformBufferObject;
    }

    public ShaderManager createShaderManager(String shader, int types) {
        Game.getGame().getLogManager().log("Creating shader " + shader);
        ShaderManager shaderManager = new ShaderManager(new ShaderGroup(shader, types));
        ShaderAssets.allShaders.add(shaderManager);
        return shaderManager;
    }

    public void startShaders() {
        Game.getGame().getLogManager().log("Compiling shaders!");
        for (ShaderManager shaderManager : ShaderAssets.allShaders) {
            shaderManager.startProgram();
        }
    }

    public void destroyShaders() {
        Game.getGame().getLogManager().log("Destroying shaders!");
        for (ShaderManager shaderManager : ShaderAssets.allShaders) {
            shaderManager.destroyProgram();
        }
    }

    public void load() {
        for (ShaderManager shaderManager : ShaderAssets.allShaders) {
            if (shaderManager.getShaderGroup().getFragmentShader() != null) {
                shaderManager.getShaderGroup().initAll();
            }
        }
    }

    @Override
    public boolean parallelLoading() {
        return true;
    }
}
