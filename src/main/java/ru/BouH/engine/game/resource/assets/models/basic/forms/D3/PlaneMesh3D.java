package ru.BouH.engine.game.resource.assets.models.basic.forms.D3;

import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneMesh3D implements BasicMesh<Format3D> {
    private final Vector3d v1;
    private final Vector3d v2;
    private final Vector3d v3;
    private final Vector3d v4;

    public PlaneMesh3D(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    private List<Vector3d> reorderPositions(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        List<Vector3d> vertices = new ArrayList<>(Arrays.asList(v1, v2, v3, v4));

        Vector3d center = new Vector3d();
        for (Vector3d vector3d : vertices) {
            center.add(vector3d);
        }
        center.div(vertices.size());

        vertices.sort((e1, e2) -> {
            Vector3d vec1 = new Vector3d(e1).sub(center);
            Vector3d vec2 = new Vector3d(e2).sub(center);
            return Double.compare(Math.atan2(vec1.y, vec1.z), Math.atan2(vec2.y, vec2.x));
        });

        return vertices;
    }

    private Vector3f getPosition(List<Float> list, int s) {
        return new Vector3f(list.get(s * 3), list.get(s * 3 + 1), list.get(s * 3 + 2));
    }

    @Override
    public Mesh<Format3D> generateMesh() {
        Mesh<Format3D> mesh = new Mesh<>(new Format3D());
        List<Vector3d> list = this.reorderPositions(this.v1, this.v2, this.v3, this.v4);

        Vector3d v1 = list.get(0);
        Vector3d v2 = list.get(1);
        Vector3d v3 = list.get(2);
        Vector3d v4 = list.get(3);

        mesh.putPositionValue((float) v1.x);
        mesh.putPositionValue((float) v1.y);
        mesh.putPositionValue((float) v1.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v2.x);
        mesh.putPositionValue((float) v2.y);
        mesh.putPositionValue((float) v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v3.x);
        mesh.putPositionValue((float) v3.y);
        mesh.putPositionValue((float) v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v4.x);
        mesh.putPositionValue((float) v4.y);
        mesh.putPositionValue((float) v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);


        mesh.putPositionValue((float) v4.x);
        mesh.putPositionValue((float) v4.y);
        mesh.putPositionValue((float) v4.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v3.x);
        mesh.putPositionValue((float) v3.y);
        mesh.putPositionValue((float) v3.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(1.0f);

        mesh.putPositionValue((float) v2.x);
        mesh.putPositionValue((float) v2.y);
        mesh.putPositionValue((float) v2.z);
        mesh.putTextureCoordinateValue(1.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putPositionValue((float) v1.x);
        mesh.putPositionValue((float) v1.y);
        mesh.putPositionValue((float) v1.z);
        mesh.putTextureCoordinateValue(0.0f);
        mesh.putTextureCoordinateValue(0.0f);

        mesh.putIndexValue(1);
        mesh.putIndexValue(2);
        mesh.putIndexValue(0);
        mesh.putIndexValue(3);
        mesh.putIndexValue(0);
        mesh.putIndexValue(2);

        mesh.putIndexValue(5);
        mesh.putIndexValue(6);
        mesh.putIndexValue(4);
        mesh.putIndexValue(7);
        mesh.putIndexValue(4);
        mesh.putIndexValue(6);

        Vector3f vAB = this.getPosition(mesh.getAttributePositions(), 1).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vAD = this.getPosition(mesh.getAttributePositions(), 3).sub(this.getPosition(mesh.getAttributePositions(), 0));
        Vector3f vN = vAB.cross(vAD).normalize();

        for (int i = 0; i < 4; i++) {
            mesh.putNormalValue(vN.x);
            mesh.putNormalValue(vN.y);
            mesh.putNormalValue(vN.z);
        }

        for (int i = 0; i < 4; i++) {
            mesh.putNormalValue(-vN.x);
            mesh.putNormalValue(-vN.y);
            mesh.putNormalValue(-vN.z);
        }

        mesh.bakeMesh();
        return mesh;
    }
}
