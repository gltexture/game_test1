package ru.BouH.engine.game.resource.assets.utils;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resource.assets.obj.OBJLoader;
import ru.BouH.engine.render.scene.components.MeshModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssetsHelper {
    public static String loadShader(String path) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = AssetsHelper.class.getResourceAsStream("/shaders/" + path)) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                }
                reader.close();
            } else {
                throw new IOException("Null input: " + path);
            }
        } catch (IOException ex) {
            Game.getGame().getLogManager().error(ex.getMessage());
        }
        return shaderSource.toString();
    }

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
