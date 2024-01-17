package ru.BouH.engine.game.resources.assets;

import org.joml.Vector2d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderBrushPlane;
import ru.BouH.engine.render.scene.fabric.RenderEntity;
import ru.BouH.engine.render.scene.fabric.RenderNull;
import ru.BouH.engine.render.scene.objects.items.EntityObject;
import ru.BouH.engine.render.scene.objects.items.LampObject;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;

public class RenderDataLoader implements IAssetsLoader {
    public RenderObjectData entityCube;
    public RenderObjectData entityCube2;
    public RenderObjectData entityLargeCube;
    public RenderObjectData entityLamp;
    public RenderObjectData player;
    public RenderObjectData plane;
    public RenderObjectData planeGround;

    @Override
    public void load(GameCache gameCache) {
        IEntityModelConstructor<WorldItem> entityModelConstructor = e -> {
            Plane4dBrush plane4dBrush = (Plane4dBrush) e;
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(plane4dBrush.getVertices()[0], plane4dBrush.getVertices()[1], plane4dBrush.getVertices()[2], plane4dBrush.getVertices()[3]));
        };

        Material grassPlane = new Material();
        grassPlane.setDiffuse(ResourceManager.renderAssets.grassTexture);

        Material brickPlane = new Material();
        brickPlane.setDiffuse(ResourceManager.renderAssets.bricksTexture);

        this.entityCube = new RenderObjectData(new RenderEntity(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityCube2 = new RenderObjectData(new RenderEntity(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);

        this.entityLargeCube = new RenderObjectData(new RenderEntity(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityLamp = new RenderObjectData(new RenderEntity(), LampObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.player = new RenderObjectData(new RenderNull(), EntityObject.class, ResourceManager.shaderAssets.world);
        this.plane = new RenderObjectData(new RenderBrushPlane(), EntityObject.class, ResourceManager.shaderAssets.world).setEntityModelConstructor(entityModelConstructor).setModelTextureScaling(new Vector2d(32.0d, 4.0d));
        this.planeGround = new RenderObjectData(new RenderBrushPlane(), EntityObject.class, ResourceManager.shaderAssets.world).setEntityModelConstructor(entityModelConstructor).setModelTextureScaling(new Vector2d(128.0d));

        this.plane.setOverObjectMaterial(brickPlane);
        this.planeGround.setOverObjectMaterial(grassPlane);
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 4;
    }
}
