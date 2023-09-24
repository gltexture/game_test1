package ru.BouH.engine.render.scene.mesh_forms.wire;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;

import java.util.ArrayList;
import java.util.List;

public class AABBWireForm extends AbstractMeshForm {
    public AABBWireForm(btVector3 min, btVector3 max) {
        this.genBoxModel(MathHelper.convert(min), MathHelper.convert(max));
        min.deallocate();
        max.deallocate();
    }

    public AABBWireForm(Vector3d min, Vector3d max) {
        this.genBoxModel(min, max);
    }

    public void genBoxModel(Vector3d min, Vector3d max) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        positions.add((float) min.x);
        positions.add((float) min.y);
        positions.add((float) min.z);

        positions.add((float) max.x);
        positions.add((float) min.y);
        positions.add((float) min.z);

        positions.add((float) max.x);
        positions.add((float) max.y);
        positions.add((float) min.z);

        positions.add((float) min.x);
        positions.add((float) max.y);
        positions.add((float) min.z);

        positions.add((float) min.x);
        positions.add((float) min.y);
        positions.add((float) max.z);

        positions.add((float) max.x);
        positions.add((float) min.y);
        positions.add((float) max.z);

        positions.add((float) max.x);
        positions.add((float) max.y);
        positions.add((float) max.z);

        positions.add((float) min.x);
        positions.add((float) max.y);
        positions.add((float) max.z);

        indices.add(0);
        indices.add(1);

        indices.add(1);
        indices.add(2);

        indices.add(2);
        indices.add(3);

        indices.add(3);
        indices.add(0);

        indices.add(4);
        indices.add(5);

        indices.add(5);
        indices.add(6);

        indices.add(6);
        indices.add(7);

        indices.add(7);
        indices.add(4);

        indices.add(0);
        indices.add(4);

        indices.add(1);
        indices.add(5);

        indices.add(2);
        indices.add(6);

        indices.add(3);
        indices.add(7);

        this.buildMeshModel(positions, indices, null, null);
    }
}
