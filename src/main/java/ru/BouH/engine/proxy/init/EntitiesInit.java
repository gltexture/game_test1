package ru.BouH.engine.proxy.init;

import org.joml.Vector4d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.components.MaterialType;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.render.entities.EntityRenderer;
import ru.BouH.engine.render.scene.render.models.entity.EntityForm;

public class EntitiesInit {
    public static Texture textureCube;
    public static EntityForm entityCube;
    public static EntityForm entityLamp;

    public static void init() {
        EntitiesInit.textureCube = Texture.createTexture("props/cube.png");
        EntitiesInit.entityCube = new EntityForm("prop/cube.obj", MaterialType.Grass, EntitiesInit.textureCube);
        EntitiesInit.entityLamp = new EntityForm("prop/sphere.obj", MaterialType.Rock, new Vector4d(1.0f, 1.0f, 1.0f, 1.0f));
    }
}
