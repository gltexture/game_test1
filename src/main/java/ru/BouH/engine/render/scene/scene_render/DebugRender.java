package ru.BouH.engine.render.scene.scene_render;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.ResourceManager;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;

import ru.BouH.engine.render.scene.objects.items.PhysicsObject;

import ru.BouH.engine.render.scene.scene_render.utility.RenderGroup;
import ru.BouH.engine.render.scene.scene_render.utility.UniformConstants;

import java.util.ArrayList;
import java.util.List;

public class DebugRender extends SceneRenderBase {
    private final ShaderManager debugShaders;

    public DebugRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(4, sceneRenderConveyor, new RenderGroup("DEBUG", false));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(double partialTicks) {
        if (this.getSceneRenderConveyor().getCurrentDebugMode() == 1) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.renderTriggers(partialTicks, this);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.debugShaders.getUtils().performProjectionMatrix();
            for (PhysicsObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
                this.renderHitBox(partialTicks, this, entityItem);
            }
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.debugShaders.unBind();
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Mesh<Format3D> mesh = MeshHelper.generateVector3DMesh(new Vector3d(0.0d), new Vector3d(sceneRenderBase.getSceneWorld().getEnvironment().getSunPosition()).mul(1000.0f));
        this.debugShaders.getUtils().performModelViewMatrix3d(mesh);
        GL30.glBindVertexArray(mesh.getVao());
        GL30.glEnableVertexAttribArray(0);
        this.debugShaders.performUniform(UniformConstants.colour, new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        GL30.glDrawElements(GL30.GL_LINES, mesh.getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        mesh.clean();
    }

    private void renderTriggers(double partialTicks, SceneRenderBase sceneRenderBase) {
        List<ITriggerZone> triggerZones = new ArrayList<>(this.getSceneWorld().getWorld().getTriggerZones());
        for (ITriggerZone triggerZone : triggerZones) {
            Mesh<Format3D> form = MeshHelper.generateWirebox3DMesh(new Vector3d(triggerZone.getZone().getSize()).mul(-0.5f), new Vector3d(triggerZone.getZone().getSize()).mul(0.5f));
            form.getFormat().getPosition().set(triggerZone.getZone().getLocation());
            this.debugShaders.getUtils().performModelViewMatrix3d(form);
            GL30.glBindVertexArray(form.getVao());
            GL30.glEnableVertexAttribArray(0);
            this.debugShaders.performUniform(UniformConstants.colour, new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
            GL30.glDrawElements(GL30.GL_LINES, form.getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            form.clean();
        }
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysicsObject physicsObject) {
        WorldItem worldItem = physicsObject.getWorldItem();
        if (worldItem instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) worldItem;
            RigidBodyObject rigidBodyObject = jBulletEntity.getRigidBodyObject();
            if (jBulletEntity.isValid()) {
                Mesh<Format3D> form = this.constructForm(rigidBodyObject);
                form.getFormat().getPosition().set(physicsObject.getRenderPosition());
                this.debugShaders.getUtils().performModelViewMatrix3d(form);
                GL30.glBindVertexArray(form.getVao());
                GL30.glEnableVertexAttribArray(0);
                this.debugShaders.performUniform(UniformConstants.colour, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
                GL30.glDrawElements(GL30.GL_LINES, form.getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
                GL30.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
                form.clean();
            }
        }
    }

    private Mesh<Format3D> constructForm(btCollisionObject btCollisionObject) {
        btVector3 min = new btVector3();
        btVector3 max = new btVector3();
        btTransform transform = new btTransform();
        transform.setIdentity();
        btCollisionObject.getCollisionShape().getAabb(transform, min, max);
        transform.deallocate();
        Mesh<Format3D> form = MeshHelper.generateWirebox3DMesh(MathHelper.convert(min), MathHelper.convert(max));
        min.deallocate();
        max.deallocate();
        return form;
    }
}