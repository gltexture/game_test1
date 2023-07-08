package ru.BouH.engine.render.scene.render.scene;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.scene.world.RenderWorld;
import ru.BouH.engine.render.scene.world.environment.sky.SkyBox;
import ru.BouH.engine.render.scene.world.render.RenderManager;
import ru.BouH.engine.render.utils.Utils;

public class SkyRender {
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private final RenderWorld renderWorld;
    private SkyBox skyBox;

    public SkyRender(RenderWorld renderWorld) {
        this.renderWorld = renderWorld;
        Game.getGame().getLogManager().log("SkyBox init");
    }

    public void onRender(double partialTicks) {
        if (this.getSkyBox() != null) {
            this.shaderProgram.bind();
            this.uniformProgram.setUniform("projection_matrix", this.renderWorld.getRenderManager().getTransform().getProjectionMatrix(RenderManager.FOV, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), RenderManager.Z_NEAR, RenderManager.Z_FAR));
            Matrix4d matrix4d = this.renderWorld.getRenderManager().getTransform().getModelViewMatrix(this.getSkyBox().getModel3DInfo(), this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera()));
            matrix4d.m30(0);
            matrix4d.m31(0);
            matrix4d.m32(0);
            this.uniformProgram.setUniform("model_view_matrix", matrix4d);
            this.uniformProgram.setUniform("ambient", new Vector3d(1, 1, 1));
            GL30.glBindVertexArray(this.getSkyBox().getModel3DInfo().getMesh().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.uniformProgram.setUniform("texture_sampler", 0);
            this.getSkyBox().getTexture().performTexture();
            GL30.glDrawElements(GL30.GL_TRIANGLES, this.getSkyBox().getModel3DInfo().getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
            this.shaderProgram.unbind();
        }
    }

    public void onStartRender() {
        this.initShaders(new ShaderProgram());
        SkyBox skyBox1 = new SkyBox("skybox1.png");
        this.setSkyBox(skyBox1);
    }

    public void onStopRender() {
        if (this.shaderProgram != null) {
            this.shaderProgram.clean();
        }
    }

    public RenderWorld getRenderWorld() {
        return this.renderWorld;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createFragmentShader(Utils.loadShader("skybox/fragment.frag"));
        this.shaderProgram.createVertexShader(Utils.loadShader("skybox/vertex.vert"));
        this.shaderProgram.link();
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        this.uniformProgram.createUniform("projection_matrix");
        this.uniformProgram.createUniform("model_view_matrix");
        this.uniformProgram.createUniform("texture_sampler");
        this.uniformProgram.createUniform("ambient");
    }
}
