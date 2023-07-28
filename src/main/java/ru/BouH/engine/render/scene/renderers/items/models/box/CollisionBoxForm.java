package ru.BouH.engine.render.scene.renderers.items.models.box;

import org.joml.Vector3d;
import ru.BouH.engine.physx.components.CollisionBox3D;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;

import java.util.ArrayList;
import java.util.List;

public class CollisionBoxForm {
    private Model3DInfo model;
    private CollisionBox3D collisionBox3D;

    public CollisionBoxForm(PhysEntity physEntity) {
        this.setCollisionBox3D(physEntity.getPosition(), physEntity.getCollisionBox3D());
    }

    private void genBoxModel(CollisionBox3D collisionBox3D) {
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

    public CollisionBox3D getCollisionBox3D() {
        return this.collisionBox3D;
    }

    public void setCollisionBox3D(Vector3d translate, CollisionBox3D collisionBox3D) {
        if (this.getMeshInfo() != null) {
            this.getMeshInfo().getModel3D().cleanMesh();
        }
        this.collisionBox3D = collisionBox3D.copy();
        this.genBoxModel(collisionBox3D);
        this.getMeshInfo().setPosition(translate);
    }

    public Model3DInfo getMeshInfo() {
        return this.model;
    }
}
