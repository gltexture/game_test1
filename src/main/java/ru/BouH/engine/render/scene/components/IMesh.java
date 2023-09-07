package ru.BouH.engine.render.scene.components;

public interface IMesh {
    int getVao();

    int getVertexCount();

    void cleanMesh();
}
