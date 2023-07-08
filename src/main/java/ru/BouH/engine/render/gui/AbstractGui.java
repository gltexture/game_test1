package ru.BouH.engine.render.gui;

import ru.BouH.engine.render.scene.components.Model2DInfo;

public abstract class AbstractGui {
    private final String id;
    private Model2DInfo model2DInfo;
    private final int zLevel;

    public AbstractGui(String id, int zLevel) {
        this.id = id;
        this.zLevel = zLevel;
    }

    public abstract void render();

    public void setModel2DInfo(Model2DInfo model2DInfo) {
        this.model2DInfo = model2DInfo;
    }

    public Model2DInfo getModel2DInfo() {
        return this.model2DInfo;
    }

    public int getzLevel() {
        return 0;
    }

    public String getId() {
        return this.id;
    }
}
