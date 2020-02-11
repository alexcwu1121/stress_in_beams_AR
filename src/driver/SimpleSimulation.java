package driver;

import org.opencv.core.*;
import org.opencv.calib3d.Calib3d;

public class SimpleSimulation implements Simulation {
	private static SimpleSimulation singleton;

	static {
		singleton = new SimpleSimulation();
	}

	private SimpleSimulation(){}

	public SimpleSimulation get(){
		return singleton;
	}

	public Mat run(Mat baseMatrix, Mat rotationMatrix, Mat translationMatrix, Mat cameraMatrix, Mat distCoeffs){
		Mat finalMatrix = new Mat();
		baseMatrix.copyTo(finalMatrix);
		Calib3d.drawFrameAxes(finalMatrix, cameraMatrix, distCoeffs, rotationMatrix, translationMatrix, 0.1F);
		return finalMatrix;
	}
}