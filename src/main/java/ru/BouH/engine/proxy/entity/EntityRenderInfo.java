package ru.BouH.engine.proxy.entity;

import ru.BouH.engine.render.scene.render.IRenderFabric;
import ru.BouH.engine.render.scene.render.models.entity.EntityForm;

public class EntityRenderInfo {
    private final String rName;
    private final EntityForm entityForm;
    private final Class<? extends IRenderFabric> iRender;

    public EntityRenderInfo(String rName, EntityForm entityForm, Class<? extends IRenderFabric> iRender) {
        this.rName = rName;
        this.entityForm = entityForm;
        this.iRender = iRender;
    }

    public Class<? extends IRenderFabric> getiRender() {
        return this.iRender;
    }

    public String getrName() {
        return this.rName;
    }

    public EntityForm getEntityForm() {
        return this.entityForm;
    }
}
