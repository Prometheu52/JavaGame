package engine.spirit;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{

    private String vertexShaderScr = "#version 460 core\n" +
            "\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "    fColor = aColor;\n" +
            "\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderScr = "#version 460 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main(){\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //POSITION                //COLOR
             0.5f,  -0.5f,  0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
            -0.5f,   0.5f,  0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
             0.5f,   0.5f,  0.0f,       0.0f, 0.0f, 1.0f, 1.0f, // Top right    2
            -0.5f,  -0.5f,  0.0f,       1.0f, 1.0f, 0.0f, 1.0f // Bottom left   3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // Bottom left triangle
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("LEVEL EDITOR SCENE");
    }

    @Override
    public void init(){
        /* Compile and Link shaders */

        // Load and compile the vertex shader
        this.vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(vertexID, vertexShaderScr);
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));

            assert false:"";
        }

/*--------------------------------------------------------------------------------------------------------------------*/

        // Load and compile the fragment shader
        this.fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(fragmentID, fragmentShaderScr);
        glCompileShader(fragmentID);

        // Check for compilation errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));

            assert false:"";
        }

/*--------------------------------------------------------------------------------------------------------------------*/

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // Check for linking errors
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking of shaders failed");
            System.out.println(glGetShaderInfoLog(shaderProgram, len));

            assert false:"";
        }

/*--------------------------------------------------------------------------------------------------------------------*/

        /* Generate VAO, VBO, and EBO objects, and sent it to the GPU */

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = Float.BYTES;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float deltaTime) {
        //System.out.println("" + (1.0f / deltaTime) + "FPS");

        // Bind Shader Program
        glUseProgram(shaderProgram);
        // Bind the VAO
        glBindVertexArray(vaoID);

        // Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);


        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);

    }

}
