package ru.BouH.engine.render.scene.renderers.items.models.box;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;

import java.util.ArrayList;
import java.util.List;

public class VectorForm {
    private Model3DInfo model;

    public VectorForm(Vector3d start, Vector3d end) {
        this.genBoxModel(start, end);
    }

    public void genBoxModel(Vector3d start, Vector3d end) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        positions.add((float) start.x);
        positions.add((float) start.y);
        positions.add((float) start.z);

        positions.add((float) end.x);
        positions.add((float) end.y);
        positions.add((float) end.z);

        indices.add(0);
        indices.add(1);

        float[] f1 = new float[positions.size()];
        int[] i1 = new int[indices.size()];

        for (int i = 0; i < f1.length; i++) {
            f1[i] = positions.get(i);
        }

        for (int i = 0; i < i1.length; i++) {
            i1[i] = indices.get(i);
        }

        this.model = new Model3DInfo(new Model3D(f1, i1));
    }

    public Model3DInfo getMeshInfo() {
        return this.model;
    }
}
