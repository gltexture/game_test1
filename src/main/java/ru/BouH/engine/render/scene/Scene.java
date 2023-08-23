package ru.BouH.engine.render.scene;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.physx.world.object.WorldItem;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.programs.FrameBufferObjectProgram;
import ru.BouH.engine.render.scene.programs.ShaderManager;
import ru.BouH.engine.render.scene.scene_render.GuiRender;
import ru.BouH.engine.render.scene.scene_render.SkyRender;
import ru.BouH.engine.render.scene.scene_render.WorldRender;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.window.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Scene {
    private final List<SceneRenderBase> sceneRenderBases;
    private final SceneWorld sceneWorld;
    private final SkyRender skyRender;
    private final GuiRender guiRender;
    private final WorldRender worldRender;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    private ICamera currentCamera;

    public Scene(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.frustumCulling = new FrustumCulling();
        this.sceneRenderBases = new ArrayList<>();
        this.skyRender = new SkyRender(sceneWorld);
        this.worldRender = new WorldRender(sceneWorld);
        this.guiRender = new GuiRender(sceneWorld);
        this.sceneRenderConveyor = new SceneRenderConveyor();
        this.currentCamera = null;
    }

    public void init() {
        this.sceneRenderBases.add(this.getSkyRender());
        this.sceneRenderBases.add(this.getEntityRender());
        this.sceneRenderBases.add(this.getGuiRender());
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public void setRenderCamera(ICamera camera) {
        this.currentCamera = camera;
    }

    public ICamera getCurrentCamera() {
        return this.currentCamera;
    }

    public WorldRender getEntityRender() {
        return this.worldRender;
    }

    public GuiRender getGuiRender() {
        return this.guiRender;
    }

    public SkyRender getSkyRender() {
        return this.skyRender;
    }

    public SceneWorld getRenderWorld() {
        return this.sceneWorld;
    }

    public void preRender() {
        this.attachCameraToLocalPlayer(Game.getGame().getProxy().getLocalPlayer());
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getMultiPassRenderConveyor().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully started!");
        }
        Game.getGame().getLogManager().log("Scene rendering started");
    }

    public void enableFreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        this.setRenderCamera(new FreeCamera(controller, pos, rot));
    }

    public void enableAttachedCamera(WorldItem worldItem) {
        this.setRenderCamera(new AttachedCamera(worldItem));
    }

    public void attachCameraToLocalPlayer(LocalPlayer localPlayer) {
        if (localPlayer != null && localPlayer.getEntityPlayerSP() != null) {
            this.setRenderCamera(new AttachedCamera(localPlayer.getEntityPlayerSP()));
        }
    }

    public boolean isCameraAttachedToItem(WorldItem worldItem) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getWorldItem() == worldItem;
    }

    public void renderScene(double partialTicks) {
        if (this.getCurrentCamera() != null) {
            this.getFrustumCulling().refreshFrustumCullingState(RenderManager.instance.getProjectionMatrix(), RenderManager.instance.getViewMatrix(this.getCurrentCamera()));
        }
        this.getMultiPassRenderConveyor().onRender(partialTicks, this.sortedList(this.getEntityRender(), this.getSkyRender()), this.sortedList(this.getGuiRender()));
        if (this.getCurrentCamera() != null) {
            this.getCurrentCamera().updateCamera(partialTicks);
        }
    }

    private List<SceneRenderBase> sortedList(SceneRenderBase... s) {
        List<SceneRenderBase> sceneRenderBases1 = new ArrayList<>(Arrays.asList(s));
        sceneRenderBases1.sort(Comparator.comparing(SceneRenderBase::getRenderPriority));
        return sceneRenderBases1;
    }

    public SceneRenderConveyor getMultiPassRenderConveyor() {
        return this.sceneRenderConveyor;
    }

    public void takeScreenshot() {
        this.getMultiPassRenderConveyor().takeScreenshot();
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void postRender() {
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        this.getMultiPassRenderConveyor().onStopRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully stopped!");
        }
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public static class SceneRenderConveyor {
        private final FrameBufferObjectProgram postProcessFBO;
        private final ShaderManager shaderManager;
        private boolean wantsTakeScreenshot;

        public SceneRenderConveyor() {
            Window window = Game.getGame().getScreen().getWindow();
            this.postProcessFBO = new FrameBufferObjectProgram(window);
            this.shaderManager = new ShaderManager("post_render_1");
            this.getShaderManager().addUniform("projection_model_matrix");
            this.getShaderManager().addUniform("texture_sampler");
        }

        public void onRender(double partialTicks, List<SceneRenderBase> mainList, List<SceneRenderBase> additionalList) {
            Vector2d v2 = Game.getGame().getScreen().getDimensions();
            Model2D model2D = this.genFrameBufferSquare((float) v2.x, (float) v2.y);
            FrameBufferObjectProgram frameBufferObjectProgram = this.getPostProcessFBO();
            frameBufferObjectProgram.createRenderBuffer(v2);

            frameBufferObjectProgram.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            for (SceneRenderBase sceneRenderBase : mainList) {
                sceneRenderBase.bindProgram();
                sceneRenderBase.onRender(partialTicks);
                sceneRenderBase.unBindProgram();
            }
            frameBufferObjectProgram.unBindFBO();

            this.getShaderManager().bind();
            this.getShaderManager().performUniform("projection_model_matrix", RenderManager.instance.getOrthoModelMatrix(model2D));
            this.getShaderManager().performUniform("texture_sampler", 0);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            frameBufferObjectProgram.bindTextureFBO();
            this.renderTexture(model2D);
            frameBufferObjectProgram.unBindTextureFBO();
            this.getShaderManager().unBind();

            for (SceneRenderBase sceneRenderBase : additionalList) {
                sceneRenderBase.bindProgram();
                sceneRenderBase.onRender(partialTicks);
                sceneRenderBase.unBindProgram();
            }
            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }
            frameBufferObjectProgram.clearFBO();
            model2D.clean();
        }

        private void writeBufferInFile() {
            Vector2d vector2d = Game.getGame().getScreen().getDimensions();
            int w = (int) vector2d.x;
            int h = (int) vector2d.y;
            int i1 = w * h;
            ByteBuffer p = ByteBuffer.allocateDirect(i1 * 4);
            GL30.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
            GL30.glReadPixels(0, 0, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, p);
            try {
                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                int[] pArray = new int[i1];
                p.asIntBuffer().get(pArray);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int i = (x + (w * y)) * 4;
                        int r = p.get(i) & 0xFF;
                        int g = p.get(i + 1) & 0xFF;
                        int b = p.get(i + 2) & 0xFF;
                        int a = p.get(i + 3) & 0xFF;
                        int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                        image.setRGB(x, (int) (vector2d.y - y - 1), rgb);
                    }
                }
                String builder = "screen.png";
                ImageIO.write(image, "PNG", new File(builder));
            } catch (IOException e) {
                Game.getGame().getLogManager().error(e.getMessage());
            }
        }

        private void renderTexture(Model2D model2D) {
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            GL30.glBindVertexArray(model2D.getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glDrawElements(GL30.GL_TRIANGLES, model2D.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glBindVertexArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
        }

        private Model2D genFrameBufferSquare(float w, float h) {
            List<Float> positions = new ArrayList<>();
            List<Float> textureCoordinates = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();

            positions.add(0.0f);
            positions.add(0.0f);
            positions.add(0.0f);
            textureCoordinates.add(0.0f);
            textureCoordinates.add(1.0f);

            positions.add(w);
            positions.add(0.0f);
            positions.add(0.0f);
            textureCoordinates.add(1.0f);
            textureCoordinates.add(1.0f);

            positions.add(0.0f);
            positions.add(h);
            positions.add(0.0f);
            textureCoordinates.add(0.0f);
            textureCoordinates.add(0.0f);

            positions.add(w);
            positions.add(h);
            positions.add(0.0f);
            textureCoordinates.add(1.0f);
            textureCoordinates.add(0.0f);

            indices.add(1);
            indices.add(2);
            indices.add(3);
            indices.add(0);
            indices.add(2);
            indices.add(1);

            float[] f1 = new float[positions.size()];
            int[] i1 = new int[indices.size()];
            float[] f2 = new float[textureCoordinates.size()];

            for (int i = 0; i < f1.length; i++) {
                f1[i] = positions.get(i);
            }

            for (int i = 0; i < i1.length; i++) {
                i1[i] = indices.get(i);
            }

            for (int i = 0; i < f2.length; i++) {
                f2[i] = textureCoordinates.get(i);
            }

            return new Model2D(new MeshModel(f1, i1, f2));
        }

        public FrameBufferObjectProgram getPostProcessFBO() {
            return this.postProcessFBO;
        }

        public void onStartRender() {
            this.getShaderManager().startProgram();
        }

        public void onStopRender() {
            this.getShaderManager().destroyProgram();
        }

        public ShaderManager getShaderManager() {
            return this.shaderManager;
        }

        public void takeScreenshot() {
            this.wantsTakeScreenshot = true;
        }
    }
}
