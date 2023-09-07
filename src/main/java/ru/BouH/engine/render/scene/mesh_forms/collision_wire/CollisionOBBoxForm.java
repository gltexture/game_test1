package ru.BouH.engine.render.scene.mesh_forms.collision_wire;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physx.collision.objects.OBB;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;

import java.util.ArrayList;
import java.util.List;

public class CollisionOBBoxForm extends AbstractMeshForm {
    public CollisionOBBoxForm(OBB obb) {
        this.genBoxModel(obb);
    }

    private void genBoxModel(OBB collisionBox3D) throws GameException {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Vector3d size = collisionBox3D.getSize();
        Vector3d p0 = new Vector3d(0.0d).sub(new Vector3d(size).div(2.0d));
        Vector3d p4 = new Vector3d(0.0d).add(new Vector3d(size).div(2.0d));

        positions.add((float) p0.x);
        positions.add((float) p0.y);
        positions.add((float) p0.z);

        positions.add((float) p0.x);
        positions.add((float) p0.y);
        positions.add((float) p4.z);

        positions.add((float) p0.x);
        positions.add((float) p4.y);
        positions.add((float) p0.z);

        positions.add((float) p4.x);
        positions.add((float) p0.y);
        positions.add((float) p0.z);

        positions.add((float) p4.x);
        positions.add((float) p4.y);
        positions.add((float) p4.z);

        positions.add((float) p4.x);
        positions.add((float) p4.y);
        positions.add((float) p0.z);

        positions.add((float) p4.x);
        positions.add((float) p0.y);
        positions.add((float) p4.z);

        positions.add((float) p0.x);
        positions.add((float) p4.y);
        positions.add((float) p4.z);

        positions.add((float) p4.x);
        positions.add((float) p4.y);
        positions.add((float) p4.z);


        indices.add(4);
        indices.add(7);

        indices.add(6);
        indices.add(1);

        indices.add(2);
        indices.add(5);

        indices.add(3);
        indices.add(0);

        indices.add(2);
        indices.add(7);

        indices.add(0);
        indices.add(1);

        indices.add(5);
        indices.add(4);

        indices.add(3);
        indices.add(6);

        indices.add(5);
        indices.add(3);

        indices.add(2);
        indices.add(0);

        indices.add(4);
        indices.add(6);

        indices.add(7);
        indices.add(1);

        this.buildMeshModel(positions, indices, null, null);
    }
}
