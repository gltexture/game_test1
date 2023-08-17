package ru.BouH.engine.render.scene.objects.gui;

import ru.BouH.engine.render.scene.components.Model2D;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public abstract class AbstractGui implements IRenderObject {
    private final String id;
    private final int zLevel;
    private Model2D model2D;

    public AbstractGui(String id, int zLevel) {
        this.id = id;
        this.zLevel = zLevel;
    }

    public Model2D getModel2DInfo() {
        return this.model2D;
    }

    public void setModel2DInfo(Model2D model2D) {
        this.model2D = model2D;
    }

    public abstract void performGuiTexture();

    public int getzLevel() {
        return this.zLevel;
    }

    public String getId() {
        return this.id;
    }
}
