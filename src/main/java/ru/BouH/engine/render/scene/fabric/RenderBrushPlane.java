package ru.BouH.engine.render.scene.fabric;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.fabric.base.RenderWorldItem;
import ru.BouH.engine.render.scene.objects.IRenderObject;
import ru.BouH.engine.render.scene.objects.data.RenderData;
import ru.BouH.engine.render.scene.objects.items.BrushPlanePhysXObject;

public class RenderBrushPlane extends RenderWorldItem {
    public RenderBrushPlane() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
        BrushPlanePhysXObject entityItem = (BrushPlanePhysXObject) renderItem;
        if (entityItem.isHasModel()) {
            RenderData renderData = entityItem.getRenderData();
            Mesh<Format3D> mesh = entityItem.getModel3D();
            renderData.getShaderManager().getUtils().performModelViewMatrix3d(mesh);
            renderData.getShaderManager().getUtils().setTexture(renderData.getItemTexture());
            GL30.glBindVertexArray(mesh.getVao());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }
}
