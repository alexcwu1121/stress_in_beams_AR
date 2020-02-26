package driver;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import detector.*;
import util.Pair;

public class DividedSimulation implements Simulation {
	private Mat cameraMatrix;
	private Mat distCoeffs;
	private Pair<Integer, Integer> firstGroup;
	private Pair<Integer, Integer> secondGroup;

	public DividedSimulation(Mat cameraMatrix, Mat distCoeffs, Pair<Integer, Integer> firstRange, Pair<Integer, Integer> secondRange){
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		this.firstGroup = firstRange;
		this.secondGroup = secondRange;
	}

	public Mat run(DetectorResults results){
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		for(int i = firstGroup.first(); i < firstGroup.second(); i++){
			MarkerInformation info = results.getMarkerInformation(i);
			if(info == null){
				continue;
			}
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, info.rotationVector(), info.translationVector(), 0.25F);
		}
		for(int i = secondGroup.first(); i < secondGroup.second(); i++){
			MarkerInformation info = results.getMarkerInformation(i);
			if(info == null){
				continue;
			}
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, info.rotationVector(), info.translationVector(), 0.75F);
		}
		return finalMatrix;
	}
}