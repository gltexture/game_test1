package ru.BouH.engine.render.scene.render.world;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.physx.components.MaterialType;
import ru.BouH.engine.physx.world.surface.Terrain;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.render.scene.SceneRender;

import java.util.ArrayList;
import java.util.List;

public class RenderTerrain {
    private final Terrain terrain;
    private Model3DInfo mesh;
    private final Texture texture;

    public RenderTerrain(Terrain terrain) {
        this.terrain = terrain;
        this.mesh = this.buildMesh();
        this.mesh.setPosition(-terrain.getSize() / 2, terrain.getStartY(), -terrain.getSize() / 2);
        this.texture = Texture.createTexture("terrain/asphalt.png");
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

    public Model3DInfo getMesh() {
        return this.mesh;
    }

    public void onRender(SceneRender sceneRender) {
        sceneRender.getUniformProgram().setUniform("use_texture", 1);
        sceneRender.getUniformProgram().setUniform("specular_power", 1.0f);
        sceneRender.setMaterialUniform("material", Game.getGame().getProxy().getTypeAmbientMaterialMap().get(MaterialType.Grass));
        GL30.glBindVertexArray(this.mesh.getMesh().getVao());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        sceneRender.getUniformProgram().setUniform("texture_sampler", 0);
        this.texture.performTexture();
        GL30.glDrawElements(GL30.GL_TRIANGLES, this.mesh.getMesh().getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void onStartRender() {
    }

    public void onStopRender() {
        this.mesh.getMesh().cleanMesh();
    }
}
