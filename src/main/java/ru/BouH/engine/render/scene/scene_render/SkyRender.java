package ru.BouH.engine.render.scene.scene_render;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.environment.sky.SkyBox;
import ru.BouH.engine.render.RenderManager;

public class SkyRender extends SceneRenderBase {
    private final SceneWorld sceneWorld;

    public SkyRender(SceneWorld sceneWorld) {
        super(0, sceneWorld, RenderGroup.SKYBOX);
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("texture_sampler");
        this.addUniform("use_texture");

        this.addUniformBuffer("Lights", 150);
        this.sceneWorld = sceneWorld;
    }

    public void onRender(double partialTicks) {
        SkyBox skyBox = this.getRenderWorld().getEnvironment().getSky().getSkyBox();
        if (skyBox != null) {
            this.getUtils().performProjectionMatrix();
            Matrix4d matrix4d = RenderManager.instance.getModelViewMatrix(Game.getGame().getScreen().getCamera(), skyBox.getModel3DInfo());
            matrix4d.m30(0);
            matrix4d.m31(0);
            matrix4d.m32(0);
            this.getUtils().performModelViewMatrix3d(matrix4d);
            GL30.glBindVertexArray(skyBox.getModel3DInfo().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.getUtils().setTexture(WorldItemTexture.createItemTexture(skyBox.getTexture()));
            GL30.glDrawElements(GL30.GL_TRIANGLES, skyBox.getModel3DInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }


    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }
}
