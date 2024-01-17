package ru.BouH.engine.render.scene.objects;

import ru.BouH.engine.render.scene.fabric.base.RenderFabric;

public interface IRenderObject {
    RenderFabric renderFabric();

    boolean hasRender();
}
