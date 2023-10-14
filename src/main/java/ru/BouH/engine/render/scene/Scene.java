package ru.BouH.engine.render.scene;

import org.joml.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.shadows.CascadeShadowBuilder;
import ru.BouH.engine.render.environment.shadows.DepthBuffer;
import ru.BouH.engine.render.environment.shadows.DepthTexture;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.items.PhysXObject;
import ru.BouH.engine.render.scene.programs.FrameBufferObjectProgram;
import ru.BouH.engine.render.scene.programs.ShaderManager;
import ru.BouH.engine.render.scene.programs.UniformBufferUtils;
import ru.BouH.engine.render.scene.scene_render.GuiRender;
import ru.BouH.engine.render.scene.scene_render.SkyRender;
import ru.BouH.engine.render.scene.scene_render.WorldRender;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;
import ru.BouH.engine.render.scene.world.camera.FreeCamera;
import ru.BouH.engine.render.scene.world.camera.ICamera;
import ru.BouH.engine.render.screen.Screen;
import ru.BouH.engine.render.screen.timer.TestTimer;
import ru.BouH.engine.render.screen.window.Window;
import ru.BouH.engine.render.utils.Syncer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scene {
    public static final Syncer SYNC_UPD_POS = new Syncer();
    public double elapsedTime;
    private final List<SceneRenderBase> sceneRenderBases;
    private final Screen screen;
    private final Window window;
    private final SceneWorld sceneWorld;
    private final SkyRender skyRender;
    private final GuiRender guiRender;
    private final WorldRender worldRender;
    private final FrustumCulling frustumCulling;
    private final SceneRenderConveyor sceneRenderConveyor;
    private ICamera currentCamera;

    public Scene(Screen screen, SceneWorld sceneWorld) {
        Scene.SYNC_UPD_POS.syncUp();
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
        this.elapsedTime = PhysicThreadManager.getFrameTime();
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
        LocalPlayer localPlayer = Game.getGame().getProxy().getLocalPlayer();
        this.getRenderWorld().setFrustumCulling(this.getFrustumCulling());
        if (LocalPlayer.VALID_PL) {
            this.attachCameraToLocalPlayer(Game.getGame().getProxy().getLocalPlayer());
        }
        Game.getGame().getLogManager().log("Starting scene rendering: ");
        this.getSceneRender().onStartRender();
        for (SceneRenderBase sceneRenderBase : this.sceneRenderBases) {
            Game.getGame().getLogManager().log("Starting " + sceneRenderBase.getRenderGroup().name() + " scene");
            sceneRenderBase.onStartRender();
            Game.getGame().getLogManager().log("Scene " + sceneRenderBase.getRenderGroup().name() + " successfully started!");
        }
        Game.getGame().getLogManager().log("Filling light buffer!");
        this.initLights();
        Game.getGame().getLogManager().log("Scene rendering started");
    }

    private void initLights() {
        this.initPointLights(this.getEntityRender(), UniformBufferUtils.UBO_POINT_LIGHTS.getName());
    }

    private void initPointLights(SceneRenderBase sceneRenderBase, String bufferName) {
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(7 * LightManager.MAX_POINT_LIGHTS);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        value1Buffer.put(0.0f);
        for (int i = 0; i < LightManager.MAX_POINT_LIGHTS; i++) {
            sceneRenderBase.performUniformBuffer(bufferName, i * 32, value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);
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

    public void enableFreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        this.setRenderCamera(new FreeCamera(controller, pos, rot));
    }

    public void enableAttachedCamera(WorldItem worldItem) {
        if (worldItem != null) {
            this.setRenderCamera(new AttachedCamera(worldItem, this.getCurrentController()));
        }
    }

    public void attachCameraToLocalPlayer(LocalPlayer localPlayer) {
        this.enableAttachedCamera(localPlayer.getEntityPlayerSP());
    }

    public IController getCurrentController() {
        return this.getScreen().getControllerDispatcher().getCurrentController();
    }

    public boolean isCameraAttachedToItem(WorldItem worldItem) {
        return this.getCurrentCamera() instanceof AttachedCamera && ((AttachedCamera) this.getCurrentCamera()).getWorldItem() == worldItem;
    }

    TestTimer testTimer = new TestTimer(20);
    public void renderScene(double deltaTime) {
        testTimer.updateTimer();
        this.elapsedTime += deltaTime / PhysicThreadManager.getFrameTime();
        double d1 = Math.min(this.elapsedTime, 1.0d);
        System.out.println(d1);
        this.renderSceneWithInterpolation(deltaTime, d1);
    }

    @SuppressWarnings("all")
    private void renderSceneWithInterpolation(double deltaTicks, double partialTicks) {
        if (Scene.isSceneActive()) {
            if (this.getCurrentCamera() != null) {
                while (Game.getGame().getScreen().getTimer().markSyncUpdate());
                this.getRenderWorld().onWorldEntityUpdate(partialTicks, deltaTicks / PhysicThreadManager.getFrameTime());
                synchronized (PhysicThreadManager.locker) {
                    PhysicThreadManager.locker.notifyAll();
                }
                this.getCurrentCamera().updateCameraPosition(deltaTicks);
                this.getCurrentCamera().updateCameraRotation(partialTicks);
                RenderManager.instance.updateViewMatrix(this.getCurrentCamera());
                this.getFrustumCulling().refreshFrustumCullingState(RenderManager.instance.getProjectionMatrix(), RenderManager.instance.getViewMatrix());
                this.getSceneRender().onRender(partialTicks, this.sortedSceneList(this.getEntityRender(), this.getSkyRender()), this.sortedSceneList(this.getGuiRender()));
            }
        }
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
        Game.getGame().getLogManager().log("Scene rendering stopped");
    }

    public static class ShadowDispatcher {
        private final DepthBuffer depthMap;
        private final List<CascadeShadowBuilder> cascadeShadowBuilders;
        private final ShaderManager depthShaderManager;
        private final int numCascades;
        private final SceneWorld sceneWorld;

        public ShadowDispatcher(SceneWorld sceneWorld, int cascadeCount) {
            this.numCascades = cascadeCount;
            this.sceneWorld = sceneWorld;
            this.depthMap = new DepthBuffer();
            this.cascadeShadowBuilders = new ArrayList<>();
            for (int i = 0; i < cascadeCount; i++) {
                CascadeShadowBuilder cascadeShadowBuilder = new CascadeShadowBuilder();
                cascadeShadowBuilders.add(cascadeShadowBuilder);
            }
            this.depthShaderManager = new ShaderManager("shadows");
            this.initShaders();
        }

        private void initShaders() {
            this.getDepthShaderManager().addUniform("model_matrix");
            this.getDepthShaderManager().addUniform("projection_view_matrix");
        }

        private void renderDepthBuffer(double partialTicks, SceneRenderBase base) {
            Vector3f sunPos = this.getSceneWorld().getEnvironment().getSunPosition();
            CascadeShadowBuilder.updateCascadeShadow(this.getCascadeShadowBuilders(), new Vector4d(sunPos, 0.0d), RenderManager.instance.getViewMatrix(), RenderManager.instance.getProjectionMatrix());
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.depthMap.getDepthFbo());
            Screen.setViewport(new Vector2i(DepthTexture.MAP_DIMENSIONS));
            this.getDepthShaderManager().bind();
            for (int i = 0; i < this.getNumCascades(); i++) {
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, this.depthMap.getDepthTexture().getId()[i], 0);
                GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
                CascadeShadowBuilder cascadeShadowBuilder = this.cascadeShadowBuilders.get(i);
                this.getDepthShaderManager().performUniform("projection_view_matrix", cascadeShadowBuilder.getProjectionViewMatrix());
                for (PhysXObject physXObject : base.getSceneWorld().getEntityList()) {
                    if (physXObject.isHasModel()) {
                        Model3D model3D = physXObject.getModel3D();
                        Matrix4d m2 = RenderManager.instance.getModelMatrix(model3D);
                        this.getDepthShaderManager().performUniform("model_matrix", m2);
                        physXObject.renderFabric().onRender(partialTicks, base, physXObject);
                    }
                }
            }
            this.getDepthShaderManager().unBind();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }

        public SceneWorld getSceneWorld() {
            return this.sceneWorld;
        }

        public void onStartRender() {
            this.getDepthShaderManager().startProgram();
        }

        public void onStopRender() {
            this.getDepthShaderManager().destroyProgram();
            this.getDepthMap().cleanBuffer();
        }

        public ShaderManager getDepthShaderManager() {
            return this.depthShaderManager;
        }

        public DepthBuffer getDepthMap() {
            return this.depthMap;
        }

        public int getNumCascades() {
            return this.numCascades;
        }

        public List<CascadeShadowBuilder> getCascadeShadowBuilders() {
            return this.cascadeShadowBuilders;
        }
    }

    public class SceneRenderConveyor {
        private final ShaderManager postProcessingShader;
        private final ShaderManager blurShader;
        private final ShadowDispatcher shadowDispatcher;
        private final FrameBufferObjectProgram fboBlur;
        private final FrameBufferObjectProgram sceneFbo;
        private final float[] blurKernel;
        private int CURRENT_POST_RENDER = 0;
        private boolean wantsTakeScreenshot;

        public SceneRenderConveyor() {
            this.postProcessingShader = new ShaderManager("post_render_1");
            this.blurShader = new ShaderManager("post_blur");
            this.fboBlur = new FrameBufferObjectProgram();
            this.sceneFbo = new FrameBufferObjectProgram();
            this.blurKernel = this.blurKernels(8.0f, 7);
            this.shadowDispatcher = new ShadowDispatcher(Scene.this.getRenderWorld(), CascadeShadowBuilder.SHADOW_CASCADE_MAX);
            this.initShaders();
        }

        private void initShaders() {
            this.attachFBO(new Vector2i((int) this.getWindowDimensions().x, (int) this.getWindowDimensions().y));
            for (int i = 0; i < this.blurKernel.length; i++) {
                this.getBlurShader().addUniform("kernel[" + i + "]");
            }
            this.getBlurShader().addUniform("projection_model_matrix");
            this.getBlurShader().addUniform("texture_sampler");
            this.getPostProcessingShader().addUniform("projection_model_matrix");
            this.getPostProcessingShader().addUniform("texture_sampler");
            this.getPostProcessingShader().addUniform("blur_sampler");
            this.getPostProcessingShader().addUniform("post_mode");
            this.getPostProcessingShader().addUniformBuffer(UniformBufferUtils.UBO_MISC);
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
            //this.getShadowDispatcher().renderDepthBuffer(partialTicks, Scene.this.getEntityRender());

            Model2D model2D = this.genFrameBufferSquare((float) this.getWindowDimensions().x, (float) this.getWindowDimensions().y);

            this.sceneFbo.bindFBO();
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.renderMainScene(partialTicks, mainList);
            this.sceneFbo.unBindFBO();

            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performUniform("projection_model_matrix", RenderManager.instance.getOrthographicModelMatrix(model2D));
            this.getBlurShader().performUniform("texture_sampler", 0);

            for (int i = 0; i < this.blurKernel.length; i++) {
                this.getBlurShader().performUniform("kernel[" + i + "]", this.blurKernel[i]);
            }

            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO(1);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            this.renderTexture(model2D);
            this.sceneFbo.unBindTextureFBO();
            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();

            this.getPostProcessingShader().bind();
            this.getPostProcessingShader().performUniform("projection_model_matrix", RenderManager.instance.getOrthographicModelMatrix(model2D));
            this.getPostProcessingShader().performUniform("texture_sampler", 0);
            this.getPostProcessingShader().performUniform("blur_sampler", 1);
            this.getPostProcessingShader().performUniform("post_mode", this.getPostRender());
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
                sceneRenderBase.bindProgram();
                sceneRenderBase.onRender(partialTicks);
                sceneRenderBase.unBindProgram();
            }

            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }
            model2D.clean();
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

        public ShadowDispatcher getShadowDispatcher() {
            return this.shadowDispatcher;
        }

        private void renderMainScene(double partialTicks, List<SceneRenderBase> mainList) {
            Screen.setViewport(Scene.this.getWindowDimensions());
            for (SceneRenderBase sceneRenderBase : mainList) {
                sceneRenderBase.onRender(partialTicks);
            }
        }

        public void onStartRender() {
            this.getBlurShader().startProgram();
            this.getPostProcessingShader().startProgram();
            this.getShadowDispatcher().onStartRender();
        }

        public void onStopRender() {
            this.fboBlur.clearFBO();
            this.sceneFbo.clearFBO();
            this.getBlurShader().destroyProgram();
            this.getPostProcessingShader().destroyProgram();
            this.getShadowDispatcher().onStopRender();
        }

        public ShaderManager getBlurShader() {
            return this.blurShader;
        }

        public ShaderManager getPostProcessingShader() {
            return this.postProcessingShader;
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
