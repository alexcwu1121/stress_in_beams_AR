package lwjglrendering;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.awt.*;
import java.awt.event.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.color.ColorSpace;
import java.awt.image.DataBufferByte;
import java.awt.image.ComponentColorModel;
import java.awt.image.ColorModel;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Context
{
    private static long windowID;
    float now, delta, last = 0;

    public Context()
    {        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
        {
            System.err.println("Error initializing GLFW");
            System.exit(1);
        }
        
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        //glfwWindowHint(GLFW_VISIBLE, 0 ); // the window will stay hidden after creation
        windowID = glfwCreateWindow(640, 480, "My GLFW Window", NULL, NULL);

        if (windowID == NULL)
        {
            System.err.println("Error creating a window");
            System.exit(1);
        }

        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        glfwSwapInterval(1);
    }

    public static void main(String[] args)
    {
        new Context().start();
    }

    public void end()
    {
        glfwSetWindowShouldClose(windowID, true);

        // Dispose the simulation
        dispose();

        // Destroy the window
        glfwDestroyWindow(windowID);
        glfwTerminate();
    }

    public void init()
    {
    }

    public void update(float delta)
    {
    }

    public void render(float delta)
    {
    }

    public static long getWindowID()
    {
        return windowID;
    }

    public void dispose()
    {
    }

    public void start()
    {
        // Initialize the context
        init();
    }

    public BufferedImage newframe(){
        now = (float) glfwGetTime();
        delta = now - last;
        last = now;

        // Update and Render
        update(delta);
        render(delta);

        // Poll the events and swap the buffers
        glfwPollEvents();
        glfwSwapBuffers(windowID);

        glFlush();
        return toImage(grabScreen(), 640, 480);
    }

    private static synchronized byte[] grabScreen() {
        int w = 640;
        int h = 480;
        ByteBuffer bufor = BufferUtils.createByteBuffer(w * h * 3);

        GL11.glReadPixels(0, 0, w, h, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, bufor); //Copy the image to the array imageData

        byte[] byteimg = new byte[w * h * 3];
        bufor.get(byteimg, 0, byteimg.length);
        return byteimg;
    }

    BufferedImage toImage(byte[] data, int w, int h){
        if (data.length == 0)
            return null;

        DataBuffer buffer = new DataBufferByte(data, w * h);

        int pixelStride = 3; //assuming r, g, b, skip, r, g, b, skip...
        int scanlineStride = 3 * w; //no extra padding   
        int[] bandOffsets = { 0, 1, 2 }; //r, g, b
        WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h, scanlineStride, pixelStride, bandOffsets,
                null);

        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        boolean hasAlpha = false;
        boolean isAlphaPremultiplied = true;
        int transparency = Transparency.TRANSLUCENT;
        int transferType = DataBuffer.TYPE_BYTE;
        ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency,
                transferType);

        BufferedImage image = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);

        AffineTransform flip;
        AffineTransformOp op;
        flip = AffineTransform.getScaleInstance(1, -1);
        flip.translate(0, -image.getHeight());
        op = new AffineTransformOp(flip, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);

        return image;
    }


}
