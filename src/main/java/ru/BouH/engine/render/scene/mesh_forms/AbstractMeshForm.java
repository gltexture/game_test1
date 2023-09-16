package ru.BouH.engine.render.scene.mesh_forms;

import ru.BouH.engine.render.scene.components.MeshModel;
import ru.BouH.engine.render.scene.components.Model3D;

import java.util.List;

public abstract class AbstractMeshForm {
    private final FormUtils formUtils;
    protected Model3D model;

    public AbstractMeshForm() {
        this.formUtils = new FormUtils();
        this.model = null;
    }

    public Model3D getMeshInfo() {
        return this.model;
    }

    protected void buildMeshModel(List<Float> positions, List<Integer> indices, List<Float> textureCoordinates, List<Float> normals) {
        float[] pArray = this.getFormUtils().reorderFloats(positions);
        int[] iArray = this.getFormUtils().reorderInts(indices);
        float[] tArray = this.getFormUtils().reorderFloats(textureCoordinates);
        float[] nArray = this.getFormUtils().reorderFloats(normals);
        this.model = new Model3D(this.generateModel(pArray, iArray, tArray, nArray));
    }

    @SuppressWarnings("all")
    private MeshModel generateModel(float[] pArray, int[] iArray, float[] tArray, float[] nArray) {
        boolean b1 = nArray == null;
        boolean b2 = tArray == null;
        if (b1) {
            if (b2) {
                return new MeshModel(pArray, iArray);
            }
            return new MeshModel(pArray, iArray, tArray);
        }
        return new MeshModel(pArray, iArray, tArray, nArray);
    }

    public void clearMesh() {
        this.getMeshInfo().clean();
        this.model = null;
    }

    protected FormUtils getFormUtils() {
        return this.formUtils;
    }

    public boolean hasMesh() {
        return this.getMeshInfo() != null;
    }

    public static class FormUtils {
        public float[] reorderFloats(List<Float> floats) {
            if (floats == null) {
                return null;
            }
            float[] f1 = new float[floats.size()];
            for (int i = 0; i < f1.length; i++) {
                f1[i] = floats.get(i);
            }
            return f1;
        }

        public int[] reorderInts(List<Integer> integers) {
            if (integers == null) {
                return null;
            }
            int[] i1 = new int[integers.size()];
            for (int i = 0; i < i1.length; i++) {
                i1[i] = integers.get(i);
            }
            return i1;
        }
    }
}
