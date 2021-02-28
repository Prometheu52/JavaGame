package engine.spirit;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double posX, posY, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[7];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0;
        this.scrollY = 0;

        this.posX = 0;
        this.posY = 0;

        this.lastX = 0;
        this.lastY = 0;
    }

    public static MouseListener get(){
        if (MouseListener.instance == null){
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double posX, double posY){
        get().lastX = get().posX;
        get().lastY = get().posY;

        get().posX = posX;
        get().posY = posY;

        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2] ||
                get().mouseButtonPressed[3] || get().mouseButtonPressed[4];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS && button < get().mouseButtonPressed.length){
            get().mouseButtonPressed[button] = true;
        }else if (action == GLFW_RELEASE && button < get().mouseButtonPressed.length){
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;

        get().lastX = get().posX;
        get().lastY = get().posY;
    }

    public static float getPosX(){
        return (float) get().posX;
    }

    public static float getPosY(){
        return (float) get().posY;
    }

    public static float getDeltaX(){
        return (float) (get().lastX - get().posX);
    }

    public static float getDeltaY(){
        return (float) (get().lastY - get().posX);
    }

    public static float getScrollX(){
        return (float) get().scrollX;
    }

    public static float getScrollY(){
        return (float) get().scrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length)
            return get().mouseButtonPressed[button];
        return false;
    }
}
