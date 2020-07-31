package markerdetector;

import org.opencv.core.*;
import org.opencv.aruco.*;
import java.util.*;

public class ArucoCalibrator implements Calibrator{
	private final int minFrames;

	private static final int DEFAULT_VALUE = 10;
	//Hardcoded values, could be changed to config file.
	private static final int DICT_ID = 4;
	private static final org.opencv.aruco.Dictionary DICTIONARY = Aruco.getPredefinedDictionary(DICT_ID);
	private static final int MIN_FRAMES = 5;
	private static final int MARKERS_X = 5;
    private static final int MARKERS_Y = 4;
    private static final float MARKER_LENGTH = .043f;
    private static final float MARKER_SEPARATION = .016f;

	public ArucoCalibrator(){
		this(DEFAULT_VALUE);
	}

	public ArucoCalibrator(int minFrames){
		this.minFrames = minFrames;
	}

	public CalibrationInformation calibrate(Collection<DetectorResults> frames) throws NotEnoughFramesException {
		List<List<Mat>> allCorners = new Vector<List<Mat>>();
    	List<Mat> allIds = new Vector<Mat>();
    	Size imgSize = new Size();
    	for(DetectorResults results : frames){
    		if(!results.corners().isEmpty()){
				allCorners.add(results.corners());
            	allIds.add(results.getIds());
            	imgSize = results.baseImage().size();
			}
    	}

    	if(allIds.size() < this.minFrames){
    		throw new NotEnoughFramesException(allIds.size(), this.minFrames);
    	}
		

		Board board = GridBoard.create(MARKERS_X, MARKERS_Y, MARKER_LENGTH, MARKER_SEPARATION, DICTIONARY);
	    List<Mat> rvecs = new Vector<Mat>();
	    List<Mat> tvecs = new Vector<Mat>();
	    double repError;

	    List<Mat> allCornersConcatenated = new Vector<Mat>();
	    int total = 0;
	    for(int i = 0; i < allCorners.size(); i++){
	        total += allCorners.get(i).size();
	    }
	    Mat allIdsConcatenated = new Mat(total, 1, 4);
	    Mat markerCounterPerFrame = new Mat(allCorners.size(), 1, CvType.CV_32SC1);
	    
	    int index = 0;
	    for(int i = 0; i < allCorners.size(); i++) {
	        markerCounterPerFrame.put(i, 0, allCorners.get(i).size());
	        for(int j = 0; j < allCorners.get(i).size(); j++) {
	            allCornersConcatenated.add(allCorners.get(i).get(j));
	            //allIdsConcatenated.push_back(allIds.get(i));
	            allIdsConcatenated.put(index, 0, allIds.get(i).get(j, 0));
	            index++;
	        }
	    }

	    Mat cameraMatrix = new Mat();
	    Mat distCoeffs = new Mat();

	    repError = Aruco.calibrateCameraAruco(allCornersConcatenated, allIdsConcatenated,
	                                       markerCounterPerFrame, board, imgSize, cameraMatrix,
	                                       distCoeffs, rvecs, tvecs);

	    return new CalibrationInformation(cameraMatrix, distCoeffs);
	}
}