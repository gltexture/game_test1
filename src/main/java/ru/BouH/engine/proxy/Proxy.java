package ru.BouH.engine.proxy;

import ru.BouH.engine.game.init.controller.KeyBinding;
import ru.BouH.engine.physx.PhysX;
import ru.BouH.engine.physx.entities.PhysEntity;
import ru.BouH.engine.physx.entities.living.player.EntityPlayerSP;
import ru.BouH.engine.render.scene.renderers.items.models.entity.EntityModel;
import ru.BouH.engine.render.screen.Screen;

public class Proxy {
    private final PhysX physX;
    private final Screen screen;

    public Proxy(PhysX physX, Screen screen) {
        this.physX = physX;
        this.screen = screen;
    }

    public void addEntityInWorlds(PhysEntity physEntity, EntityModel.EntityForm entityForm) {
        this.physX.getWorld().addEntity(physEntity);
        this.screen.getRenderWorld().addEntity(physEntity, entityForm == null ? null : new EntityModel(entityForm));
    }

    public void addLocalPlayer() {
        EntityPlayerSP entityPlayerSP = this.physX.getWorld().getLocalPlayer();
        this.addEntityInWorlds(entityPlayerSP, null);
    }

    public void tickWorlds() {
        this.physX.getWorld().onWorldUpdate();
        this.screen.getRenderWorld().tickWorld();
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
}
