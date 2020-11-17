package driver;

import org.opencv.core.*;
import markerdetector.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import util.*;
import org.opencv.calib3d.Calib3d;

public class CompoundMarkerSimulation implements Simulation {
	private final Mat cameraMatrix;
	private final Mat distCoeffs;
	private final LinkedList<Integer> ids;
	private final MultiMarkerBody cMarker;

	public CompoundMarkerSimulation(Mat cameraMatrix, Mat distCoeffs, LinkedList<Integer> ids){
		if(cameraMatrix == null || distCoeffs == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		this.ids = ids;

		this.cMarker = new MultiMarkerBody(.2, new HashMap<Integer, MarkerOffset>() {{
        put(ids.get(0), new MarkerOffset(ids.get(0), 0, 0, 0, .85, -.85, 0));
        put(ids.get(1), new MarkerOffset(ids.get(1), 0, 0, 0, -.85, -.85, 0));
    	put(ids.get(2), new MarkerOffset(ids.get(2), 0, 0, 0, .85, .85, 0));
		put(ids.get(3), new MarkerOffset(ids.get(3), 0, 0, 0, -.85, .85, 0));}});
	}

	public Mat run(DetectorResults results){
		for(int i = 0; i < 4; i++){
			if(results.getMarkerInformation(this.ids.get(i)) == null){
				return results.baseImage();
			}
		}

		Pair<Mat, Mat> prediction = this.cMarker.predictCenter(results);

		Mat finalMatrix = results.baseImage();

		Calib3d.drawFrameAxes(finalMatrix, this.cameraMatrix, this.distCoeffs, prediction.first(), prediction.second(), 0.5F);
		return finalMatrix;
	}
}
