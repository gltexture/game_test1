package ru.BouH.engine.render.scene;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.FrameBufferObjectProgram;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Scene implements IScene {
    public static boolean testTrigger = false;
    private double elapsedTime;
    private final List<SceneRenderBase> sceneRenderBases;
    private List<SceneRenderBase> mainGroup;
    private List<SceneRenderBase> sideGroup;
    private final Screen screen;
    private final Window window;
    private final SceneWorld sceneWorld;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    private boolean refresh;
    private ICamera currentCamera;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
        this.screen = screen;
        this.mainGroup = new ArrayList<>();
        this.sideGroup = new ArrayList<>();
        this.window = this.getScreen().getWindow();
        this.frustumCulling = new FrustumCulling();
        this.sceneRenderBases = new ArrayList<>();
        this.sceneRenderConveyor = new SceneRenderConveyor();
        this.currentCamera = null;
    }

    public static boolean isSceneActive() {
        return Screen.isScreenActive();
    }

    public static void setCamera(ICamera camera) {
        Game.getGame().getScreen().getScene().setRenderCamera(camera);
    }

    public static int getDebugMode() {
        return Game.getGame().getScreen().getScene().getSceneRenderConveyor().getCurrentDebugMode();
    }

    public static int getPostRender() {
        return Game.getGame().getScreen().getScene().getSceneRenderConveyor().getCurrentRenderPostMode();
    }

    public static void setSceneDebugMode(int a) {
        Game.getGame().getScreen().getScene().setDebugMode(a);
    }

    public static void setScenePostRender(int a) {
        Game.getGame().getScreen().getScene().setRenderPostMode(a);
    }

    public void addSceneRenderBase(SceneRenderBase sceneRenderBase) {
        this.sceneRenderBases.add(sceneRenderBase);
    }

    public void setRenderCamera(ICamera camera) {
        this.currentCamera = camera;
    }

    public ICamera getCurrentCamera() {
        return this.currentCamera;
    }

    public SceneWorld getSceneWorld() {
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

    private void collectRenderBases() {
        Game.getGame().getLogManager().log("Creating render groups...");
        this.mainGroup = this.getRenderQueueContainer().stream().filter(e -> e.getRenderGroup().isMainSceneGroup()).sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
        this.sideGroup = this.getRenderQueueContainer().stream().filter(e -> !e.getRenderGroup().isMainSceneGroup()).sorted(Comparator.comparingInt(SceneRenderBase::getRenderPriority)).collect(Collectors.toList());
    }

    public void preRender() {
        this.collectRenderBases();
        ResourceManager.shaderAssets.startShaders();
        this.getSceneWorld().setFrustumCulling(this.getFrustumCulling());
        if (LocalPlayer.VALID_PL) {
            this.enableAttachedCamera(SceneWorld.PL);
        }
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getSceneRender().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully started!");
        }
    }

    public List<SceneRenderBase> getRenderQueueContainer() {
        return this.sceneRenderBases;
    }

    public void onWindowResize(Vector2i dim) {
        this.getSceneRenderConveyor().onWindowResize(dim);
    }

    public SceneRenderConveyor getSceneRenderConveyor() {
        return this.sceneRenderConveyor;
    }

    public void setRenderPostMode(int a) {
        this.getSceneRender().CURRENT_POST_RENDER = a;
        if (this.getSceneRender().CURRENT_POST_RENDER > 3) {
            this.getSceneRender().CURRENT_POST_RENDER = 0;
        }
    }

    public void setDebugMode(int a) {
        this.getSceneRender().CURRENT_DEBUG_MODE = a;
        if (this.getSceneRender().CURRENT_DEBUG_MODE > 1) {
            this.getSceneRender().CURRENT_DEBUG_MODE = 0;
        }
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
        this.getSceneWorld().onWorldEntityUpdate(this.refresh, partialTicks);
        this.getCurrentCamera().updateCamera(partialTicks);
        RenderManager.instance.updateViewMatrix(this.getCurrentCamera());
        this.getSceneRender().onRender(partialTicks, this.mainGroup, this.sideGroup);
        this.refresh = false;
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
        for (SceneRenderBase sceneRenderBase : this.getRenderQueueContainer()) {
            Game.getGame().getLogManager().log("Stopping " + sceneRenderBase.getRenderGroup().getId() + " scene");
            sceneRenderBase.onStopRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().getId() + " successfully stopped!");
        }
        ResourceManager.shaderAssets.destroyShaders();
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public class SceneRenderConveyor {
        private final FrameBufferObjectProgram fboBlur;
        private final FrameBufferObjectProgram sceneFbo;
        private final float[] blurKernel;
        private int CURRENT_POST_RENDER = 0;
        private int CURRENT_DEBUG_MODE;
        private boolean wantsTakeScreenshot;

        public SceneRenderConveyor() {
            this.fboBlur = new FrameBufferObjectProgram();
            this.sceneFbo = new FrameBufferObjectProgram();
            this.blurKernel = this.blurKernels(8.0f);
            this.initShaders();
        }

        private void initShaders() {
            this.attachFBO(new Vector2i((int) this.getWindowDimensions().x, (int) this.getWindowDimensions().y));
        }

        private float[] blurKernels(float sigma) {
            float[] kernel = new float[5];
            float weight = 0.0f;

            for (int i = 0; i < 5; i++) {
                float x = i - (5 / 2.0f);
                kernel[i] = (float) Math.exp(-(x * x) / (2.0f * sigma * sigma));
                weight += kernel[i];
            }

            for (int i = 0; i < 5; i++) {
                kernel[i] /= weight;
            }

            return kernel;
        }

        private void attachFBO(Vector2i dim) {
            this.fboBlur.createFBO_MRT(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, GL30.GL_SRGB_ALPHA, false, false);
            this.sceneFbo.createFBO_MRT(dim, new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1}, GL43.GL_RGB16F, true, true);
        }

        public void onWindowResize(Vector2i dim) {
            this.attachFBO(dim);
        }

        private void glClear() {
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        }

        public int getCurrentDebugMode() {
            return this.CURRENT_DEBUG_MODE;
        }

        public int getCurrentRenderPostMode() {
            return this.CURRENT_POST_RENDER;
        }

        public void onRender(double partialTicks, List<SceneRenderBase> mainGroup, List<SceneRenderBase> sideGroup) {
            UniformBufferUtils.updateLightBuffers(Scene.this);
            Scene.this.getGameUboShader().bind();
            Mesh<Format2D> mesh = MeshHelper.generatePlane2DMeshInverted(new Vector2d(0.0d), new Vector2d(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);

            GL30.glEnable(GL30.GL_STENCIL_TEST);
            this.renderSceneInFbo(partialTicks, mainGroup);
            this.twoPassBlurShader(partialTicks, mesh);
            this.renderMixedScene(partialTicks, mesh);
            this.renderSideGroup(partialTicks, sideGroup);
            GL30.glDisable(GL30.GL_STENCIL_TEST);

            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }

            mesh.clean();
            Scene.this.getGameUboShader().unBind();
        }

        private void renderSideGroup(double partialTicks, List<SceneRenderBase> sideGroup) {
            for (SceneRenderBase sceneRenderBase : sideGroup) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        private void renderSceneInFbo(double partialTicks, List<SceneRenderBase> mainList) {
            this.sceneFbo.bindFBO();
            this.glClear();
            this.renderMainScene(partialTicks, mainList);
            this.sceneFbo.unBindFBO();
        }


        private void twoPassBlurShader(double partialTicks, Mesh<Format2D> mesh) {
            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(mesh));
            this.getBlurShader().performUniform(UniformConstants.texture_sampler, 0);
            this.getBlurShader().performArrayUniform("kernel", this.blurKernel);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO(1);
            this.glClear();
            this.renderTexture(mesh);
            this.sceneFbo.unBindTextureFBO();

            this.fboBlur.bindTextureFBO();
            //this.renderTexture(model2D);
            this.fboBlur.unBindTextureFBO();

            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();
        }

        private void renderMixedScene(double partialTicks, Mesh<Format2D> mesh) {
            this.getPostProcessingShader().bind();
            this.getPostProcessingShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(mesh));
            this.getPostProcessingShader().performUniform(UniformConstants.texture_sampler, 0);
            this.getPostProcessingShader().performUniform("blur_sampler", 1);
            this.getPostProcessingShader().performUniform("post_mode", Scene.testTrigger ? 1 : this.getCurrentRenderPostMode());
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE1);
            this.fboBlur.bindTextureFBO();
            this.glClear();
            this.renderTexture(mesh);
            this.fboBlur.unBindTextureFBO();
            this.sceneFbo.unBindTextureFBO();
            this.getPostProcessingShader().unBind();
        }

        public Vector2d getWindowDimensions() {
            return Scene.this.getWindowDimensions();
        }

        public SceneWorld getRenderWorld() {
            return Scene.this.getSceneWorld();
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

        private void renderTexture(Mesh<Format2D> mesh) {
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            GL30.glBindVertexArray(mesh.getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glBindVertexArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
        }

        public void takeScreenshot() {
            this.wantsTakeScreenshot = true;
        }
    }
}
