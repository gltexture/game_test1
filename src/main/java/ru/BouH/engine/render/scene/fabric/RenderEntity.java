package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.EntityPhysXObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class RenderEntity implements RenderFabric {
    public RenderEntity() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        EntityPhysXObject entityItem = (EntityPhysXObject) renderItem;
        PhysEntity physEntity = entityItem.getEntity();
        if (entityItem.isHasModel()) {
            Model3D model3D = entityItem.getModel3D();
            model3D.setScale(physEntity.getScale());
            model3D.getPosition().lerp(entityItem.getRenderPosition(), partialTicks);
            model3D.setRotation(entityItem.getRenderRotation());
            sceneRenderBase.getUtils().performModelViewMatrix3d(sceneRenderBase.getSceneWorld(), model3D);
            RenderData renderData = entityItem.getRenderData();
            GL30.glBindVertexArray(model3D.getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            sceneRenderBase.getUtils().setTexture(renderData.getItemTexture());
            GL30.glDrawElements(GL30.GL_TRIANGLES, model3D.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {
        EntityPhysXObject entityItem = (EntityPhysXObject) renderItem;
        if (entityItem.isHasModel()) {
            entityItem.getModel3D().setPosition(entityItem.getEntity().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
        EntityPhysXObject entityItem = (EntityPhysXObject) renderItem;
        if (entityItem.isHasModel()) {
            SceneWorld.toCleanSet.add(entityItem.getModel3D().getMeshModel());
        }
    }
}
