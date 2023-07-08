package ru.BouH.engine.render.scene.render.models.entity;

import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.render.IRenderFabric;
import ru.BouH.engine.render.scene.world.RenderWorld;

import java.lang.reflect.InvocationTargetException;

public class EntityModel {
    private final EntityForm entityForm;
    private final Model3DInfo model3DInfo;
    private IRenderFabric render;

    public EntityModel(EntityForm entityForm, Class<? extends IRenderFabric> iRender) {
        this.entityForm = entityForm;
        this.model3DInfo = new Model3DInfo(this.entityForm.getMesh());
        this.setRender(iRender);
    }

    public void setRender(Class<? extends IRenderFabric> iRender) {
        try {
            this.render = iRender.getConstructor(RenderWorld.class).newInstance(Game.getGame().getScreen().getRenderWorld());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Game.getGame().getLogManager().error(e.getMessage());
        }
    }

    public void setRender(IRenderFabric render) {
        this.render = render;
    }

    public EntityForm getPropForm() {
        return this.entityForm;
    }

    public IRenderFabric getRender() {
        return this.render;
    }

    public Model3DInfo getMeshModel() {
        return this.model3DInfo;
    }
}
