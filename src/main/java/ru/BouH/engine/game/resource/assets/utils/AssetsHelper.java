package ru.BouH.engine.game.resource.assets.utils;

import ru.BouH.engine.game.resource.assets.models.Mesh;
import ru.BouH.engine.game.resource.assets.models.formats.Format3D;
import ru.BouH.engine.game.resource.assets.obj.OBJLoader;

import java.io.InputStream;

public class AssetsHelper {
    public static Mesh<Format3D> loadMesh(String path) {
        return OBJLoader.loadMesh(path);
    }

    public static InputStream loadFile(String path) {
        return AssetsHelper.class.getResourceAsStream(path);
    }

    public static InputStream loadTexture(String path) {
        return AssetsHelper.loadFile("/textures/" + path);
    }

    public static InputStream loadNormalMapTexture(String path) {
        return AssetsHelper.loadTexture("normals/" + path);
    }
}
