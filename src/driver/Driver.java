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
import java.io.*;

import util.Pair;
import detector.*;

public class Driver{
	private static VideoCap webcam = new VideoCap();

	public static void main(String[] args) throws IOException {
        //Might want some more preprocessing on this one, like using a CLI library or ensuring that the file exists.
        String arucoConfigFilePath = args[0];
        Detector detector = new Detector(arucoConfigFilePath, arucoConfigFilePath);
        Pair<Mat, Mat> cameraInfo = detector.getCameraInformation();
        Simulation s = new DividedSimulation(cameraInfo.first(), cameraInfo.second(), new Pair<Integer, Integer>(0, 9), new Pair<Integer, Integer>(10, 19));
        SimulationFrame frame = new SimulationFrame(s);
        while(true){
            Mat m = webcam.getOneFrame();
            DetectorResults results = detector.detectMarkers(m, 4);
            frame.simulate(results);
            //frame.simulate(m, new Mat(), new Mat());
            frame.repaint();
        }
	}
}