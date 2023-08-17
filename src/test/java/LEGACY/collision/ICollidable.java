package LEGACY.collision;

import java.util.List;

public interface ICollidable {
    List<ICollidable> getCollideList();
    ICollision getCollision();
    boolean isStatic();
    default boolean hasCollision() {
        return this.getCollision() != null;
    }
}
