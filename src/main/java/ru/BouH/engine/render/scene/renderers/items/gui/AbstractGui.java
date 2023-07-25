package ru.BouH.engine.render.scene.renderers.items.gui;

import ru.BouH.engine.render.scene.components.Model2DInfo;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;

public abstract class AbstractGui implements IRenderItem {
    private final String id;
    private final int zLevel;
    private Model2DInfo model2DInfo;

    public AbstractGui(String id, int zLevel) {
        this.id = id;
        this.zLevel = zLevel;
    }

    public Model2DInfo getModel2DInfo() {
        return this.model2DInfo;
    }

    public void setModel2DInfo(Model2DInfo model2DInfo) {
        this.model2DInfo = model2DInfo;
    }

    public int getzLevel() {
        return this.zLevel;
    }

    public String getId() {
        return this.id;
    }
}
