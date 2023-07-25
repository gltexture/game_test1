package ru.BouH.engine.render.scene.renderers.main_render;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.renderers.main_render.base.RenderGroup;
import ru.BouH.engine.render.scene.renderers.main_render.base.SceneRenderBase;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.environment.sky.SkyBox;
import ru.BouH.engine.render.RenderManager;

public class SkyRender extends SceneRenderBase {
    private final SceneWorld sceneWorld;
    private SkyBox skyBox;

    public SkyRender(SceneWorld sceneWorld) {
        super(0, sceneWorld, RenderGroup.SKYBOX);
        this.addUniform("projection_matrix");
        this.addUniform("model_view_matrix");
        this.addUniform("texture_sampler");
        this.addUniform("ambient");
        this.sceneWorld = sceneWorld;
    }

    public void onRender(double partialTicks) {
        if (this.getSkyBox() != null) {
            this.performUniform("projection_matrix", this.sceneWorld.getRenderManager().getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR));
            Matrix4d matrix4d = this.sceneWorld.getRenderManager().getTransform().getModelViewMatrix(this.getSkyBox().getModel3DInfo(), this.sceneWorld.getRenderManager().getTransform().getViewMatrix(this.sceneWorld.getCamera()));
            matrix4d.m30(0);
            matrix4d.m31(0);
            matrix4d.m32(0);
            this.performUniform("model_view_matrix", matrix4d);
            this.performUniform("ambient", new Vector3d(1, 1, 1));
            GL30.glBindVertexArray(this.getSkyBox().getModel3DInfo().getModel3D().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.performUniform("texture_sampler", 0);
            this.getSkyBox().getTexture().performTexture();
            GL30.glDrawElements(GL30.GL_TRIANGLES, this.getSkyBox().getModel3DInfo().getModel3D().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

    public void onStartRender() {
        super.onStartRender();
        SkyBox skyBox1 = new SkyBox("skybox1.png");
        this.setSkyBox(skyBox1);
    }


    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
}
