package ru.BouH.engine.render.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.components.Face;
import ru.BouH.engine.render.scene.components.Model3D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    public static Model3D loadMesh(String path) {
        List<Vector3f> vert = new ArrayList<>();
        List<Vector2f> text = new ArrayList<>();
        List<Vector3f> norm = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        String fullPath = "/models/" + path;
        URL url = Utils.class.getResource(fullPath);
        if (url != null) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Utils.loadFile(fullPath)))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] tokens = line.split("\\s+");
                    switch (tokens[0]) {
                        case "v": {
                            Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                            vert.add(vec3f);
                            break;
                        }
                        case "vt": {
                            Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                            text.add(vec2f);
                            break;
                        }
                        case "vn": {
                            Vector3f vec3fNorm = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                            norm.add(vec3fNorm);
                            break;
                        }
                        case "f": {
                            Face face = new Face(tokens[1], tokens[2], tokens[3]);
                            faces.add(face);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                Game.getGame().getLogManager().error(e.toString());
            }
        } else {
            Game.getGame().getLogManager().error(fullPath + " not found");
        }
        return Utils.reorderLists(vert, text, norm, faces);
    }

    private static Model3D reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<Face> facesList) {
        List<Integer> indices = new ArrayList<>();
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];
        for (Face face : facesList) {
            Face.IdxGroup[] faceVertexIndices = face.getIdxGroups();
            for (Face.IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList, indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr;
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return new Model3D(posArr, indicesArr, textCoordArr, normArr);
    }
    private static void processFaceVertex(Face.IdxGroup indices, List<Vector2f> textCoordList, List<Vector3f> normList, List<Integer> indicesList, float[] texCoordArr, float[] normArr) {
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);
        if (indices.idxTextCoordinates >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoordinates);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }
}
