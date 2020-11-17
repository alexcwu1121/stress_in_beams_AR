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

import util.*;
import markerdetector.*;
import userinterface.*;

public class Driver{
	private static VideoCap webcam = new VideoCap(0);

	public static void main(String[] args) throws IOException {
        //Might want some more preprocessing on this one, like using a CLI library or ensuring that the file exists.
        String detectorParameters = args[0];
        String cameraParameters = args[1];
        MarkerDetector detector = new MarkerDetector(detectorParameters, cameraParameters);
        CalibrationInformation cameraInfo = detector.getCameraInformation();
        StrengthsGUI gui = new StrengthsGUI(cameraInfo);
        while(true){
            Mat m = webcam.getOneFrame();
            DetectorResults results = detector.detectMarkers(m, 4);
            gui.updateSimulations(results);
        }
	}
}