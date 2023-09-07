package ru.BouH.engine.render.scene.mesh_forms;

import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PlaneForm extends AbstractMeshForm {
    public PlaneForm(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        this.genPlaneModel(v1, v2, v3, v4);
    }

    public void genPlaneModel(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

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
        textureCoordinates.add(0.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v4.x);
        positions.add((float) v4.y);
        positions.add((float) v4.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v4.x);
        positions.add((float) v4.y);
        positions.add((float) v4.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(0.0f);

        positions.add((float) v3.x);
        positions.add((float) v3.y);
        positions.add((float) v3.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(0.0f);

        positions.add((float) v2.x);
        positions.add((float) v2.y);
        positions.add((float) v2.z);
        textureCoordinates.add(0.0f);
        textureCoordinates.add(1.0f);

        positions.add((float) v1.x);
        positions.add((float) v1.y);
        positions.add((float) v1.z);
        textureCoordinates.add(1.0f);
        textureCoordinates.add(1.0f);

        indices.add(1);
        indices.add(2);
        indices.add(3);
        indices.add(2);
        indices.add(1);
        indices.add(0);

        indices.add(4);
        indices.add(5);
        indices.add(6);
        indices.add(7);
        indices.add(6);
        indices.add(5);

        Vector3f vAB = this.getPosition(positions, 1).sub(this.getPosition(positions, 0));
        Vector3f vAD = this.getPosition(positions, 3).sub(this.getPosition(positions, 0));
        Vector3f vN = vAD.cross(vAB).normalize();

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
