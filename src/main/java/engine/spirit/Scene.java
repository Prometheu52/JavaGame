package engine.spirit;

public abstract class Scene {

    protected Camera camera;

    public Scene(){

    }

    public void init(){

    }

    public abstract void update(float deltaTime);

}
