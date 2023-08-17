package ru.BouH.engine.render.scene.primitive_forms;

import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;

import java.util.ArrayList;
import java.util.List;

public class CollisionPlaneForm implements IForm {
    private Model3D model;

    public CollisionPlaneForm(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        this.genPlaneModel(v1, v2, v3, v4);
    }

    public void genPlaneModel(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        positions.add((float) v1.x);
        positions.add((float) v1.y);
        positions.add((float) v1.z);

        positions.add((float) v2.x);
        positions.add((float) v2.y);
        positions.add((float) v2.z);

        positions.add((float) v3.x);
        positions.add((float) v3.y);
        positions.add((float) v3.z);

        positions.add((float) v4.x);
        positions.add((float) v4.y);
        positions.add((float) v4.z);

        indices.add(0);
        indices.add(1);

        indices.add(0);
        indices.add(2);

        indices.add(3);
        indices.add(1);

        indices.add(3);
        indices.add(2);

        indices.add(0);
        indices.add(3);

        indices.add(1);
        indices.add(2);

        float[] f1 = new float[positions.size()];
        int[] i1 = new int[indices.size()];

        for (int i = 0; i < f1.length; i++) {
            f1[i] = positions.get(i);
        }

        for (int i = 0; i < i1.length; i++) {
            i1[i] = indices.get(i);
        }
        this.model = new Model3D(new MeshModel(f1, i1));
    }

    private Vector3f getPosition(List<Float> list, int s) {
        return new Vector3f(list.get(s * 3), list.get(s * 3 + 1), list.get(s * 3 + 2));
    }

    public Model3D getMeshInfo() {
        return this.model;
    }
}
