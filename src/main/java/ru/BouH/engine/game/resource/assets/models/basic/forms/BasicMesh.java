package ru.BouH.engine.game.resource.assets.models.basic.forms;

import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.IFormat;

public interface BasicMesh <T extends IFormat> {
    Mesh<T> generateMesh();
}
