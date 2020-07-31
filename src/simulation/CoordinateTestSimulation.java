package simulation;

import org.opencv.core.*;
import markerdetector.*;
import org.opencv.imgproc.Imgproc;
import util.*;
import org.opencv.calib3d.Calib3d;

@HumanReadableName("Test Simulation")
public class CoordinateTestSimulation implements Simulation {
	private final int secondid;
	private final MultiMarkerBody body;

	public CoordinateTestSimulation(){
		this(7);
	}

	public CoordinateTestSimulation(int secondid){
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
		CalibrationInformation ci = results.calibrationInformation();
		Calib3d.drawFrameAxes(finalMatrix, ci.cameraMatrix(), ci.distCoeffs(), prediction.first(), prediction.second(), 0.5F);
		return finalMatrix;
	}
}
