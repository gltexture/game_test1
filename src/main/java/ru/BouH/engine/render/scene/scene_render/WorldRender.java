package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

public class WorldRender extends SceneRenderBase {
    private final CubeMapProgram cubeEnvironmentTexture;

    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD", true));
        this.cubeEnvironmentTexture = this.getSceneWorld().getEnvironment().getSky().getSkyBox().getCubeMap();
    }

    public void onRender(double partialTicks) {
        for (PhysicsObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
            if (entityItem.isHasRender()) {
                entityItem.getShaderManager().bind();
                entityItem.getShaderManager().getUtils().performProjectionMatrix();
                entityItem.getShaderManager().performUniform(UniformConstants.dimensions, Game.getGame().getScreen().getWindow().getWindowDimensions());
                if (entityItem.isHasModel()) {
                    this.setRenderTranslation(entityItem);
                }
                if (entityItem.isVisible()) {
                    entityItem.getShaderManager().getUtils().performProperties(entityItem.getRenderData().getRenderProperties());
                    entityItem.getShaderManager().getUtils().setCubeMapTexture(2, this.getCubeEnvironmentTexture());
                    GL30.glEnable(GL30.GL_DEPTH_TEST);
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    GL30.glDisable(GL30.GL_DEPTH_TEST);
                }
                entityItem.getShaderManager().unBind();
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void setRenderTranslation(PhysicsObject physicsObject) {
        Mesh<Format3D> mesh = physicsObject.getModel3D();
        mesh.getFormat().getScale().set(physicsObject.getScale());
        mesh.getFormat().getPosition().set(physicsObject.getRenderPosition());
        mesh.getFormat().getRotation().set(physicsObject.getRenderRotation());
    }

    public CubeMapProgram getCubeEnvironmentTexture() {
        return this.cubeEnvironmentTexture;
    }
}