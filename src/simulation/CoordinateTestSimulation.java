package simulation;

import org.opencv.core.*;
import markerdetector.*;
import org.opencv.imgproc.Imgproc;
import util.*;
import org.opencv.calib3d.Calib3d;

@HumanReadableName("Multimarker Body Test Simulation")
public class CoordinateTestSimulation implements Simulation {
	private final int secondid;
	private final MultiMarkerBody body;

	@Internal
	public CoordinateTestSimulation(int secondid){
		this.secondid = secondid;
		//body = new MultiMarkerBody(1, new MarkerOffset(secondid, 0, 0, 0, .85, -.85, 0));
		//body = new MultiMarkerBody(1, new MarkerOffset(this.secondid+1, 0, 0, 0, -.85, -.85, 0));
		//body = new MultiMarkerBody(1, new MarkerOffset(this.secondid+2, 0, 0, 0, .85, .85, 0));
		body = new MultiMarkerBody(1, new MarkerOffset(this.secondid+3, 0, 0, 0, -.85, .85, 0));
	}

	public CoordinateTestSimulation(int drawingID, double xRotation, double yRotation, double zRotation, double xTranslation, double yTranslation, double zTranslation){
		this.secondid = drawingID;
		body = new MultiMarkerBody(1, new MarkerOffset(secondid, xRotation, yRotation, zRotation, xTranslation, yTranslation, zTranslation));
	}

	public Mat run(DetectorResults results){
		MarkerInformation second = results.getMarkerInformation(this.secondid);
		if(second == null){
			return results.baseImage();
		}
		Pair<Mat, Mat> prediction = this.body.predictCenter(results);

		Mat finalMatrix = results.baseImage();
		//MatMathUtils.printmat(prediction.first());
		//MatMathUtils.printmat(prediction.second());
		//System.out.println();
		//Pair<Mat, Mat> back = MatMathUtils.get3DCoords(prediction.first(), prediction.second());
		CalibrationInformation ci = results.calibrationInformation();
		Calib3d.drawFrameAxes(finalMatrix, ci.cameraMatrix(), ci.distCoeffs(), prediction.first(), prediction.second(), 0.5F);
		return finalMatrix;
	}
}
