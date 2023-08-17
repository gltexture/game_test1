package LEGACY.collision.objects;

import LEGACY.collision.IBox;
import LEGACY.collision.ICollision;
import LEGACY.collision.IFormedCollision;
import org.joml.Math;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;

import java.util.ArrayList;
import java.util.List;

public class CollisionOrientedBox3D extends AbstractCollision implements IBox {
    private final Vector3d size;

    public CollisionOrientedBox3D(Vector3d position, Vector3d offset, Vector3d rotation, Vector3d size) {
        super(position, offset, rotation, 1.0d);
        this.size = size;
    }

    public List<Vector3d> calcVertices(Vector3d p, Vector3d o) throws GameException {
        if (this.getSize() == null) {
            throw new GameException("Attempted to perform Invalid Size-Vector!");
        }
        Vector3d c = new Vector3d(p).add(o);
        Vector3d s = new Vector3d(this.getSize()).mul(this.getScale());
        Vector3d p0 = new Vector3d(c).sub(new Vector3d(s).div(2.0d));
        Vector3d p4 = new Vector3d(c).add(new Vector3d(s).div(2.0d));
        ArrayList<Vector3d> arrayList = this.getVector3ds(p0, p4);
        for (int i = 0; i < arrayList.size(); i++) {
            Vector3d vector3d = new Vector3d(arrayList.get(i));
            vector3d.sub(c);
            Quaterniond q1 = new Quaterniond();
            q1.rotateXYZ(Math.toRadians(this.getRotation().x), Math.toRadians(this.getRotation().y), Math.toRadians(this.getRotation().z));
            vector3d = q1.transform(vector3d);
            vector3d.add(c);
            arrayList.set(i, vector3d);
        }
        return arrayList;
    }

    private ArrayList<Vector3d> getVector3ds(Vector3d p0, Vector3d p4) {
        final Vector3d sized = this.getSize();
        return new ArrayList<Vector3d>() {{
            add(p0);
            add(new Vector3d(p0).add(new Vector3d(sized.x, 0, 0)));
            add(new Vector3d(p0).add(new Vector3d(0, sized.y, 0)));
            add(new Vector3d(p0).add(new Vector3d(0, 0, sized.z)));
            add(p4);
            add(new Vector3d(p4).sub(new Vector3d(sized.x, 0, 0)));
            add(new Vector3d(p4).sub(new Vector3d(0, sized.y, 0)));
            add(new Vector3d(p4).sub(new Vector3d(0, 0, sized.z)));
        }};
    }

    public Vector3d getSize() {
        return this.size;
    }

    @Override
    public IFormedCollision getForm() {
        return this;
    }

    @Override
    public ICollision copy() {
        CollisionOrientedBox3D collisionOrientedBox3D = new CollisionOrientedBox3D(new Vector3d(this.getPosition()), new Vector3d(this.getOffset()), new Vector3d(this.getRotation()), this.size);
        try {
            collisionOrientedBox3D.refreshVertices();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
        return collisionOrientedBox3D;
    }

    @Override
    public boolean equalsTo(ICollision iCollision) {
        return this.equals(iCollision);
    }
}
