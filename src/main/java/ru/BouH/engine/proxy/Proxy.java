package ru.BouH.engine.proxy;

import org.joml.Vector4d;
import ru.BouH.engine.game.init.controller.KeyBinding;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.physx.components.MaterialType;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.proxy.entity.EntityRenderInfo;
import ru.BouH.engine.proxy.lights.Light;
import ru.BouH.engine.proxy.lights.LightType;
import ru.BouH.engine.render.scene.components.AmbientMaterial;
import ru.BouH.engine.render.scene.render.entities.init.RenderEntity;
import ru.BouH.engine.render.scene.render.models.entity.EntityModel;
import ru.BouH.engine.render.screen.Screen;

import java.util.HashMap;
import java.util.Map;

public class Proxy {
    private final Map<MaterialType, AmbientMaterial> typeAmbientMaterialMap;
    private final PhysX physX;
    private final Screen screen;

    public Proxy(PhysX physX, Screen screen) {
        this.physX = physX;
        this.screen = screen;
        this.typeAmbientMaterialMap = new HashMap<>();
        this.typeAmbientMaterialMap.put(MaterialType.Grass, new AmbientMaterial(new Vector4d(0.5f, 0.5f, 0.5f, 1.0f), new Vector4d(0.5f, 0.5f, 0.5f, 1.0f), 0.0f));
        this.typeAmbientMaterialMap.put(MaterialType.Rock, new AmbientMaterial(new Vector4d(0.0f, 0.0f, 0.0f, 1.0f), new Vector4d(0.0f, 0.0f, 0.0f, 1.0f), 0.0f));
    }

    public Map<MaterialType, AmbientMaterial> getTypeAmbientMaterialMap() {
        return this.typeAmbientMaterialMap;
    }

    public void addEntityInWorlds(PhysEntity physEntity, EntityRenderInfo entityRenderInfo) {
        this.physX.getWorld().addEntity(physEntity);
        RenderEntity renderEntity = new RenderEntity(this.screen.getRenderWorld(), physEntity, new EntityModel(entityRenderInfo.getEntityForm(), entityRenderInfo.getiRender()));
        this.screen.getRenderWorld().addEntity(renderEntity);
    }

    public void addKeyBinding(KeyBinding keybinding) {
        this.screen.getController().addKeyBinding(keybinding);
    }

    public void removeEntityFromWorlds(PhysEntity physEntity) {
        this.physX.getWorld().removeEntity(physEntity);
    }

    public void clearEntities() {
        this.physX.getWorld().getEntitySet().forEach(PhysEntity::setDead);
    }

    public Light createLight(LightType lightType) {
        return this.screen.getRenderWorld().createLight(lightType);
    }
}
