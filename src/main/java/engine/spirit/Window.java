package engine.spirit;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final int with;
    private final int height;
    private final String title;

    private static Window window = null;
    private long glfwWindow;

    private static Scene currentScene;

    public float r, g, b, a;

    private Window() {
        this.with = 1920;
        this.height = 1080;
        this.title = "Mario";

        this.r = 0f;
        this.g = 0f;
        this.b = 0f;
        this.a = 0f;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false:"Unknown scene '" + newScene + "";
        }
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        this.init();
        this.loop();

        /* Free the memory */
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        /* Terminate GLFW and free the error callback */
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        /* Setup error callback */
        GLFWErrorCallback.createPrint(System.err).set();

        /* Initialize GLFW */
        if (!glfwInit()){
            throw new IllegalStateException("Could not initialize GLFW");
        }

        /* Configure GLFW */
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        /* Create the window */
        glfwWindow = glfwCreateWindow(this.with, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL){
            throw new IllegalStateException("Failed to create GLFW window");
        }

        /* Event listeners */
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        /* Make the OpenGL context current */
        glfwMakeContextCurrent(glfwWindow);

        /* Enable v-sync */
        glfwSwapInterval(1);

        /* Make window visible */
        glfwShowWindow(glfwWindow);

        /*
        This line is critical for LWJGL's interoperation with GLFW's
        OpenGL context, or any context that is managed externally.
        LWJGL detects the context that is current in the current thread,
        creates the GLCapabilities instance and makes the OpenGL
        bindings available for use.
        */
        GL.createCapabilities();

        Window.changeScene(0);

    }

    public void loop(){
        float beginTime = (float) glfwGetTime();
        float endTime;

        float deltaTime = -1f;

        while (!glfwWindowShouldClose(glfwWindow)){
            /* Poll events */
            glfwPollEvents();

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                System.out.println("EXITING..");
                glfwSetWindowShouldClose(glfwWindow, true);
            }

            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0)
                currentScene.update(deltaTime);

             glClearColor(r, g, b, a);

            glfwSwapBuffers(glfwWindow);

            endTime = (float) glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
