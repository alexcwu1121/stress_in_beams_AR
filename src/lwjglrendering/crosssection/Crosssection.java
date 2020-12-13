package lwjglrendering.crosssection;


import lwjglrendering.Context;
import lwjglrendering.util.ShaderProgram;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Crosssection extends Context
{
    private ShaderProgram shaderProgram;

    private int vaoID;
    private int vboVertID;
    private int vboColID;
    private int eboID;
    private float[] colors;


    public void init()
    {
        glfwSetWindowTitle(Context.getWindowID(), "Element Buffer Objects");

        shaderProgram = new ShaderProgram();
        shaderProgram.attachVertexShader("lwjglrendering/crosssection/Crosssection.vs");
        shaderProgram.attachFragmentShader("lwjglrendering/crosssection/Crosssection.fs");
        shaderProgram.link();

        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        double vert1x = -1.0; //bottom left
        double vert1y = -1.0;
        double vert2x = -1; //bottom right
        double vert2y = 1;
        double vert3x = 1; //top left
        double vert3y = -1;
        double vert4x = 1.0; //top right
        double vert4y = 1.0;

        float[] vertices = new float[30];

        for (int i = 0; i < 15; i++){
            double x1 = 0;
            double x2 = 0;
            double y1 = 0;
            double y2 = 0;
            if (Math.floor(i/5) == 0){
                x1 = vert1x;
                y1 = vert1y;
                x2 = vert2x;
                y2 = vert2y;
            } else if (Math.floor(i/5) == 1){
                x1 = (vert1x + vert3x) / 2.0;
                x2 = (vert2x + vert4x) / 2.0;
                y1 = (vert1y + vert3y) / 2.0;
                y2 = (vert2y + vert4y) / 2.0;
            } else {
                x1 = vert3x;
                x2 = vert4x;
                y1 = vert3y;
                y2 = vert4y;
            }

            // calculate distance between the two points
            double DT = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
            double D = i % 5/4.0 * DT;
           
            double x;
            double y;
            double T = D / DT;
           
            // finding point C coordinate
            x = (1 - T) * x1 + T * x2;
            y = (1 - T) * y1 + T * y2;
            vertices[i*2] = (float)x;
            vertices[i*2 + 1] = (float)y;
        }

        // The colors of the vertices
        colors = new float[]
        {
            1, 0, 0, 1, //vertex 0
            1, 0, 0, 1, //vertex 1...
            1, 0, 0, 1, //2
            1, 0, 0, 1, //3
            1, 0, 0, 1, //4
            1, 1, 1, 1, //5
            1, 1, 1, 1, //6
            1, 1, 1, 1, //7
            1, 1, 1, 1, //8
            1, 1, 1, 1, //9
            0, 0, 1, 1, //10
            0, 0, 1, 1, //11
            0, 0, 1, 1, //12
            0, 0, 1, 1, //13
            0, 0, 1, 1, //14
        };

        //random colors
        // colors = new float[]
        // {
        //     (int)(Math.random() * 2), 0, 0, 1, //vertex 0
        //     (int)(Math.random() * 2), 0, 0, 1, //vertex 1...
        //     (int)(Math.random() * 2), 0, 0, 1, //2
        //     (int)(Math.random() * 2), 0, 0, 1, //3
        //     (int)(Math.random() * 2), 0, 0, 1, //4
        //     (int)(Math.random() * 2), 1, 1, 1, //5
        //     (int)(Math.random() * 2), 1, 1, 1, //6
        //     (int)(Math.random() * 2), 1, 1, 1, //7
        //     (int)(Math.random() * 2), 1, 1, 1, //8
        //     (int)(Math.random() * 2), 1, 1, 1, //9
        //     (int)(Math.random() * 2), 0, 1, 1, //10
        //     (int)(Math.random() * 2), 0, 1, 1, //11
        //     (int)(Math.random() * 2), 0, 1, 1, //12
        //     (int)(Math.random() * 2), 0, 1, 1, //13
        //     (int)(Math.random() * 2), 0, 1, 1, //14
        // };


        // The vertex order for the triangle strip
        short[] indices = new short[]
        {
            0, 5, 1, 
            6, 2, 7, 
            3, 8, 4, 
            9, 9, 5, 5, 
            10, 6, 11, 
            7, 12, 8, 
            13, 9, 14
        };

        // Create a FloatBuffer of vertices
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        // Create a Buffer Object and upload the vertices buffer
        vboVertID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // Point the buffer at location 0, the location we set
        // inside the vertex shader. You can use any location
        // but the locations should match
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        // Create a FloatBuffer of colors
        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        // Create a Buffer Object and upload the colors buffer
        vboColID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColID);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);

        // Point the buffer at location 1, the location we set
        // inside the vertex shader. You can use any location
        // but the locations should match
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        // Create a ShortBuffer of indices
        ShortBuffer indicesBuffer = BufferUtils.createShortBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        // Create the Element Buffer object
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Enable the vertex attribute locations
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    //render a frame
    public void render(float delta)
    {
        glClear(GL_COLOR_BUFFER_BIT);
        shaderProgram.bind();
        glBindVertexArray(vaoID);

        // Bind the colors
        glBindBuffer(GL_ARRAY_BUFFER, vboColID);

        glDrawElements(GL_TRIANGLE_STRIP, 22, GL_UNSIGNED_SHORT, 0);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        ShaderProgram.unbind();
    }

    public void updateColors(){
        // The colors of the vertices
        colors = new float[]
        {
            (int)(Math.random() * 2), 0, 0, 1, //vertex 0
            (int)(Math.random() * 2), 0, 0, 1, //vertex 1...
            (int)(Math.random() * 2), 0, 0, 1, //2
            (int)(Math.random() * 2), 0, 0, 1, //3
            (int)(Math.random() * 2), 0, 0, 1, //4
            (int)(Math.random() * 2), 1, 1, 1, //5
            (int)(Math.random() * 2), 1, 1, 1, //6
            (int)(Math.random() * 2), 1, 1, 1, //7
            (int)(Math.random() * 2), 1, 1, 1, //8
            (int)(Math.random() * 2), 1, 1, 1, //9
            (int)(Math.random() * 2), 0, 1, 1, //10
            (int)(Math.random() * 2), 0, 1, 1, //11
            (int)(Math.random() * 2), 0, 1, 1, //12
            (int)(Math.random() * 2), 0, 1, 1, //13
            (int)(Math.random() * 2), 0, 1, 1, //14
        };
        // Create a FloatBuffer of colors
        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        // Create a Buffer Object and upload the colors buffer
        vboColID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColID);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
    }

    //destroy the frame
    public void dispose()
    {
        shaderProgram.dispose();
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboVertID);
        glDeleteBuffers(vboColID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteBuffers(eboID);
    }
}