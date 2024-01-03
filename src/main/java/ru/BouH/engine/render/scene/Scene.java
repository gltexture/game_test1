package ru.BouH.engine.render.scene;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.FrameBufferObjectProgram;
import ru.BouH.engine.game.resource.assets.ShaderAssets;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.scene_render.GuiRender;
import ru.BouH.engine.render.scene.scene_render.SkyRender;
import ru.BouH.engine.render.scene.scene_render.WorldRender;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.window.Window;
import ru.BouH.engine.game.synchronizing.SyncManger;

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
    public static boolean testTrigger = false;

    private double elapsedTime;
    private final List<SceneRenderBase> sceneRenderBases;
    private final Screen screen;
    private final Window window;
    private final SceneWorld sceneWorld;
    private final SkyRender skyRender;
    private final GuiRender guiRender;
    private final WorldRender worldRender;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    private boolean refresh;
    private ICamera currentCamera;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.screen = screen;
        this.window = this.getScreen().getWindow();
        this.frustumCulling = new FrustumCulling();
        this.sceneRenderBases = new ArrayList<>();
        this.sceneRenderConveyor = new SceneRenderConveyor();
        this.skyRender = new SkyRender(this.getSceneRenderConveyor());
        this.worldRender = new WorldRender(this.getSceneRenderConveyor());
        this.guiRender = new GuiRender(this.getSceneRenderConveyor());
        this.currentCamera = null;
    }

    public static boolean isSceneActive() {
        return Screen.isScreenActive();
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public static int getPostRender() {
        return Game.getGame().getScreen().getScene().getRenderPostMode();
    }

    public static void setPostRender(int a) {
        Game.getGame().getScreen().getScene().setRenderPostMode(a);
    }

    public void init() {
        this.sceneRenderBases.add(this.getSkyRender());
        this.sceneRenderBases.add(this.getEntityRender());
        this.sceneRenderBases.add(this.getGuiRender());
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

    public Screen getScreen() {
        return this.screen;
    }

    public Vector2d getWindowDimensions() {
        return this.getWindow().getWindowDimensions();
    }

    public Window getWindow() {
        return this.window;
    }

    public void preRender() {
        ResourceManager.shaderAssets.startShaders();
        this.getRenderWorld().setFrustumCulling(this.getFrustumCulling());
        if (LocalPlayer.VALID_PL) {
            this.enableAttachedCamera(SceneWorld.PL);
        }
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getSceneRender().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully started!");
        }
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRenderConveyor().onWindowResize(dim);
    }

    public SceneRenderConveyor getSceneRenderConveyor() {
        return this.sceneRenderConveyor;
    }

    public int getRenderPostMode() {
        return this.getSceneRender().getPostRender();
    }

    public void setRenderPostMode(int a) {
        this.getSceneRender().setPostRender(a);
    }

    public ShaderManager getGameUboShader() {
        return ResourceManager.shaderAssets.gameUbo;
    }

    public void enableFreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        this.setRenderCamera(new FreeCamera(controller, pos, rot));
    }

    public void enableAttachedCamera(PhysicsObject physicsObject) {
        this.setRenderCamera(new AttachedCamera(physicsObject));
    }

    public boolean isCameraAttachedToItem(PhysicsObject physicsObject) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getPhysXObject() == physicsObject;
    }

    @SuppressWarnings("all")
    public void renderScene(double deltaTime) throws InterruptedException {
        if (Scene.isSceneActive()) {
            if (this.getCurrentCamera() != null) {
                this.elapsedTime += deltaTime / PhysicThreadManager.getFrameTime();
                if (this.elapsedTime > 1.0d) {
                    SyncManger.SyncPhysicsAndRender.mark();
                    this.refresh = true;
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.notifyAll();
                    }
                    this.elapsedTime %= 1.0d;
                }
                SyncManger.SyncPhysicsAndRender.blockCurrentThread();
                this.renderSceneInterpolated(this.elapsedTime);
            }
        }
    }

    @SuppressWarnings("all")
    public void renderSceneInterpolated(final double partialTicks) throws InterruptedException {
        this.getFrustumCulling().refreshFrustumCullingState(RenderManager.instance.getProjectionMatrix(), RenderManager.instance.getViewMatrix());
        this.getRenderWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        RenderManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks, this.sortedSceneList(this.getEntityRender(), this.getSkyRender()), this.sortedSceneList(this.getGuiRender()));
        this.refresh = false;
    }

    private List<SceneRenderBase> sortedSceneList(SceneRenderBase... s) {
        List<SceneRenderBase> sceneRenderBases1 = new ArrayList<>(Arrays.asList(s));
        sceneRenderBases1.sort(Comparator.comparing(SceneRenderBase::getRenderPriority));
        return sceneRenderBases1;
    }

    public SceneRenderConveyor getSceneRender() {
        return this.sceneRenderConveyor;
    }

    public void takeScreenshot() {
        this.getSceneRender().takeScreenshot();
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void postRender() {
        Game.getGame().getLogManager().log("Stopping scene rendering: ");
        this.getSceneRender().onStopRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully stopped!");
        }
        ResourceManager.shaderAssets.destroyShaders();
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public class SceneRenderConveyor {
        private final FrameBufferObjectProgram fboBlur;
        private final FrameBufferObjectProgram sceneFbo;
        private final float[] blurKernel;
        private int CURRENT_POST_RENDER = 0;
        private boolean wantsTakeScreenshot;

        public SceneRenderConveyor() {
            this.fboBlur = new FrameBufferObjectProgram();
            this.sceneFbo = new FrameBufferObjectProgram();
            this.blurKernel = this.blurKernels(8.0f, 7);
            this.initShaders();
        }

        private void initShaders() {
            this.attachFBO(new Vector2i((int) this.getWindowDimensions().x, (int) this.getWindowDimensions().y));
        }

        private float[] blurKernels(float sigma, int kernelC) {
            float[] kernel = new float[kernelC];
            float weight = 0.0f;

            for (int i = 0; i < kernelC; i++) {
                float x = i - (kernelC / 2.0f);
                kernel[i] = (float) Math.exp(-(x * x) / (2.0f * sigma * sigma));
                weight += kernel[i];
            }

            for (int i = 0; i < kernelC; i++) {
                kernel[i] /= weight;
            }

            return kernel;
        }

        private void attachFBO(Vector2i dim) {
            this.fboBlur.createFBO(dim, GL30.GL_SRGB_ALPHA, false, false);
            this.sceneFbo.createFBO_MRT(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, GL43.GL_RGB16F, true, true);
        }

        public void onWindowResize(Vector2i dim) {
            this.attachFBO(dim);
        }

        public void onRender(double partialTicks, List<SceneRenderBase> mainList, List<SceneRenderBase> additionalList) {
            UniformBufferUtils.updateLightBuffers(Scene.this);
            Scene.this.getGameUboShader().bind();
            Model2D model2D = this.genFrameBufferSquare((float) this.getWindowDimensions().x, (float) this.getWindowDimensions().y);
            this.sceneFbo.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.renderMainScene(partialTicks, mainList);
            this.sceneFbo.unBindFBO();

            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(model2D));
            this.getBlurShader().performUniform(UniformConstants.texture_sampler, 0);

            for (int i = 0; i < this.blurKernel.length; i++) {
                this.getBlurShader().performUniform("kernel", i, this.blurKernel[i]);
            }

            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO(1);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.renderTexture(model2D);
            this.sceneFbo.unBindTextureFBO();
            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();

            this.getPostProcessingShader().bind();
            this.getPostProcessingShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(model2D));
            this.getPostProcessingShader().performUniform(UniformConstants.texture_sampler, 0);
            this.getPostProcessingShader().performUniform("blur_sampler", 1);
            this.getPostProcessingShader().performUniform("post_mode", Scene.testTrigger ? 1 : this.getPostRender());
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE1);
            this.fboBlur.bindTextureFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.renderTexture(model2D);
            this.fboBlur.unBindTextureFBO();
            this.sceneFbo.unBindTextureFBO();
            this.getPostProcessingShader().unBind();

            for (SceneRenderBase sceneRenderBase : additionalList) {
                sceneRenderBase.onRender(partialTicks);
            }

            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }
            model2D.clean();
            Scene.this.getGameUboShader().unBind();
        }

        public Vector2d getWindowDimensions() {
            return Scene.this.getWindowDimensions();
        }

        public SceneWorld getRenderWorld() {
            return Scene.this.getRenderWorld();
        }

        public int getPostRender() {
            return this.CURRENT_POST_RENDER;
        }

        public void setPostRender(int a) {
            this.CURRENT_POST_RENDER = a;
            if (this.CURRENT_POST_RENDER > 3) {
                this.CURRENT_POST_RENDER = 0;
            }
        }

        private void renderMainScene(double partialTicks, List<SceneRenderBase> mainList) {
            Screen.setViewport(Scene.this.getWindowDimensions());
            for (SceneRenderBase sceneRenderBase : mainList) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        public void onStartRender() {
        }

        public void onStopRender() {
            this.fboBlur.clearFBO();
            this.sceneFbo.clearFBO();
        }

        public ShaderManager getBlurShader() {
            return ResourceManager.shaderAssets.post_blur;
        }

        public ShaderManager getPostProcessingShader() {
            return ResourceManager.shaderAssets.post_render_1;
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

        public void takeScreenshot() {
            this.wantsTakeScreenshot = true;
        }
    }
}
