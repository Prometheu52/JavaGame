package engine.spirit;


import Components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utils.AssetPool;

public class LevelEditorScene extends Scene{
    private final String TEST_IMG_PATH = "assets/images/testImage_mario.png";
    private final String DEFAULT_SHADER_PATH = "assets/shaders/default.glsl";

    public LevelEditorScene() {
        System.out.println("LEVEL EDITOR SCENE");
    }

    @Override
    public void init(){
        this.camera = new Camera(new Vector2f(-250, -100));

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float) (600 - xOffset * 2);
        float totalHeight = (float) (300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;
        float padding = 0;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX) + (padding * x);
                float yPos = yOffset + (y * sizeY) + (padding * y);

                GameObject go = new GameObject("Obj" + x + "" + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }

        loadResources();
    }

    private void loadResources(){
        AssetPool.getShader(DEFAULT_SHADER_PATH);
    }

    @Override
    public void update(float deltaTime) {
        //System.out.println("" + (1.0f / deltaTime) + "FPS");

        for (GameObject go : this.gameObjects){
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}
