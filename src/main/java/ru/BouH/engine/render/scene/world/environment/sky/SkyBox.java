package ru.BouH.engine.render.scene.world.environment.sky;

import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.utils.Utils;

public class SkyBox {
    private final Model3DInfo model3DInfo;
    private final Texture texture;

    public SkyBox(String textureName) {
        this.model3DInfo = new Model3DInfo(Utils.loadMesh("sky/skyCube.obj"));
        this.texture = Texture.createTexture("environment/skybox/" + textureName);
    }

    public Model3DInfo getModel3DInfo() {
        return this.model3DInfo;
    }

    public Texture getTexture() {
        return this.texture;
    }
}
