package ru.BouH.engine.render.scene.components;

public abstract class ObjectModel {
    private final IMesh meshModel;

    public ObjectModel(IMesh meshModel) {
        this.meshModel = meshModel;
    }

    public IMesh getMeshModel() {
        return this.meshModel;
    }

    public int getVertexCount() {
        return this.getMeshModel().getVertexCount();
    }

    public int getVao() {
        return this.getMeshModel().getVao();
    }

    public void clean() {
        this.getMeshModel().cleanMesh();
    }
}
