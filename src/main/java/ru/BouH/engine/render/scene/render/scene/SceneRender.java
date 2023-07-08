package ru.BouH.engine.render.scene.render.scene;

import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.proxy.init.EntitiesInit;
import ru.BouH.engine.proxy.lights.*;
import ru.BouH.engine.proxy.lights.env.DirectionalLight;
import ru.BouH.engine.proxy.lights.env.PointLight;
import ru.BouH.engine.proxy.lights.env.SpotLight;
import ru.BouH.engine.render.scene.components.AmbientMaterial;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.scene.render.entities.CollisionRenderer;
import ru.BouH.engine.render.scene.render.entities.init.RenderEntity;
import ru.BouH.engine.render.scene.render.world.RenderTerrain;
import ru.BouH.engine.render.scene.world.RenderWorld;
import ru.BouH.engine.render.scene.world.render.RenderManager;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.utils.Utils;

public class SceneRender {
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private final RenderWorld renderWorld;
    private final RenderTerrain renderTerrain;
    private final CollisionRenderer collisionRenderer;

    public SceneRender(RenderWorld renderWorld) {
        this.renderWorld = renderWorld;
        this.renderTerrain = new RenderTerrain(renderWorld.getWorld().getTerrain());
        this.collisionRenderer = new CollisionRenderer(renderWorld);
        Game.getGame().getLogManager().log("Scene init");
    }

