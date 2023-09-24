package ru.BouH.engine.render.scene.mesh_forms;

import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneForm extends AbstractMeshForm {
    public PlaneForm(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        this.genPlaneModel(this.reorderPositions(v1, v2, v3, v4));
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

    public void genPlaneModel(List<Vector3d> list) {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Vector3d v1 = list.get(0);
        Vector3d v2 = list.get(1);
        Vector3d v3 = list.get(2);
        Vector3d v4 = list.get(3);

        positions.add((float) v1.x);
        positions.add((float) v1.y);
        positions.add((float) v1.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(0.0f);

        positions.add((float) v2.x);
        positions.add((float) v2.y);
        positions.add((float) v2.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(0.0f);

        positions.add((float) v3.x);
        positions.add((float) v3.y);
        positions.add((float) v3.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v4.x);
        positions.add((float) v4.y);
        positions.add((float) v4.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(1.0f);


        positions.add((float) v4.x);
        positions.add((float) v4.y);
        positions.add((float) v4.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v3.x);
        positions.add((float) v3.y);
        positions.add((float) v3.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v2.x);
        positions.add((float) v2.y);
        positions.add((float) v2.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(0.0f);

        positions.add((float) v1.x);
        positions.add((float) v1.y);
        positions.add((float) v1.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(0.0f);

        indices.add(1);
        indices.add(2);
        indices.add(0);
        indices.add(3);
        indices.add(0);
        indices.add(2);

        indices.add(5);
        indices.add(6);
        indices.add(4);
        indices.add(7);
        indices.add(4);
        indices.add(6);

        Vector3f vAB = this.getPosition(positions, 1).sub(this.getPosition(positions, 0));
        Vector3f vAD = this.getPosition(positions, 3).sub(this.getPosition(positions, 0));
        Vector3f vN = vAB.cross(vAD).normalize();

        for (int i = 0; i < 4; i++) {
            normals.add(vN.x);
            normals.add(vN.y);
            normals.add(vN.z);
        }

        for (int i = 0; i < 4; i++) {
            normals.add(-vN.x);
            normals.add(-vN.y);
            normals.add(-vN.z);
        }

        this.buildMeshModel(positions, indices, textureCoordinates, normals);
    }

    private Vector3f getPosition(List<Float> list, int s) {
        return new Vector3f(list.get(s * 3), list.get(s * 3 + 1), list.get(s * 3 + 2));
    }
}
