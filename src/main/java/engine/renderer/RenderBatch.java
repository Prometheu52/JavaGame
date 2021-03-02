package engine.renderer;

import Components.SpriteRenderer;
import engine.spirit.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    private final String DEFAULT_SHADER_PATH = "assets/shaders/default.glsl";

    /*  Vertex
    *   --------
    *   Position            Color                           TextureCoordinates      TextureID
    *   float, float,       float, float, float, float      float, float            float
    * */
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;


    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numberOfSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textureList;
    private int vaoID, vboID;
    private int maxBathSize;
    private Shader shader;

    public RenderBatch(int maxBathSize) {
        shader = AssetPool.getShader(DEFAULT_SHADER_PATH);
        this.sprites = new SpriteRenderer[maxBathSize];
        this.maxBathSize = maxBathSize;

        // 4 vertices quads
        vertices = new float[maxBathSize * 4 * VERTEX_SIZE];

        this.numberOfSprites = 0;
        this.hasRoom = true;
        this.textureList = new ArrayList<>();
    }

    public void start(){
        // generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for the vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer spr){
        // Get the index and renderObject
        int index = this.numberOfSprites;
        this.sprites[index] = spr;
        this.numberOfSprites++;

        if (spr.getTexture() != null){
            if (!textureList.contains(spr.getTexture())){
                textureList.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numberOfSprites >= this.maxBathSize){
            this.hasRoom = false;
        }
    }

    public void render(){
        // For now we rebuffer every batch
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().camera().getViewMatrix());
        for (int i = 0; i < textureList.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numberOfSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (Texture texture : textureList) {
            texture.unBind();
        }
        shader.detach();
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find the offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] textureCoordinates = sprite.getTextureCoordinates();

        int textureID = 0;
        if (sprite.getTexture() != null){
            for (int i = 0; i < textureList.size(); i++) {
                if (textureList.get(i) == sprite.getTexture()){
                    textureID = i + 1;
                    break;
                }
            }
        }


        // Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1){
                yAdd = 0.0f;
            } else if (i == 2){
                xAdd = 0.0f;
            } else if (i == 3){
                yAdd = 1.0f;
            }

            // Add positions
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = textureCoordinates[i].x;
            vertices[offset + 7] = textureCoordinates[i].y;

            // Load texture ID
            vertices[offset + 8] = textureID;

            offset += VERTEX_SIZE;
        }

    }

    private int[] generateIndices(){
        // 6 indices per quad (3 per triangles)
        int[] elements = new int[6 * this.maxBathSize];
        for (int i = 0; i < maxBathSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1     ->      7, 6, 4, 4, 6, 5

        //Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        //Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;

    }

    public boolean hasRoom(){
        return this.hasRoom;
    }

}
