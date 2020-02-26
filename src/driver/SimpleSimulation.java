package driver;

import org.opencv.core.*;
import org.opencv.calib3d.Calib3d;
import detector.*;

public class SimpleSimulation implements Simulation {
	private Mat cameraMatrix;
	private Mat distCoeffs;

	public SimpleSimulation(Mat cameraMatrix, Mat distCoeffs){
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
	}

	public Mat run(DetectorResults results){
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		Mat rotationMatrix = results.rotationVectors();
		Mat translationMatrix = results.translationVectors();
		for(int i = 0; i < rotationMatrix.rows(); i++){
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, rotationMatrix.row(i), translationMatrix.row(i), 0.5F);
		}
		return finalMatrix;
	}
}