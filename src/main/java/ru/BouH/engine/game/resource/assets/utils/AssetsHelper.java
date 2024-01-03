package ru.BouH.engine.game.resource.assets.utils;

import ru.BouH.engine.game.resource.assets.obj.OBJLoader;
import ru.BouH.engine.render.scene.components.MeshModel;

import java.io.InputStream;

public class AssetsHelper {
    public static MeshModel loadMesh(String path) {
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
