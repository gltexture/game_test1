package ru.BouH.engine.render.scene.render.scene;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.gui.AbstractGui;
import ru.BouH.engine.render.gui.hud.font.GuiText;
import ru.BouH.engine.render.scene.programs.ShaderProgram;
import ru.BouH.engine.render.scene.programs.UniformProgram;
import ru.BouH.engine.render.scene.world.RenderWorld;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GuiRender {
    private ShaderProgram shaderProgram;
    private UniformProgram uniformProgram;
    private final List<AbstractGui> guiList = new ArrayList<>();
    private final RenderWorld renderWorld;
    private final GuiText guiText = this.createText("debug", 0, 0, 0);
    private final GuiText guiText2 = this.createText("", 0, 0, 0);

    public GuiRender(RenderWorld renderWorld) {
        this.renderWorld = renderWorld;
        Game.getGame().getLogManager().log("Gui init");
    }

    public GuiText createText(String text, int x, int y, int z) {
        GuiText guiText = new GuiText(text, z);
        guiText.getModel2DInfo().setPosition(x, y);
        this.getGuiList().add(guiText);
        Game.getGame().getLogManager().log("Added new gui(text) element");
        return guiText;
    }

    public void onRender(double partialTicks) {
        this.shaderProgram.bind();
        EntityPlayerSP entityPlayerSP = this.getRenderWorld().getWorld().getLocalPlayer();
        this.guiText.setText((int) entityPlayerSP.getPosition().x + " " + (int) entityPlayerSP.getPosition().y + " " + (int) entityPlayerSP.getPosition().z);
        this.guiText2.setText(String.valueOf(Screen.FPS));
        this.guiText2.getModel2DInfo().setPosition(Game.getGame().getScreen().getWindow().getWidth() - this.guiText2.getWidth(), 0);
        Matrix4d matrix4d1 = this.getRenderWorld().getRenderManager().getTransform().getOrthographicMatrix(0, Game.getGame().getScreen().getWindow().getWidth(), Game.getGame().getScreen().getWindow().getHeight(), 0);
        for (AbstractGui abstractGui : this.getGuiList()) {
            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
            Matrix4d matrix4d = this.getRenderWorld().getRenderManager().getTransform().getOrthoModelMatrix(abstractGui.getModel2DInfo(), matrix4d1);
            this.uniformProgram.setUniform("projection_model_matrix", matrix4d);
            this.uniformProgram.setUniform("colour", new Vector4d(1, 1, 1, 1));
            abstractGui.render();
            GL30.glDisable(GL30.GL_BLEND);
        }
        this.shaderProgram.unbind();
    }

    public void onStartRender() {
        this.initShaders(new ShaderProgram());
    }

    public void onStopRender() {
        if (this.shaderProgram != null) {
            this.shaderProgram.clean();
        }
    }

    public RenderWorld getRenderWorld() {
        return this.renderWorld;
    }

    private void initShaders(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.shaderProgram.createFragmentShader(Utils.loadShader("gui/fragment.frag"));
        this.shaderProgram.createVertexShader(Utils.loadShader("gui/vertex.vert"));
        this.shaderProgram.link();
        this.initUniforms(new UniformProgram(this.shaderProgram.getProgramId()));
    }

    private void initUniforms(UniformProgram uniformProgram) {
        this.uniformProgram = uniformProgram;
        this.uniformProgram.createUniform("projection_model_matrix");
        this.uniformProgram.createUniform("colour");
    }

    public List<AbstractGui> getGuiList() {
        return this.guiList;
    }
}
