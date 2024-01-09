package ru.BouH.engine.game.resource.assets.models.basic;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.forms.D2.PlaneMesh2D;
import ru.BouH.engine.game.resource.assets.models.basic.forms.D2.VectorMesh2D;
import ru.BouH.engine.game.resource.assets.models.basic.forms.D3.PlaneMesh3D;
import ru.BouH.engine.game.resource.assets.models.basic.forms.D3.VectorMesh3D;
import ru.BouH.engine.game.resource.assets.models.basic.forms.D3.WireBox3D;
import ru.BouH.engine.game.resource.assets.models.formats.Format2D;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;

public class MeshHelper {
    public static Mesh<Format2D> generateVector2DMesh(Vector2d v1, Vector2d v2) {
        VectorMesh2D vectorMesh2D = new VectorMesh2D(v1, v2);
        return vectorMesh2D.generateMesh();
    }

    public static Mesh<Format2D> generatePlane2DMesh(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneMesh2D planeMesh2D = new PlaneMesh2D(v1, v2, zLevel);
        return planeMesh2D.generateMesh();
    }

    public static Mesh<Format2D> generatePlane2DMeshInverted(Vector2d v1, Vector2d v2, int zLevel) {
        PlaneMesh2D planeMesh2D = new PlaneMesh2D(true, v1, v2, zLevel);
        return planeMesh2D.generateMesh();
    }

    public static Mesh<Format3D> generatePlane3DMesh(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        PlaneMesh3D planeMesh3D = new PlaneMesh3D(v1, v2, v3, v4);
        return planeMesh3D.generateMesh();
    }

    public static Mesh<Format3D> generateVector3DMesh(Vector3d v1, Vector3d v2) {
        VectorMesh3D vectorMesh3D = new VectorMesh3D(v1, v2);
        return vectorMesh3D.generateMesh();
    }

    public static Mesh<Format3D> generateWirebox3DMesh(Vector3d min, Vector3d max) {
        WireBox3D wireBox3D = new WireBox3D(min, max);
        return wireBox3D.generateMesh();
    }
}
