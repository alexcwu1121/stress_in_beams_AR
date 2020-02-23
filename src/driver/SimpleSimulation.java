package driver;

import org.opencv.core.*;
import org.opencv.calib3d.Calib3d;

public class SimpleSimulation implements Simulation {
	private Mat cameraMatrix = new Mat();
	private Mat distCoeffs = new Mat();

	public SimpleSimulation(Mat cameraMatrix, Mat distCoeffs){
		//cameraMatrix.copyTo(this.cameraMatrix);
		//distCoeffs.copyTo(this.distCoeffs);
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
	}

	public Mat run(Mat baseMatrix, Mat rotationMatrix, Mat translationMatrix){
		Mat finalMatrix = new Mat();
		baseMatrix.copyTo(finalMatrix);
		for(int i = 0; i < rotationMatrix.rows(); i++){
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, rotationMatrix.row(i), translationMatrix.row(i), 0.5F);
		}
		return finalMatrix;
	}
}