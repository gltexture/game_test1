package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.physx.brush.WorldBrush;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.BrushPlanePhysXObject;

public class RenderBrushPlane implements RenderFabric {
    public RenderBrushPlane() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        BrushPlanePhysXObject entityItem = (BrushPlanePhysXObject) renderItem;
        WorldBrush worldBrush = entityItem.getWorldBrush();
        if (entityItem.isHasModel()) {
            Model3D model3D = entityItem.getModel3D();
            model3D.setScale(worldBrush.getScale());
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
        BrushPlanePhysXObject entityItem = (BrushPlanePhysXObject) renderItem;
        if (entityItem.isHasModel()) {
            entityItem.getModel3D().setPosition(entityItem.getWorldItem().getPosition());
        }
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
        BrushPlanePhysXObject entityItem = (BrushPlanePhysXObject) renderItem;
        if (entityItem.isHasModel()) {
            entityItem.getModel3D().clean();
        }
    }
}
