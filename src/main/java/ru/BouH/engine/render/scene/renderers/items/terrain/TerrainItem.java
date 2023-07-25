package ru.BouH.engine.render.scene.renderers.items.terrain;

import ru.BouH.engine.physx.world.surface.Terrain;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.scene.renderers.RenderTerrain;
import ru.BouH.engine.render.scene.renderers.items.IRenderItem;

import java.util.ArrayList;
import java.util.List;

public class TerrainItem implements IRenderItem {
    private final Terrain terrain;
    private final Model3DInfo mesh;
    private final Texture texture;

    public TerrainItem(Terrain terrain) {
        this.terrain = terrain;
        this.mesh = this.buildMesh();
        this.mesh.setPosition(-terrain.getSize() / 2, terrain.getStartY(), -terrain.getSize() / 2);
        this.texture = Texture.createTexture("terrain/asphalt.png");
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public Model3DInfo getMesh() {
        return this.mesh;
    }

    private Model3DInfo buildMesh() {
        List<Float> positions = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < this.terrain.getSize(); i++) {
            for (int j = 0; j < this.terrain.getSize(); j++) {
                positions.add((float) i);
                positions.add(0.0f);
                positions.add((float) j);
                textureCoordinates.add(0.0f);
                textureCoordinates.add(0.0f);

                positions.add((float) i + 1.0f);
                positions.add(0.0f);
                positions.add((float) j);
                textureCoordinates.add(1.0f);
                textureCoordinates.add(0.0f);

                positions.add((float) i);
                positions.add(0.0f);
                positions.add((float) j + 1.0f);
                textureCoordinates.add(0.0f);
                textureCoordinates.add(1.0f);

                positions.add((float) i + 1.0f);
                positions.add(0.0f);
                positions.add((float) j + 1.0f);
                textureCoordinates.add(1.0f);
                textureCoordinates.add(1.0f);

                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 1));
                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 2));
                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 3));
                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 2));
                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 1));
                indices.add((int) (4 * i + (4 * this.terrain.getSize()) * j + 0));

                normals.add(0.0f);
                normals.add(1.0f);
                normals.add(1.0f);

                normals.add(0.0f);
                normals.add(1.0f);
                normals.add(1.0f);

                normals.add(0.0f);
                normals.add(1.0f);
                normals.add(1.0f);

                normals.add(0.0f);
                normals.add(1.0f);
                normals.add(1.0f);
            }
        }

        float[] f1 = new float[positions.size()];
        int[] i1 = new int[indices.size()];
        float[] f2 = new float[textureCoordinates.size()];
        float[] f3 = new float[normals.size()];

        for (int i = 0; i < f1.length; i++) {
            f1[i] = positions.get(i);
        }

        for (int i = 0; i < i1.length; i++) {
            i1[i] = indices.get(i);
        }

        for (int i = 0; i < f2.length; i++) {
            f2[i] = textureCoordinates.get(i);
        }

        for (int i = 0; i < f3.length; i++) {
            f3[i] = normals.get(i);
        }
        return new Model3DInfo(new Model3D(f1, i1, f2, f3));
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public IRenderFabric renderFabric() {
        return new RenderTerrain();
    }
}
