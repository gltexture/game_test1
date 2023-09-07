package ru.BouH.engine.render.scene.mesh_forms;

import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class VectorForm extends AbstractMeshForm {
    public VectorForm(Vector3d start, Vector3d end) {
        this.genVectorModel(start, end);
    }

    public void genVectorModel(Vector3d start, Vector3d end) {
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

        this.buildMeshModel(positions, indices, null, null);
    }
}
