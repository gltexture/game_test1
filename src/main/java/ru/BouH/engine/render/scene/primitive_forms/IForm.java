package ru.BouH.engine.render.scene.primitive_forms;

import ru.BouH.engine.render.scene.components.Model3D;

public interface IForm {
    Model3D getMeshInfo();
    default boolean hasMesh() {
        return this.getMeshInfo() != null;
    }
}
