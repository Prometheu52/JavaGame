package engine.spirit;

import Components.SpriteRenderer;
import org.joml.Vector2f;
import utils.AssetPool;

public class LevelEditorScene extends Scene{
    private final String TEST_IMG_PATH = "assets/images/mario_texture.png";
    private final String DEFAULT_SHADER_PATH = "assets/shaders/default.glsl";

    public LevelEditorScene() {
        System.out.println("LEVEL EDITOR SCENE");
    }

    @Override
    public void init(){
        this.camera = new Camera(new Vector2f(-250, 0));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object2", new Transform(new Vector2f(400,100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage2.png")));
        this.addGameObjectToScene(obj2);

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
