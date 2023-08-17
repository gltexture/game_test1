package ru.BouH.engine.render.scene.fabric;

import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public class RenderNull implements RenderFabric {
    public RenderNull() {
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, IRenderObject renderItem) {
    }

    @Override
    public void onStartRender(IRenderObject renderItem) {
    }

    @Override
    public void onStopRender(IRenderObject renderItem) {
    }
}
