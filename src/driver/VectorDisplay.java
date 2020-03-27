package driver;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import util.*;
import java.util.Vector;
import markerdetector.*;

public class VectorDisplay implements Simulation {
	/*
	Draws a vector on a matrix given a normal plane and an intersection point
	*/
	private final boolean drawForce;
	private final boolean drawMoment;
	private final double scale;

	//Mat base, Dictionary dict, Mat ids, List<Mat> corners, List<Mat> rejected, Mat rotationVectors, Mat translationVectors

	public VectorDisplay(boolean _drawForce, boolean _drawMoment, double _scale){
		drawForce = _drawForce;
		drawMoment = _drawMoment;
		scale = _scale;
	}

	public Mat run(DetectorResults results){
		//for each set of rvecs and tvecs (defining a normal plane), calculate the magnitude of the bend and draw a 3D vector through a point using getMarkerInformation
		//transform all 3d vectors into 2d vectors with the camera matrix and change the source Mat
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		MarkerInformation reference = results.getMarkerInformation(0);
		MarkerInformation dynamic = results.getMarkerInformation(1);

		if(dynamic == null || reference == null){
			return finalMatrix;
		}

		Point3D origin = new Point3D(dynamic);
		Vector<Point3D> endpoints = new Vector<Point3D>();
		endpoints.add(origin);

		if(drawForce){
			// normal force applied parallel to the y axis of the beam
			for(int i = 0; i < 2; i++){
				Double magnitude = scale * (reference.rotationVector().get(0,0)[i] - dynamic.rotationVector().get(0,0)[i]);
				Point3D endpoint = new Point3D(magnitude * Math.cos(reference.rotationVector().get(0, 0)[0]) + origin.x(),
											magnitude * Math.cos(reference.rotationVector().get(0, 0)[1] + origin.y()),
											magnitude * Math.cos(reference.rotationVector().get(0, 0)[2]) + origin.z());
				endpoints.add(endpoint);
			}
		}

		//iterate through endpoints and perform change of basis on all vectors

		Point pt1 = new Point(100, 200);
		Point pt2 = new Point(300, 400);
		Imgproc.line(finalMatrix, pt1, pt2, new Scalar(0,255,0), 3);

		return finalMatrix;
	}
}