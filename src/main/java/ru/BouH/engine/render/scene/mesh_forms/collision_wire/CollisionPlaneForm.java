package ru.BouH.engine.render.scene.mesh_forms.collision_wire;

import org.joml.Vector3d;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;

import java.util.ArrayList;
import java.util.List;

public class CollisionPlaneForm extends AbstractMeshForm {
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

        this.buildMeshModel(positions, indices, null, null);
    }
}
