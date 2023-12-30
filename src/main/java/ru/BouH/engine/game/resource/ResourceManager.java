package ru.BouH.engine.game.resource;

import ru.BouH.engine.game.resource.assets.IAssets;
import ru.BouH.engine.game.resource.assets.RenderAssets;
import ru.BouH.engine.game.resource.assets.ShaderAssets;

import java.util.*;

public class ResourceManager {
    public static final ResourceManager instance = new ResourceManager();
    private final List<IAssets> assetsObjects;
    private final RenderAssets renderAssets;
    private final ShaderAssets shaderAssets;

    public ResourceManager() {
        this.assetsObjects = new ArrayList<>();
        this.renderAssets = new RenderAssets();
        this.shaderAssets = new ShaderAssets();
        this.addAsset(this.renderAssets);
        this.addAsset(this.shaderAssets);
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

        for (String s : this.getShaderAssets().world.getFragmentShader().getUniforms()) {
            System.out.println(s);
        }
    }

    public RenderAssets getRenderAssets() {
        return this.renderAssets;
    }

    public ShaderAssets getShaderAssets() {
        return this.shaderAssets;
    }
}
