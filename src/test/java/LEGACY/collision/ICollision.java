package LEGACY.collision;

import org.joml.Vector3d;
import ru.BouH.engine.game.exception.GameException;

public interface ICollision {
    void updateCollision() throws GameException;
    void setScale(double scale);
    void setPosition(Vector3d vector3d);
    void setRotation(Vector3d vector3d);
    void setOffset(Vector3d vector3d);
    double getScale();
    Vector3d getPosition();
    Vector3d getRotation();
    Vector3d getOffset();
    ICollision copy();
    boolean equalsTo(ICollision iCollision);
}
