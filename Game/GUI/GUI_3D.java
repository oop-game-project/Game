package Game.GUI;


import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GUI_3D {

    // The window handle
    private long window;
    private double lastLoopTime;
    private String vertexSource =
            "#version 150 core\n" +
            "\n" +
            "in vec3 position;\n" +
            "in vec3 color;\n" +
            "\n" +
            "out vec3 vertexColor;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "void main() {\n" +
            "    vertexColor = color;\n" +
            "    mat4 mvp = projection * view * model;\n" +
            "    gl_Position = mvp * vec4(position, 1.0);\n" +
            "}";

    private String fragmentSource =
            "#version 150 core\n" +
            "\n" +
            "in vec3 vertexColor;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fragColor = vec4(vertexColor, 1.0);\n" +
            "}";

    private static String getLogInfo(int obj)
    {
        return ARBShaderObjects.glGetInfoLogARB(
                obj,
                ARBShaderObjects.glGetObjectParameteriARB(
                        obj,
                        ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    private int createShader(String shaderCode, int shaderType) throws Exception
    {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if(shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, shaderCode);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(
                    shader,
                    ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) ==
                GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

            return shader;
        }
        catch(Exception exc) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw exc;
        }
    }

    public void init()
    {
        // TODO : glfwSetErrorCallback(errorCallback); (?)
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        this.lastLoopTime = glfwGetTime();
        this.window = glfwCreateWindow(700, 700, "Game", NULL, NULL);
        if ( this.window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
        {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_PRESS )
                glfwSetWindowShouldClose(window, true);
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public float getDelta() {
        double time = glfwGetTime();
        float delta = (float) (time - this.lastLoopTime);
        this.lastLoopTime = time;
        return delta;
    }

    public void render()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//       // Get the thread stack and push a new frame
//       try ( MemoryStack stack = stackPush() ) {
//           IntBuffer pWidth = stack.mallocInt(1); // int*
//           IntBuffer pHeight = stack.mallocInt(1); // int*
//
//           // Get the window size passed to glfwCreateWindow
//           glfwGetWindowSize(window, pWidth, pHeight);
//
//           // Get the resolution of the primary monitor
//           GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//
//           // Center the window
//           glfwSetWindowPos(
//                   window,
//                   (vidmode.width() - pWidth.get(0)) / 2,
//                   (vidmode.height() - pHeight.get(0)) / 2
//           );
//       } // the stack frame is popped automatically

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // Dynamic draw?
        }

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSource);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSource);
        glCompileShader(fragmentShader);
    }

    public void loop()
    {
        while ( !glfwWindowShouldClose(window) )
        {
            float delta = this.getDelta();

            //input();
            //update(delta);

            this.render();

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }
    }

    public void dispose()
    {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}

// TODO : coordinates of GameField. Only positive? If y