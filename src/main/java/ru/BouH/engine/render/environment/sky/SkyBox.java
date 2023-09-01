package ru.BouH.engine.render.environment.sky;

import org.joml.Vector2i;
import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.programs.CubeMapSample;
import ru.BouH.engine.render.utils.Utils;

public class SkyBox {
    private final Model3D model3D;
    private final CubeMapSample cubeMap;
    private static final float[] skyboxPos = {
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
    };

    private static final int[] skyboxInd = new int[]{
            0, 1, 3, 3, 1, 2,
            4, 0, 3, 5, 4, 3,
            3, 2, 7, 5, 3, 7,
            6, 1, 0, 6, 0, 4,
            2, 1, 6, 2, 6, 7,
            7, 6, 4, 7, 4, 5
    };

    public SkyBox(String textureName) {
        this.model3D = new Model3D(new MeshModel(SkyBox.skyboxPos, SkyBox.skyboxInd));
        this.cubeMap = new CubeMapSample(textureName);
    }

    public Model3D getModel3DInfo() {
        return this.model3D;
    }

    public CubeMapSample getCubeMap() {
        return this.cubeMap;
    }
}
