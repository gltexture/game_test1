package ru.BouH.engine.render.scene.objects.gui;

import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.game.resource.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.objects.IRenderObject;

public abstract class AbstractGui implements IRenderObject {
    private final String id;
    private final ShaderManager shaderManager;
    private final int zLevel;
    private Mesh<Format2D> model2D;

    public AbstractGui(String id, ShaderManager shaderManager, int zLevel) {
        this.id = id;
        this.zLevel = zLevel;
        this.shaderManager = shaderManager;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public Mesh<Format2D> getModel2DInfo() {
        return this.model2D;
    }

    public void setModel2DInfo(Mesh<Format2D> model2D) {
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
