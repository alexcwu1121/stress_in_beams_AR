package driver;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import util.*;
import org.opencv.calib3d.Calib3d;
import java.util.Vector;
import markerdetector.*;

public class VectorDisplay implements Simulation {
	/*
	Draws a vector on a matrix given a normal plane and an intersection point
	*/
	private final boolean drawForce;
	private final boolean drawMoment;
	private final double scale;
	public final Mat cameraMatrix;
	public final Mat distCoeffs;

	//Mat base, Dictionary dict, Mat ids, List<Mat> corners, List<Mat> rejected, Mat rotationVectors, Mat translationVectors

	public VectorDisplay(Mat _cameraMatrix, Mat _distCoeffs, boolean _drawForce, boolean _drawMoment, double _scale){
		this.drawForce = _drawForce;
		this.drawMoment = _drawMoment;
		this.scale = _scale;
		this.cameraMatrix = _cameraMatrix;
		this.distCoeffs = _distCoeffs;
	}

	public Mat run(DetectorResults results){
		//for each set of rvecs and tvecs (defining a normal plane), calculate the magnitude of the bend and draw a 3D vector through a point using getMarkerInformation
		//transform all 3d vectors into 2d vectors with the camera matrix and change the source Mat
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		MarkerInformation reference = results.getMarkerInformation(6);
		MarkerInformation dynamic = results.getMarkerInformation(7);

		if(dynamic == null || reference == null){
			return finalMatrix;
		}

		Vector<Float> magnitudes = new Vector<Float>();

		if(drawForce){
			// normal force applied parallel to the y axis of the beam
			for(int i = 0; i < 2; i++){
				Float magnitude = (float)scale * (float)(reference.rotationVector3D().get(i,0)[0] - dynamic.rotationVector3D().get(i,0)[0]);
				System.out.println(magnitude);
				magnitudes.add(magnitude);
			}
		}

		Mat axesPoints = new Mat(3, 3, CvType.CV_32FC3);
	    axesPoints.put(0, 0, new float[]{0, 0, 0});
	    axesPoints.put(1, 0, new float[]{(float)magnitudes.get(0), 0, 0});
	    axesPoints.put(2, 0, new float[]{0, (float)magnitudes.get(1), 0});
	    axesPoints.put(3, 0, new float[]{0, 0, (float)magnitudes.get(2)});
	    Mat imagePoints = new Mat();
	    Calib3d.fisheye_projectPoints(axesPoints, imagePoints, dynamic.rotationVector(), dynamic.translationVector(), cameraMatrix, distCoeffs);

	    //MarkerUtils.printmat(imagePoints);

	    // draw axes lines
	    //Imgproc.line(finalMatrix, new Point(imagePoints.get(0, 0)[0], imagePoints.get(0, 0)[1]), new Point(imagePoints.get(1, 0)[0], imagePoints.get(1, 0)[1]), new Scalar(0, 0, 255), 5);
	    //Imgproc.line(finalMatrix, new Point(imagePoints.get(0, 0)[0], imagePoints.get(0, 0)[1]), new Point(imagePoints.get(2, 0)[0], imagePoints.get(2, 0)[1]), new Scalar(0, 255, 0), 5);
	    //Imgproc.line(finalMatrix, new Point(imagePoints.get(0, 0)[0], imagePoints.get(0, 0)[1]), new Point(imagePoints.get(3, 0)[0], imagePoints.get(3, 0)[1]), new Scalar(255, 0, 0), 5);

		return finalMatrix;
	}
}
