package ru.BouH.engine.render.scene.render.entities;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.PhysMoving;
import ru.BouH.engine.render.scene.render.models.box.CollisionBoxForm;
import ru.BouH.engine.render.scene.render.models.box.VectorForm;
import ru.BouH.engine.render.scene.render.scene.SceneRender;
import ru.BouH.engine.render.scene.world.RenderWorld;

public class CollisionRenderer {
    private final RenderWorld renderWorld;

    public CollisionRenderer(RenderWorld renderWorld) {
        this.renderWorld = renderWorld;
    }

    public void renderHitBox(PhysEntity physEntity) {
        CollisionBox3D collisionBox3D = physEntity.getCollisionBox3D();
        SceneRender sceneRender = this.renderWorld.getSceneRender();
        sceneRender.getUniformProgram().setUniform("disable_light", 1);
        sceneRender.getUniformProgram().setUniform("use_texture", 0);
        if (physEntity instanceof PhysMoving) {
            PhysMoving physMoving = (PhysMoving) physEntity;
            Vector3d moving = physMoving.getMoveVector();
            VectorForm vectorForm = new VectorForm(new Vector3d(physMoving.getPosition().x, physMoving.getPosition().y, physMoving.getPosition().z), new Vector3d(physMoving.getPosition().x - moving.x, physMoving.getPosition().y - moving.y, physMoving.getPosition().z - moving.z));
            sceneRender.getUniformProgram().setUniform("model_view_matrix", this.renderWorld.getRenderManager().getTransform().getModelViewMatrix(vectorForm.getMeshInfo(), this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera())));
            sceneRender.getUniformProgram().setUniform("colour", new Vector4d(0, 1, 0, 1));
            GL30.glBindVertexArray(vectorForm.getMeshInfo().getMesh().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            GL30.glDrawElements(GL30.GL_LINES, vectorForm.getMeshInfo().getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            vectorForm.getMeshInfo().getMesh().cleanMesh();
        }
        if (collisionBox3D != null) {
            CollisionBoxForm collisionBoxForm = new CollisionBoxForm(collisionBox3D);
            sceneRender.getUniformProgram().setUniform("model_view_matrix", this.renderWorld.getRenderManager().getTransform().getModelViewMatrix(collisionBoxForm.getMeshInfo(), this.renderWorld.getRenderManager().getTransform().getViewMatrix(this.renderWorld.getCamera())));
            sceneRender.getUniformProgram().setUniform("colour", physEntity.isCollided() ? new Vector4d(1, 0, 0, 1) : new Vector4d(1, 1, 1, 1));
            GL30.glBindVertexArray(collisionBoxForm.getMeshInfo().getMesh().getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            GL30.glDrawElements(GL30.GL_LINES, collisionBoxForm.getMeshInfo().getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            collisionBoxForm.getMeshInfo().getMesh().cleanMesh();
        }
        sceneRender.getUniformProgram().setUniform("disable_light", 0);
    }
}
