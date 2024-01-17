package ru.BouH.engine.game.resources.assets.models.mesh;

import ru.BouH.engine.game.resources.assets.materials.Material;

public class ModelNode {
    private final Mesh mesh;
    private final Material material;

    public ModelNode(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public ModelNode(Mesh mesh) {
        this.mesh = mesh;
        this.material = new Material();
    }

    public void cleanMesh() {
        this.getMesh().clean();
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public Material getMaterial() {
        return this.material;
    }
}
