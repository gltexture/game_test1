package ru.BouH.engine.game.resources.assets.models.basic.forms.D3;

import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;

public class VectorModel3D implements BasicMesh<Format3D> {
    private final Vector3d v1;
    private final Vector3d v2;

    public VectorModel3D(Vector3d v1, Vector3d v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format3D> generateModel() {
        return new Model<>(new Format3D(), this.generateMesh());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
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
