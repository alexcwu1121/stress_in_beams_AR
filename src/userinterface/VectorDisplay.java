package userinterface;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import org.opencv.imgcodecs.*;
import java.io.*;
import java.util.Scanner;
import util.*;
import java.util.Vector;
import markerdetector.*;

public class VectorDisplay{
	/*
	Draws a vector on a matrix given a normal plane and an intersection point
	*/
	private final boolean drawForce;
	private final boolean drawMoment;

	//Mat base, Dictionary dict, Mat ids, List<Mat> corners, List<Mat> rejected, Mat rotationVectors, Mat translationVectors

	public VectorDisplay(boolean _drawForce, boolean _drawMoment){
		drawForce = _drawForce;
		drawMoment = _drawMoment;
	}

	public Mat run(DetectorResults results){
		//for each set of rvecs and tvecs (defining a normal plane), calculate the magnitude of the bend and draw a vector through a point using getMarkerInformation
		MarkerInformation reference = getMarkerInformation(0);
		MarkerInformation dynamic = getMarkerInformation(1);

		if drawForce(){

		}

		if drawMoment(){

		}
	}
}