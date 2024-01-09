package ru.BouH.engine.game.resource.assets.models.basic.forms.D2;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;

public class VectorMesh2D implements BasicMesh<Format2D> {
    private final Vector2d v1;
    private final Vector2d v2;

    public VectorMesh2D(Vector2d v1, Vector2d v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Mesh<Format2D> generateMesh() {
        Mesh<Format2D> mesh = new Mesh<>(new Format2D());
        mesh.putPositionValue((float) this.v1.x);
        mesh.putPositionValue((float) this.v1.y);

        mesh.putPositionValue((float) this.v2.x);
        mesh.putPositionValue((float) this.v2.y);

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);

        mesh.bakeMesh();
        return mesh;
    }
}
