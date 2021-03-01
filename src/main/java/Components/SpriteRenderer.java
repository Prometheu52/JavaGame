package Components;

import engine.spirit.Component;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void start(){

    }

    public Vector4f getColor() {
        return color;
    }

}
