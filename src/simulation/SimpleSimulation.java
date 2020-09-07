package simulation;

import org.opencv.core.*;
import org.opencv.calib3d.Calib3d;
import markerdetector.*;
import util.HumanReadableName;

/**Simulation which draws axes on all detected markers.
*/

@HumanReadableName("Simple Simulation")
public class SimpleSimulation implements Simulation {
	public SimpleSimulation(){}

	public Mat run(DetectorResults results){
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		Mat rotationMatrix = results.rotationVectors();
		Mat translationMatrix = results.translationVectors();
		CalibrationInformation ci = results.calibrationInformation();
		for(int i = 0; i < rotationMatrix.rows(); i++){
			Calib3d.drawFrameAxes(finalMatrix, ci.cameraMatrix(), ci.distCoeffs(), rotationMatrix.row(i), translationMatrix.row(i), 0.5F);
		}
		return finalMatrix;
	}
}