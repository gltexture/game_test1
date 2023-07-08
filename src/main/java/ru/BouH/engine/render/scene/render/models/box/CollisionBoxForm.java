package ru.BouH.engine.render.scene.render.models.box;

import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;

import java.util.ArrayList;
import java.util.List;

public class CollisionBoxForm {
    private Model3DInfo model;

    public CollisionBoxForm(CollisionBox3D collisionBox3D) {
        this.genBoxModel(collisionBox3D);
    }

    public void genBoxModel(CollisionBox3D collisionBox3D) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        positions.add((float) collisionBox3D.getMinX());
        positions.add((float) collisionBox3D.getMinY());
        positions.add((float) collisionBox3D.getMinZ());

        positions.add((float) collisionBox3D.getMaxX());
        positions.add((float) collisionBox3D.getMinY());
        positions.add((float) collisionBox3D.getMinZ());

        positions.add((float) collisionBox3D.getMaxX());
        positions.add((float) collisionBox3D.getMaxY());
        positions.add((float) collisionBox3D.getMinZ());

        positions.add((float) collisionBox3D.getMinX());
        positions.add((float) collisionBox3D.getMaxY());
        positions.add((float) collisionBox3D.getMinZ());

        positions.add((float) collisionBox3D.getMinX());
        positions.add((float) collisionBox3D.getMinY());
        positions.add((float) collisionBox3D.getMaxZ());

        positions.add((float) collisionBox3D.getMaxX());
        positions.add((float) collisionBox3D.getMinY());
        positions.add((float) collisionBox3D.getMaxZ());

        positions.add((float) collisionBox3D.getMaxX());
        positions.add((float) collisionBox3D.getMaxY());
        positions.add((float) collisionBox3D.getMaxZ());

        positions.add((float) collisionBox3D.getMinX());
        positions.add((float) collisionBox3D.getMaxY());
        positions.add((float) collisionBox3D.getMaxZ());

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
