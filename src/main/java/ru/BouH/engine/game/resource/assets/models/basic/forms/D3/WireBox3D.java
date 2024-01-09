package ru.BouH.engine.game.resource.assets.models.basic.forms.D3;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.basic.forms.BasicMesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.math.MathHelper;

public class WireBox3D implements BasicMesh<Format3D> {
    private final Vector3d min;
    private final Vector3d max;

    public WireBox3D(btVector3 min, btVector3 max) {
        this(MathHelper.convert(min), MathHelper.convert(max));
    }

    public WireBox3D(Vector3d min, Vector3d max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Mesh<Format3D> generateMesh() {
        Mesh<Format3D> mesh = new Mesh<>(new Format3D());
        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.min.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.min.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.max.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putPositionValue((float) this.min.x);
        mesh.putPositionValue((float) this.max.y);
        mesh.putPositionValue((float) this.max.z);

        mesh.putIndexValue(0);
        mesh.putIndexValue(1);

        mesh.putIndexValue(1);
        mesh.putIndexValue(2);

        mesh.putIndexValue(2);
        mesh.putIndexValue(3);

        mesh.putIndexValue(3);
        mesh.putIndexValue(0);

        mesh.putIndexValue(4);
        mesh.putIndexValue(5);

        mesh.putIndexValue(5);
        mesh.putIndexValue(6);

        mesh.putIndexValue(6);
        mesh.putIndexValue(7);

        mesh.putIndexValue(7);
        mesh.putIndexValue(4);

        mesh.putIndexValue(0);
        mesh.putIndexValue(4);

        mesh.putIndexValue(1);
        mesh.putIndexValue(5);

        mesh.putIndexValue(2);
        mesh.putIndexValue(6);

        mesh.putIndexValue(3);
        mesh.putIndexValue(7);

        mesh.bakeMesh();
        return mesh;
    }
}