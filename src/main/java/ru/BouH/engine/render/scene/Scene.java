package ru.BouH.engine.render.scene;

import org.joml.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.materials.textures.ColorSample;
import ru.BouH.engine.game.resources.assets.materials.textures.IImageSample;
import ru.BouH.engine.game.resources.assets.materials.textures.ISample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.ModelNode;
import ru.BouH.engine.game.resources.assets.shaders.UniformBufferObject;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.LocalPlayer;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.LightManager;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.programs.FrameBufferObjectProgram;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
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
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

    public static void renderModel(Model<?> model) {
        Scene.renderModel(model, GL30.GL_TRIANGLES);
    }

    public static void renderModel(Model<?> model, int code) {
        if (model == null) {
            return;
        }
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(code, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public static void renderEntity(PhysicsObject physicsObject) {
        if (physicsObject == null || physicsObject.getModel3D() == null) {
            return;
        }
        ShaderManager shaderManager = physicsObject.getShaderManager();
        Model<Format3D> model = physicsObject.getModel3D();
        shaderManager.getUtils().performModelViewMatrix3d(model);
        shaderManager.performUniform("texture_scaling", physicsObject.getRenderData().getModelTextureScaling());
        for (ModelNode modelNode : model.getMeshDataGroup().getModelNodeList()) {
            Scene.performModelMaterialOnShader(physicsObject.getRenderData().getOverObjectMaterial() != null ? physicsObject.getRenderData().getOverObjectMaterial() : modelNode.getMaterial(), shaderManager);
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(GL30.GL_TRIANGLES, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public static void activeGlTexture(int code) {
        GL30.glActiveTexture(GL13.GL_TEXTURE0 + code);
    }

    public static void performModelMaterialOnShader(Material material, ShaderManager shaderManager) {
        ISample diffuse = material.getDiffuse();
        IImageSample emissive = material.getEmissive();
        IImageSample metallic = material.getMetallic();
        IImageSample normals = material.getNormals();
        IImageSample specular = material.getSpecular();

        if (diffuse != null) {
            if (diffuse instanceof IImageSample) {
                final int code = 0;
                IImageSample imageSample = ((IImageSample) diffuse);
                Scene.activeGlTexture(code);
                imageSample.bindTexture();
                shaderManager.performUniform("diffuse_mode", 1);
                shaderManager.performUniform("diffuse_map", code);
            } else {
                if (diffuse instanceof ColorSample) {
                    shaderManager.performUniform("diffuse_mode", 0);
                    shaderManager.performUniform("diffuse_color", ((ColorSample) diffuse).getColor());
                }
            }
        }
        if (emissive != null) {
            final int code = 1;
            Scene.activeGlTexture(code);
            emissive.bindTexture();
            shaderManager.performUniform("emissive_map", code);
        }
        if (metallic != null) {
            final int code = 2;
            Scene.activeGlTexture(code);
            metallic.bindTexture();
            shaderManager.performUniform("metallic_map", code);
        }
        if (normals != null) {
            final int code = 3;
            Scene.activeGlTexture(code);
            normals.bindTexture();
            shaderManager.performUniform("normals_map", code);
        }
        if (specular != null) {
            final int code = 4;
            Scene.activeGlTexture(code);
            specular.bindTexture();
            shaderManager.performUniform("specular_map", code);
        }
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
        Game.getGame().getLogManager().log("Destroying resources!");
        Game.getGame().getResourceManager().destroy();
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
            this.updateLightBuffers();
            Scene.this.getGameUboShader().bind();
            Model<Format2D> model = MeshHelper.generatePlane2DModelInverted(new Vector2d(0.0d), new Vector2d(this.getWindowDimensions().x, this.getWindowDimensions().y), 0);

            GL30.glEnable(GL30.GL_STENCIL_TEST);
            this.renderSceneInFbo(partialTicks, mainGroup);
            this.twoPassBlurShader(partialTicks, model);
            this.renderMixedScene(partialTicks, model);
            this.renderSideGroup(partialTicks, sideGroup);
            GL30.glDisable(GL30.GL_STENCIL_TEST);

            if (this.wantsTakeScreenshot) {
                this.writeBufferInFile();
                this.wantsTakeScreenshot = false;
            }

            model.clean();
            Scene.this.getGameUboShader().unBind();
        }

        public void updateLightBuffers() {
            SceneWorld sceneWorld1 = this.getRenderWorld();
            LightManager lightManager = sceneWorld1.getEnvironment().getLightManager();
            Matrix4d view = RenderManager.instance.getViewMatrix();
            Vector3f getAngle = lightManager.getNormalisedSunAngle(view);

            float sunLightX = getAngle.x;
            float sunLightY = getAngle.y;
            float sunLightZ = getAngle.z;

            FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(5);
            value1Buffer.put(lightManager.calcAmbientLight());
            value1Buffer.put(lightManager.getSunBrightness());
            value1Buffer.put(sunLightX);
            value1Buffer.put(sunLightY);
            value1Buffer.put(sunLightZ);
            value1Buffer.flip();

            Scene.this.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.SunLight, value1Buffer);
            Scene.this.getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.Misc, new float[]{SceneWorld.elapsedRenderTicks});
            this.updatePointLightBuffer(view, ResourceManager.shaderAssets.PointLights);
            MemoryUtil.memFree(value1Buffer);
        }

        private void updatePointLightBuffer(Matrix4d view, UniformBufferObject uniformBufferObject) {
            FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(7 * LightManager.MAX_POINT_LIGHTS);
            List<PointLight> pointLightList = this.getRenderWorld().getEnvironment().getLightManager().getPointLightList();
            int activeLights = pointLightList.size();
            for (int i = 0; i < activeLights; i++) {
                PointLight pointLight = pointLightList.get(i);
                float[] f1 = LightManager.getNormalisedPointLightArray(view, pointLight);
                value1Buffer.put(f1[0]);
                value1Buffer.put(f1[1]);
                value1Buffer.put(f1[2]);
                value1Buffer.put(f1[3]);
                value1Buffer.put(f1[4]);
                value1Buffer.put(f1[5]);
                value1Buffer.put(f1[6]);
                value1Buffer.flip();
                Scene.this.getGameUboShader().performUniformBuffer(uniformBufferObject, i * 32, value1Buffer);
            }
            MemoryUtil.memFree(value1Buffer);
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


        private void twoPassBlurShader(double partialTicks, Model<Format2D> model) {
            this.fboBlur.bindFBO();
            this.getBlurShader().bind();
            this.getBlurShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(model));
            this.getBlurShader().performUniform(UniformConstants.texture_sampler, 0);
            this.getBlurShader().performArrayUniform("kernel", this.blurKernel);
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO(1);
            this.glClear();
            Scene.renderModel(model);
            this.sceneFbo.unBindTextureFBO();

            this.fboBlur.bindTextureFBO();
            //this.renderTexture(model2D);
            this.fboBlur.unBindTextureFBO();

            this.getBlurShader().unBind();
            this.fboBlur.unBindFBO();
        }

        private void renderMixedScene(double partialTicks, Model<Format2D> model) {
            this.getPostProcessingShader().bind();
            this.getPostProcessingShader().performUniform(UniformConstants.projection_model_matrix, RenderManager.instance.getOrthographicModelMatrix(model));
            this.getPostProcessingShader().performUniform(UniformConstants.texture_sampler, 0);
            this.getPostProcessingShader().performUniform("blur_sampler", 1);
            this.getPostProcessingShader().performUniform("post_mode", Scene.testTrigger ? 1 : this.getCurrentRenderPostMode());
            GL30.glActiveTexture(GL30.GL_TEXTURE0);
            this.sceneFbo.bindTextureFBO();
            GL30.glActiveTexture(GL30.GL_TEXTURE1);
            this.fboBlur.bindTextureFBO();
            this.glClear();
            Scene.renderModel(model);
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

        public void takeScreenshot() {
            this.wantsTakeScreenshot = true;
        }
    }
}
