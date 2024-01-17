package ru.BouH.engine.render.scene.scene_render;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;
import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;

public class WorldRender extends SceneRenderBase {
    private final CubeMapProgram cubeEnvironmentTexture;

    public WorldRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(1, sceneRenderConveyor, new RenderGroup("WORLD", true));
        this.cubeEnvironmentTexture = this.getSceneWorld().getEnvironment().getSky().getSkyBox().getCubeMap();
    }

    public void onRender(double partialTicks) {
        for (PhysicsObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
            if (entityItem.hasRender()) {
                entityItem.getShaderManager().bind();
                entityItem.getShaderManager().getUtils().performProjectionMatrix();
                if (entityItem.isHasModel()) {
                    this.setRenderTranslation(entityItem);
                }
                if (entityItem.isVisible()) {
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
        Model<Format3D> model = physicsObject.getModel3D();
        model.getFormat().getScale().set(physicsObject.getScale());
        model.getFormat().getPosition().set(physicsObject.getRenderPosition());
        model.getFormat().getRotation().set(physicsObject.getRenderRotation());
    }

    public CubeMapProgram getCubeEnvironmentTexture() {
        return this.cubeEnvironmentTexture;
    }
}