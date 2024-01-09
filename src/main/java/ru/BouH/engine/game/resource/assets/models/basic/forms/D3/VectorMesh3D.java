package ru.BouH.engine.game.resource.assets.models.basic.forms.D3;

import org.joml.Vector3d;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;

public class VectorMesh3D implements BasicMesh<Format3D> {
    private final Vector3d v1;
    private final Vector3d v2;

    public VectorMesh3D(Vector3d v1, Vector3d v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Mesh<Format3D> generateMesh() {
        Mesh<Format3D> mesh = new Mesh<>(new Format3D());
        mesh.putPositionValue((float) this.v1.x);
        mesh.putPositionValue((float) this.v1.y);
        mesh.putPositionValue((float) this.v1.z);

        mesh.putPositionValue((float) this.v2.x);
        mesh.putPositionValue((float) this.v2.y);
        mesh.putPositionValue((float) this.v2.z);

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);

        mesh.bakeMesh();
        return mesh;
    }
}
