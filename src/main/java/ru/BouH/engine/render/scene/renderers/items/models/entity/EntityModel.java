package ru.BouH.engine.render.scene.renderers.items.models.entity;
import org.joml.Vector3d;
import ru.BouH.engine.game.init.Game;
import ru.BouH.engine.render.scene.components.Model3D;
import ru.BouH.engine.render.scene.components.Model3DInfo;
import ru.BouH.engine.render.scene.components.Texture;
import ru.BouH.engine.render.scene.renderers.IRenderFabric;
import ru.BouH.engine.render.utils.Utils;

public class EntityModel {
    private final EntityForm entityForm;
    private final Model3DInfo model3DInfo;

    public EntityModel(EntityForm entityForm) {
        this.entityForm = entityForm;
        this.model3DInfo = new Model3DInfo(this.entityForm.getMesh());
    }

    public EntityForm getPropForm() {
        return this.entityForm;
    }

    public Model3DInfo getMeshModel() {
        return this.model3DInfo;
    }

    public static class EntityForm {
        private Model3D model;
        private final EntityTexture entityTexture;
        private final IRenderFabric iRenderFabric;

        private EntityForm(IRenderFabric iRenderFabric) {
            this.iRenderFabric = iRenderFabric;
            this.model = null;
            this.entityTexture = new EntityTexture();
        }

        public EntityForm(IRenderFabric iRenderFabric, String modelName) {
            this(iRenderFabric);
            this.loadModel(modelName);
        }

        private void loadModel(String modelName) {
            Game.getGame().getLogManager().log("Loading model " + modelName);
            long startedTime = System.currentTimeMillis();
            this.model = Utils.loadMesh(modelName);
            Game.getGame().getLogManager().log("Model loaded " + modelName + " in " + (System.currentTimeMillis() - startedTime) / 100.0f + " sec");
        }

        public EntityForm setTexture(String textureName) {
            this.entityTexture.setTexture(Texture.createTexture(textureName));
            return this;
        }

        public EntityForm setColors(Vector3d vector3d) {
            this.entityTexture.setColors(vector3d);
            return this;
        }

        public EntityForm setGradient() {
            this.entityTexture.setGradient();
            return this;
        }

        public IRenderFabric getiRenderFabric() {
            return this.iRenderFabric;
        }

        public Model3D getMesh() {
            return this.model;
        }

        public EntityTexture getEntityTexture() {
            return this.entityTexture;
        }
    }

    public static class EntityTexture {
        private Texture texture;
        private Vector3d colors;
        private TextureType textureType;

        public EntityTexture() {
            this.texture = null;
            this.colors = null;
            this.textureType = TextureType.ERROR;
        }

        public void setTexture(Texture texture) {
            this.texture = texture;
            this.colors = null;
            this.textureType = TextureType.TEXTURE;
        }

        public void setColors(Vector3d colors) {
            this.colors = colors;
            this.texture = null;
            this.textureType = TextureType.RGB;
        }

        public void setGradient() {
            this.textureType = TextureType.GRADIENT;
        }

        public Texture getTexture() {
            return this.texture;
        }

        public Vector3d getColors() {
            return this.colors;
        }

        public TextureType getTextureType() {
            return this.textureType;
        }

        public enum TextureType {
            TEXTURE(0),
            RGB(1),
            GRADIENT(2),
            ERROR(-1);

            private final int i;
            TextureType(int i) {
                this.i = i;
            }

            public int getI() {
                return this.i;
            }
        }
    }
}
