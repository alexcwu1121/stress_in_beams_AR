package driver;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import markerdetector.*;
import util.Pair;

/**Simulation which separates markers into two groups.
For the first group of markers, it draws a short set of axes. For the second group of markers, it draws a longer set of axes.
*/

public class DividedSimulation implements Simulation {
	private Mat cameraMatrix;
	private Mat distCoeffs;
	private Pair<Integer, Integer> firstGroup;
	private Pair<Integer, Integer> secondGroup;

	/**Constructs a divided simulation with the given information.<br>
	The first pair of ints represents the range of ids to put in the first grouop, and the second pair represents the second group (both inclusive).
	@param cameraMatrix The camera matrix to use for this simulation.
	@param distCoeefs The distortion coefficients to use for this simulation.
	@param firstRange Pair representing the first range of ids, inclusive.
	@param secondRange Pair representing the second range of ids, inclusive.
	*/
	public DividedSimulation(Mat cameraMatrix, Mat distCoeffs, Pair<Integer, Integer> firstRange, Pair<Integer, Integer> secondRange){
		if(cameraMatrix == null || distCoeffs == null || firstRange == null || secondRange == null){
			throw new NullPointerException();
		}
		if(firstRange.first() == null || firstRange.second() == null || secondRange.first() == null || secondRange.second() == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		this.firstGroup = firstRange;
		this.secondGroup = secondRange;
	}

	/**Runs the simulation.
	@param results The detector results object.
	@return The result mat.
	*/
	public Mat run(DetectorResults results){
		Mat finalMatrix = new Mat();
		results.baseImage().copyTo(finalMatrix);
		for(int i = firstGroup.first(); i <= firstGroup.second(); i++){
			MarkerInformation info = results.getMarkerInformation(i);
			if(info == null){
				continue;
			}
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, info.rotationVector(), info.translationVector(), 0.25F);
		}
		for(int i = secondGroup.first(); i <= secondGroup.second(); i++){
			MarkerInformation info = results.getMarkerInformation(i);
			if(info == null){
				continue;
			}
			Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, info.rotationVector(), info.translationVector(), 0.75F);
		}
		return finalMatrix;
	}
}