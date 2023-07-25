package ru.BouH.engine.proxy.init;
import ru.BouH.engine.render.scene.renderers.RenderEntity;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;

public class EntitiesInit {
    public static EntityModel.EntityForm entityCube;
    public static EntityModel.EntityForm entityCube2;
    public static EntityModel.EntityForm entityCube3;
    public static EntityModel.EntityForm entityLamp;

    public static void init() {
        EntitiesInit.entityCube = new EntityModel.EntityForm(new RenderEntity(), "prop/cube.obj").setTexture("props/cube.png");
        EntitiesInit.entityCube2 = new EntityModel.EntityForm(new RenderEntity(), "prop/cube.obj").setGradient();
        EntitiesInit.entityCube3 = new EntityModel.EntityForm(new RenderEntity(), "prop/cube.obj").setTexture("props/cube3.png");
        EntitiesInit.entityLamp = new EntityModel.EntityForm(new RenderEntity(), "prop/sphere.obj");
    }
}
