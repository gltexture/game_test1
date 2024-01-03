package ru.BouH.engine.game.resource;

import ru.BouH.engine.game.resource.assets.IAssets;
import ru.BouH.engine.game.resource.assets.RenderAssets;
import ru.BouH.engine.game.resource.assets.ShaderAssets;

import java.util.*;

public class ResourceManager {
    private final List<IAssets> assetsObjects;
    public static RenderAssets renderAssets = null;
    public static ShaderAssets shaderAssets = null;

    public ResourceManager() {
        this.assetsObjects = new ArrayList<>();
        this.init();
    }

    public void init() {
        ResourceManager.renderAssets = new RenderAssets();
        ResourceManager.shaderAssets = new ShaderAssets();
        this.addAsset(ResourceManager.renderAssets);
        this.addAsset(ResourceManager.shaderAssets);
    }

    public List<IAssets> getAssetsObjects() {
        return this.assetsObjects;
    }

    private void addAsset(IAssets asset) {
        this.assetsObjects.add(asset);
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        Iterator<IAssets> assetsIterator = this.assetsObjects.iterator();
        while (assetsIterator.hasNext()) {
            IAssets assets = assetsIterator.next();
            if (assets.parallelLoading()) {
                Thread thread = new Thread(assets::load);
                set.add(thread);
                assetsIterator.remove();
            }
        }
        return set;
    }

    public void loadAllAssets() {
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        for (IAssets assets : this.assetsObjects) {
            assets.load();
        }
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
