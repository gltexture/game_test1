package ru.BouH.engine.render.scene.scene_render;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.g_static.render.ItemRenderList;
import ru.BouH.engine.math.IntPair;
import ru.BouH.engine.render.scene.RenderGroup;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.texture.WorldItemTexture;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.environment.sky.SkyBox;
import ru.BouH.engine.render.RenderManager;

public class SkyRender extends SceneRenderBase {
    public SkyRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(0, sceneRenderConveyor, RenderGroup.SKYBOX);
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("cube_map_sampler");
        this.addUniformBuffer(UniformBufferUtils.UBO_SUN);
    }

    public void onRender(double partialTicks) {
        SkyBox skyBox = this.getSceneWorld().getEnvironment().getSky().getSkyBox();
        if (skyBox != null) {
            this.bindProgram();
            GL30.glDisable(GL30.GL_CULL_FACE);
            GL30.glDepthFunc(GL30.GL_LEQUAL);
            this.getUtils().performProjectionMatrix();
            Matrix4d matrix4d = RenderManager.instance.getModelViewMatrix(Game.getGame().getScreen().getCamera(), skyBox.getModel3DInfo());
            matrix4d.m30(0);
            matrix4d.m31(0);
            matrix4d.m32(0);
            this.getUtils().performModelViewMatrix3d(matrix4d);
            GL30.glBindVertexArray(skyBox.getModel3DInfo().getVao());
            GL30.glEnableVertexAttribArray(0);
            this.getUtils().setCubeMapTexture(skyBox.getCubeMap());
            GL30.glDrawElements(GL30.GL_TRIANGLES, skyBox.getModel3DInfo().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            this.unBindProgram();
            GL30.glDepthFunc(GL30.GL_LESS);
            GL30.glEnable(GL30.GL_CULL_FACE);
        }
    }
}
