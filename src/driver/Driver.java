package driver;

import concurrency.*;
import imageprocessing.*;
import configs.*;
import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;
import java.util.*;

public class Driver{
	private static VideoCap webcam = new VideoCap();
    private static JFrame jframe;
	private static JPanel contentPane;
    private static Mat matrix;

	public static void main(String[] args) throws InterruptedException {
        jframe = new JFrame(){
            @Override
            public void paint(Graphics g){
                if(matrix != null){
                    g.drawImage(getImage(matrix), 0, 0, this);
                }
            }
        };
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setBounds(0, 0, 1280, 720);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        jframe.setContentPane(contentPane);
        contentPane.setLayout(null);
        jframe.setVisible(true);
        //jframe.setTitle(Integer.toString(id));

        //Might want some more preprocessing on this one, like using a CLI library or ensuring that the file exists.
        String arucoConfigFilePath = args[0];
        Detector detector = new Detector(arucoConfigFilePath);
        Simulation s = NullSimulation.get();
        SimulationFrame frame = new SimulationFrame(s);

        while(true){
            Mat m = webcam.getOneFrame();
            Pair<Mat, Mat> matrices = detector.detectMarkers(m);
            frame.simulate(m, matrices.first(), matrices.second());
            frame.repaint();
        }
	}

    public static void setMatrix(Mat mat) {
        matrix = mat;
    }

	public void paint(Graphics g){
        g = contentPane.getGraphics();
        g.drawImage(getImage(matrix), 0, 0, jframe);
    }

	/**
    Not sure what this does to be honest.
    */
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = 0;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int w = mat.cols();
        int h = mat.rows();
        BufferedImage img;
        img = new BufferedImage(w, h, type);
        return img;
    }

    /**
    Returns a BufferedImage representation of a mat.
    @param lined The mat to convert.
    @return A BufferedImage represonting the mat.
    */
    private static BufferedImage getImage(Mat lined){
        // Initialize global img to buffered image
        BufferedImage img = matToBufferedImage(lined);
        WritableRaster raster = img.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        lined.get(0, 0, data);
        return img;
    } 
}