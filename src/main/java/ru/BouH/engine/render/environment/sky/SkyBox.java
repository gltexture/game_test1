package ru.BouH.engine.render.environment.sky;

import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.objects.texture.samples.PNGTexture;
import ru.BouH.engine.render.utils.Utils;

public class SkyBox {
    private final Model3D model3D;
    private final PNGTexture texture;

    public SkyBox(String textureName) {
        this.model3D = new Model3D(Utils.loadMesh("sky/skyCube.obj"));
        this.texture = PNGTexture.createTexture(textureName);
    }

    public Model3D getModel3DInfo() {
        return this.model3D;
    }

    public PNGTexture getTexture() {
        return this.texture;
    }
}
