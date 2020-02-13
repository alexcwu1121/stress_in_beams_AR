package driver;

//import concurrency.*;
//import imageprocessing.*;
//import configs.*;
import java.lang.Math;
import org.opencv.core.*;
import org.opencv.aruco.*;
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

	public static void main(String[] args) {
        //Might want some more preprocessing on this one, like using a CLI library or ensuring that the file exists.
        String arucoConfigFilePath = args[0];
        Detector detector = new Detector(arucoConfigFilePath);
        Simulation s = NullSimulation.get();
        SimulationFrame frame = new SimulationFrame(s);

        while(true){
            Mat m = webcam.getOneFrame();
            Pair<Mat, Mat> matrices = detector.detectMarkers(m);
            Pair<Mat, Mat> cameraInfo = detector.getCameraInfo(m);
            frame.simulate(m, matrices.first(), matrices.second(), cameraInfo.first(), cameraInfo.second());
            //frame.simulate(m, new Mat(), new Mat(), new Mat(), new Mat());
            frame.repaint();
        }
	}
}