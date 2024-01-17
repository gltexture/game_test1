package ru.BouH.engine.game.resources.assets;

import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup knife;
    public MeshDataGroup cube;

    @Override
    public void load(GameCache gameCache) {
        this.cube = ResourceManager.createMesh("/models/cube/cube.obj");
        this.knife = ResourceManager.createMesh("/models/knife/knife.obj");
    }
    
    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 2;
    }
}