    public void onRender(double partialTicks) {
        EntityPlayerSP entityPlayerSP = this.renderWorld.getWorld().getLocalPlayer();
        this.shaderProgram.bind();
        this.uniformProgram.setUniform("projection_matrix", this.renderWorld.getRenderManager().getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR));
        for (int i = 0; i < RenderManager.MAX_POINT_LIGHTS; i++) {
            if (this.renderWorld.getPointLightList().size() > i) {
                PointLight pointLight = this.renderWorld.getPointLightList().get(i);
                this.setPointLightUniform(LightType.POINT_LIGHT.getId() + "[" + i + "]", pointLight);
            } else {
                this.setPointLightUniform(LightType.POINT_LIGHT.getId() + "[" + i + "]", null);
            }
        }
        for (int i = 0; i < RenderManager.MAX_SPOT_LIGHTS; i++) {
            if (this.renderWorld.getSpotLightList().size() > i) {
                SpotLight spotLight = this.renderWorld.getSpotLightList().get(i);
                this.setSpotLightUniform(LightType.SPOT_LIGHT.getId() + "[" + i + "]", spotLight);
            } else {
                this.setSpotLightUniform(LightType.SPOT_LIGHT.getId() + "[" + i + "]", null);
            }
        }
        this.setDirectionalLightUniform("directional_light", this.renderWorld.getDirectionalLight());
        for (RenderEntity renderEntity : this.renderWorld.getRenderEntitySet()) {
            PhysEntity physEntity = renderEntity.getEntity();
            renderEntity.getEntityModel().getMeshModel().setScale(physEntity.getScale());
            renderEntity.getEntityModel().getMeshModel().setPosition(physEntity.getPosition().x, physEntity.getPosition().y, physEntity.getPosition().z);
            renderEntity.getEntityModel().getMeshModel().setRotation(physEntity.getRotation().x, physEntity.getRotation().y, physEntity.getRotation().z);
            this.uniformProgram.setUniform("model_view_matrix", this.renderWorld.getRenderManager().getTransform().getModelViewMatrix(renderEntity.getEntityModel().getMeshModel(), this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera())));
            renderEntity.getEntityModel().getRender().onRender(renderEntity);
        }
        if (Game.DEBUG) {
            for (PhysEntity physEntity : this.renderWorld.getWorld().getEntitySet()) {
                //this.collisionRenderer.renderHitBox(physEntity);
            }
        }
        this.uniformProgram.setUniform("ambient_light", new Vector3d(0.2f, 0.2f, 0.2f));
        this.uniformProgram.setUniform("model_view_matrix", this.renderWorld.getRenderManager().getTransform().getModelViewMatrix(renderTerrain.getMesh(), this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera())));
        this.renderTerrain.onRender(this);
        Vector3d camPos = new Vector3d(entityPlayerSP.getPosition().x, entityPlayerSP.getEyeHeight(), entityPlayerSP.getPosition().z);
        this.renderWorld.getCamera().getCamPosition().lerp(camPos, partialTicks);
        this.renderWorld.getCamera().setCamRotation(this.renderWorld.getWorld().getLocalPlayer().getRotation().x, this.renderWorld.getWorld().getLocalPlayer().getRotation().y, this.renderWorld.getWorld().getLocalPlayer().getRotation().z);
        this.shaderProgram.unbind();
    }

    public void onStartRender() {
        this.initShaders(new ShaderProgram());
        this.renderTerrain.onStartRender();
    }

    public void onStopRender() {
        this.renderTerrain.onStopRender();
        for (RenderEntity renderEntity : this.renderWorld.getRenderEntitySet()) {
            renderEntity.getEntityModel().getRender().onStartRender(renderEntity);
        }
        if (this.shaderProgram != null) {
            this.shaderProgram.clean();
        }
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createFragmentShader(Utils.loadShader("world/fragment.frag"));
        this.shaderProgram.createVertexShader(Utils.loadShader("world/vertex.vert"));
        this.shaderProgram.link();
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        this.uniformProgram.createUniform("disable_light");
        this.uniformProgram.createUniform("ambient_light");
        this.uniformProgram.createUniform("projection_matrix");
        this.uniformProgram.createUniform("model_view_matrix");
        this.uniformProgram.createUniform("texture_sampler");
        this.uniformProgram.createUniform("colour");
        this.uniformProgram.createUniform("use_texture");
        this.uniformProgram.createUniform("specular_power");
        this.createMaterialUniform("material");
        this.createDirectionalLightUniform("directional_light");
        for (int i = 0; i < RenderManager.MAX_POINT_LIGHTS; i++) {
            this.createPointLightUniform(LightType.POINT_LIGHT.getId() + "[" + i + "]");
        }
        for (int i = 0; i < RenderManager.MAX_SPOT_LIGHTS; i++) {
            this.createSpotLightUniform(LightType.SPOT_LIGHT.getId() + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) {
        this.uniformProgram.createUniform(uniformName + ".colour");
        this.uniformProgram.createUniform(uniformName + ".pos");
        this.uniformProgram.createUniform(uniformName + ".intensity");
        this.uniformProgram.createUniform(uniformName + ".at.constant");
        this.uniformProgram.createUniform(uniformName + ".at.linear");
        this.uniformProgram.createUniform(uniformName + ".at.exp");
    }

    public void createSpotLightUniform(String uniformName) {
        this.createPointLightUniform(uniformName + ".spl");
        this.uniformProgram.createUniform(uniformName + ".cone");
        this.uniformProgram.createUniform(uniformName + ".cut");
    }

    public void createDirectionalLightUniform(String uniformName) {
        this.uniformProgram.createUniform(uniformName + ".colour");
        this.uniformProgram.createUniform(uniformName + ".direction");
        this.uniformProgram.createUniform(uniformName + ".intensity");
    }

    public void setDirectionalLightUniform(String uniformName, DirectionalLight directionalLight) {
        if (directionalLight == null) {
            this.uniformProgram.setUniform(uniformName + ".intensity", 0.0f);
        } else {
            this.uniformProgram.setUniform(uniformName + ".colour", directionalLight.getColour());
            this.uniformProgram.setUniform(uniformName + ".direction", directionalLight.getDirection());
            this.uniformProgram.setUniform(uniformName + ".intensity", (float) directionalLight.getIntensity());
        }
    }

    public void createMaterialUniform(String uniformName) {
        this.uniformProgram.createUniform(uniformName + ".ambient");
        this.uniformProgram.createUniform(uniformName + ".specular");
        this.uniformProgram.createUniform(uniformName + ".reflect");
    }

    public void setPointLightUniform(String uniformName, PointLight pointLight) {
        if (pointLight == null) {
            this.uniformProgram.setUniform(uniformName + ".intensity", 0.0f);
        } else {
            this.uniformProgram.setUniform(uniformName + ".colour", pointLight.getColour());
            this.uniformProgram.setUniform(uniformName + ".pos", pointLight.getPosition());
            this.uniformProgram.setUniform(uniformName + ".intensity", (float) pointLight.getIntensity());
            Attenuation at = pointLight.getAttenuation();
            this.uniformProgram.setUniform(uniformName + ".at.constant", at.getConstant());
            this.uniformProgram.setUniform(uniformName + ".at.linear", at.getLinear());
            this.uniformProgram.setUniform(uniformName + ".at.exp", at.getExponent());
        }
    }

    public void setSpotLightUniform(String uniformName, SpotLight spotLight) {
        if (spotLight == null) {
            this.uniformProgram.setUniform(uniformName + ".spl.intensity", 0.0f);
        } else {
            this.uniformProgram.setUniform(uniformName + ".spl.colour", spotLight.getColour());
            this.uniformProgram.setUniform(uniformName + ".spl.pos", spotLight.getPosition());
            this.uniformProgram.setUniform(uniformName + ".spl.intensity", (float) spotLight.getIntensity());
            Attenuation at = spotLight.getAttenuation();
            this.uniformProgram.setUniform(uniformName + ".spl.at.constant", at.getConstant());
            this.uniformProgram.setUniform(uniformName + ".spl.at.linear", at.getLinear());
            this.uniformProgram.setUniform(uniformName + ".spl.at.exp", at.getExponent());
            this.uniformProgram.setUniform(uniformName + ".cone", spotLight.getConeDirection());
            this.uniformProgram.setUniform(uniformName + ".cut", spotLight.getCutOff());
        }
    }

    public void setMaterialUniform(String uniformName, AmbientMaterial ambientMaterial) {
        this.uniformProgram.setUniform(uniformName + ".ambient", ambientMaterial.getAmbientColor());
        this.uniformProgram.setUniform(uniformName + ".specular", ambientMaterial.getSpecularColor());
        this.uniformProgram.setUniform(uniformName + ".reflect", ambientMaterial.getReflectance());
    }

    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    public UniformProgram getUniformProgram() {
        return this.uniformProgram;
    }
}
