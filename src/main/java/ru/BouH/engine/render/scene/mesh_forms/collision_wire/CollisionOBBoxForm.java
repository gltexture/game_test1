package ru.BouH.engine.render.scene.mesh_forms.collision_wire;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.collision.objects.OBB;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.mesh_forms.AbstractMeshForm;

import java.util.ArrayList;
import java.util.List;

public class CollisionOBBoxForm extends AbstractMeshForm {
    public CollisionOBBoxForm(OBB obb) {
        this.genBoxModel(obb);
    }

    private void genBoxModel(OBB collisionBox3D) throws GameException {
        float[] positions = new float[24];
        int[] indices = new int[24];

        Vector3d size = collisionBox3D.getSize();
        Vector3d p0 = new Vector3d(0.0d).sub(new Vector3d(size).div(2.0d));
        Vector3d p4 = new Vector3d(0.0d).add(new Vector3d(size).div(2.0d));

        positions[0] = (float) p0.x;
        positions[1] = (float) p0.y;
        positions[2] = (float) p0.z;

        positions[3] = (float) p0.x;
        positions[4] = (float) p0.y;
        positions[5] = (float) p4.z;

        positions[6] = (float) p0.x;
        positions[7] = (float) p4.y;
        positions[8] = (float) p0.z;

        positions[9] = (float) p4.x;
        positions[10] = (float) p0.y;
        positions[11] = (float) p0.z;

        positions[12] = (float) p4.x;
        positions[13] = (float) p4.y;
        positions[14] = (float) p4.z;

        positions[15] = (float) p4.x;
        positions[16] = (float) p4.y;
        positions[17] = (float) p0.z;

        positions[18] = (float) p4.x;
        positions[19] = (float) p0.y;
        positions[20] = (float) p4.z;

        positions[21] = (float) p0.x;
        positions[22] = (float) p4.y;
        positions[23] = (float) p4.z;


        indices[0] = 4;
        indices[1] = 7;

        indices[2] = 6;
        indices[3] = 1;

        indices[4] = 2;
        indices[5] = 5;

        indices[6] = 3;
        indices[7] = 0;

        indices[8] = 2;
        indices[9] = 7;

        indices[10] = 0;
        indices[11] = 1;

        indices[12] = 5;
        indices[13] = 4;

        indices[14] = 3;
        indices[15] = 6;

        indices[16] = 5;
        indices[17] = 3;

        indices[18] = 2;
        indices[19] = 0;

        indices[20] = 4;
        indices[21] = 6;

        indices[22] = 7;
        indices[23] = 1;

        this.model = new Model3D(new MeshModel(positions, indices));
    }
}
