package ru.BouH.engine.render.scene.render;

public interface IRenderFabric {
    void onRender(IRenderItem iRenderItem);
    void onStartRender(IRenderItem iRenderItem);
    void onStopRender(IRenderItem iRenderItem);
}
