package ru.BouH.engine.render.scene.render.models.entity;

import org.joml.Vector4d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.components.MaterialType;
import ru.BouH.engine.proxy.init.EntitiesInit;
import ru.BouH.engine.render.scene.components.AmbientMaterial;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class EntityForm {
    private final String modelName;
    private final Model3D model;
    private Texture texture;
    private final AmbientMaterial ambientMaterial;
    private Vector4d colours;

    public EntityForm(@NotNull String modelName, MaterialType materialType, Texture texture) {
        this(modelName, materialType);
        this.texture = texture;
    }

    public EntityForm(String modelName, MaterialType materialType, Vector4d colours) {
        this(modelName, materialType);
        this.colours = colours;
    }

    private EntityForm(String modelName, MaterialType materialType) {
        this.modelName = modelName;
        this.ambientMaterial = Game.getGame().getProxy().getTypeAmbientMaterialMap().get(materialType);
        Game.getGame().getLogManager().log("Loading model " + this.getModelName());
        long startedTime = System.currentTimeMillis();
        this.model = Utils.loadMesh(modelName);
        Game.getGame().getLogManager().log("Model loaded " + this.getModelName() + " in " + (System.currentTimeMillis() - startedTime) / 100.0f + " sec");
    }

    public void setTexture(Texture texture) {
        this.colours = null;
        this.texture = texture;
    }

    public void setColours(Vector4d colours) {
        this.texture = null;
        this.colours = colours;
    }

    public AmbientMaterial getMaterial() {
        return this.ambientMaterial;
    }

    public String getModelName() {
        return this.modelName;
    }

    public Model3D getMesh() {
        return this.model;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector4d getColours() {
        return this.colours;
    }
}
