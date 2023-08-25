package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
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
            RenderData renderData = entityItem.getRenderData();
            RenderData.RenderProperties renderProperties = renderData.getRenderProperties();
            Model3D model3D = entityItem.getModel3D();
            model3D.setScale(physEntity.getScale());
            if (entityItem.shouldInterpolatePos()) {
                model3D.getPosition().lerp(entityItem.getRenderPosition(), partialTicks);
            } else {
                model3D.setPosition(entityItem.getRenderPosition());
            }
            if (entityItem.shouldInterpolateRot()) {
                model3D.getRotation().lerp(entityItem.getRenderRotation(), partialTicks);
            } else {
                model3D.setRotation(entityItem.getRenderRotation());
            }
            sceneRenderBase.getUtils().performModelViewMatrix3d(sceneRenderBase.getSceneWorld(), model3D);
            sceneRenderBase.getUtils().setTexture(renderData.getItemTexture());
            GL30.glBindVertexArray(model3D.getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
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
