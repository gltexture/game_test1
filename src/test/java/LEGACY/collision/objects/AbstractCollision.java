package LEGACY.collision.objects;

import LEGACY.collision.ICollision;
import LEGACY.collision.IFormedCollision;
import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCollision implements ICollision, IFormedCollision {
    private Vector3d rotation;
    private Vector3d position;
    private Vector3d offset;
    private List<Vector3d> verticesList;
    private double scale;

    public AbstractCollision(Vector3d position, Vector3d offset, Vector3d rotation, double scale) {
        this.verticesList = new ArrayList<>();
        this.position = position;
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
    }

    public abstract List<Vector3d> calcVertices(Vector3d p, Vector3d o) throws GameException;

    public void updateCollision() throws GameException {
        this.refreshVertices(this.getPosition(), this.getOffset());
    }

    public void refreshVertices() throws GameException {
        this.setVerticesList(this.calcVertices(this.getPosition(), this.getOffset()));
    }

    private void refreshVertices(Vector3d p, Vector3d o) throws GameException {
        this.setVerticesList(this.calcVertices(p, o));
    }

    public void setVerticesList(List<Vector3d> verticesList) {
        this.verticesList = verticesList;
    }

    public List<Vector3d> getVerticesList() {
        return this.verticesList;
    }

    public void setOffset(Vector3d offset) {
        this.offset = offset;
    }

    public void setOffset(double x, double y, double z) {
        this.offset.set(x, y, z);
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        try {
            this.refreshVertices();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public Vector3d getOffset() {
        return this.offset;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
        try {
            this.refreshVertices();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRotation(double x, double y, double z) {
        this.getRotation().set(x, y, z);
        try {
            this.refreshVertices();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPosition(Vector3d position) {
        this.position = position;
        try {
            this.refreshVertices();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPosition(double x, double y, double z) {
        this.setPosition(new Vector3d(x, y, z));
    }
}
