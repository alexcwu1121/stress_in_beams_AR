package driver;

import org.opencv.core.*;
import markerdetector.*;
import org.opencv.imgproc.Imgproc;
import util.*;
import org.opencv.calib3d.Calib3d;

public class CoordinateTestSimulation implements Simulation {
	private final Mat cameraMatrix;
	private final Mat distCoeffs;
	private final int secondid;
	private final MultiMarkerBody body;

	public CoordinateTestSimulation(Mat cameraMatrix, Mat distCoeffs, int secondid){
		if(cameraMatrix == null || distCoeffs == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		this.secondid = secondid;
		body = new MultiMarkerBody(new MarkerOffset(secondid, 0, 0, 0, 0, 0, 0));
	}

	public Mat run(DetectorResults results){
		MarkerInformation second = results.getMarkerInformation(this.secondid);
		if(second == null){
			return results.baseImage();
		}
		Pair<Mat, Mat> prediction = this.body.predictCenter(results);
		/*MarkerUtils.printmat(prediction.first());
		MarkerUtils.printmat(prediction.second());
		System.out.println();
		MarkerUtils.printmat(second.rotationVector3D());
		MarkerUtils.printmat(second.translationVector3D());
		System.out.println();
		System.out.println();
		System.out.println();*/
		Mat finalMatrix = results.baseImage();
		//MarkerUtils.printmat(prediction.first());
		//MarkerUtils.printmat(prediction.second());
		//System.out.println();
		//Pair<Mat, Mat> back = MarkerUtils.get3DCoords(prediction.first(), prediction.second());
		Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, prediction.first(), prediction.second(), 0.5F);
		return finalMatrix;
	}
}
