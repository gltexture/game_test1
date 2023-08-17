package ru.BouH.engine.render.scene.primitive_forms;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physx.collision.objects.OBB;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;

import java.util.ArrayList;
import java.util.List;

public class CollisionOBBoxForm implements IForm {
    private Model3D model;
    private OBB collisionBox3D;

    public CollisionOBBoxForm(OBB obb) {
        this.setCollisionBox3D(obb);
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

    public OBB getCollisionBox3D() {
        return this.collisionBox3D;
    }

    public void setCollisionBox3D(OBB obb) {
        if (this.hasMesh()) {
            this.getMeshInfo().clean();
        }
        this.collisionBox3D = obb;
        try {
            this.genBoxModel(collisionBox3D);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public Model3D getMeshInfo() {
        return this.model;
    }
}
