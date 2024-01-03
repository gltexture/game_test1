package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.fabric.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.WorldObject;

public class RenderEntity extends RenderWorldItem {
    public RenderEntity() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        WorldObject entityItem = (WorldObject) renderItem;
        if (entityItem.isHasModel()) {
            RenderData renderData = entityItem.getRenderData();
            Model3D model3D = entityItem.getModel3D();
            renderData.getShaderManager().getUtils().performModelViewMatrix3d(model3D);
            renderData.getShaderManager().getUtils().setTexture(renderData.getItemTexture());
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
}
