package ru.BouH.engine.render.utils;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.utils.loaders.OBJLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static String loadShader(String path) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = Utils.class.getResourceAsStream("/shaders/" + path)) {
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

    public static InputStream loadFile(String path) {
        return Utils.class.getResourceAsStream(path);
    }

    public static InputStream loadTexture(String path) {
        return Utils.loadFile("/textures/" + path);
    }

    public static MeshModel loadMesh(String path) {
        return OBJLoader.loadMesh(path);
    }
}
