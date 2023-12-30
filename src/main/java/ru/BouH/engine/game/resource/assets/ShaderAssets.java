package ru.BouH.engine.game.resource.assets;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.programs.shaders.Shader;

import java.util.HashSet;
import java.util.Set;

public class ShaderAssets implements IAssets {
    public static final Set<ShaderGroup> allShaders = new HashSet<>();

    public final ShaderGroup guiShader;
    public final ShaderGroup post_blur;
    public final ShaderGroup post_render_1;
    public final ShaderGroup skybox;
    public final ShaderGroup world;

    public ShaderAssets() {
        Game.getGame().getLogManager().log("Shader loader!");
        this.guiShader = this.createShaderGroup("gui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_blur = this.createShaderGroup("post_blur", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_render_1 = this.createShaderGroup("post_render_1", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.skybox = this.createShaderGroup("skybox", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world = this.createShaderGroup("world", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
    }

    public ShaderGroup createShaderGroup(String shader, int types) {
        Game.getGame().getLogManager().log("Creating shader " + shader);
        ShaderGroup shaderGroup = new ShaderGroup(shader, types);
        ShaderAssets.allShaders.add(shaderGroup);
        return shaderGroup;
    }

    public void load() {
        for (ShaderGroup shaderGroup : ShaderAssets.allShaders) {
            if (shaderGroup.getFragmentShader() != null) {
                Game.getGame().getLogManager().log("Initializing " + shaderGroup.getFragmentShader().getShaderName() + shaderGroup.getFragmentShader().getShaderType().getFile());
                shaderGroup.getFragmentShader().init();
            }
            if (shaderGroup.getVertexShader() != null) {
                Game.getGame().getLogManager().log("Initializing " + shaderGroup.getVertexShader().getShaderName() + shaderGroup.getVertexShader().getShaderType().getFile());
                shaderGroup.getVertexShader().init();
            }
            if (shaderGroup.getGeometricShader() != null) {
                Game.getGame().getLogManager().log("Initializing " + shaderGroup.getGeometricShader().getShaderName() + shaderGroup.getGeometricShader().getShaderType().getFile());
                shaderGroup.getGeometricShader().init();
            }
        }
    }

    @Override
    public boolean parallelLoading() {
        return true;
    }

    public static class ShaderGroup {
        private final Shader vertexShader;
        private final Shader fragmentShader;
        private final Shader geometricShader;

        private ShaderGroup(String shader, int types) {
            Shader geometricShader1 = null;
            Shader vertexShader1 = null;
            Shader fragmentShader1 = null;
            if ((types & Shader.ShaderType.FRAGMENT_BIT) != 0) {
                fragmentShader1 = new Shader(Shader.ShaderType.FRAGMENT, shader);
            }
            if ((types & Shader.ShaderType.VERTEX_BIT) != 0) {
                vertexShader1 = new Shader(Shader.ShaderType.VERTEX, shader);
            }
            if ((types & Shader.ShaderType.GEOMETRIC_BIT) != 0) {
                geometricShader1 = new Shader(Shader.ShaderType.GEOMETRIC, shader);
            }
            this.vertexShader = vertexShader1;
            this.fragmentShader = fragmentShader1;
            this.geometricShader = geometricShader1;
        }

        public Shader getFragmentShader() {
            return this.fragmentShader;
        }

        public Shader getGeometricShader() {
            return this.geometricShader;
        }

        public Shader getVertexShader() {
            return this.vertexShader;
        }
    }
}
