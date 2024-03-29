package markerdetector;

import org.opencv.core.*;
import org.opencv.aruco.*;
import java.util.List;
import java.util.LinkedList;

/**Container class containing all results from a marker detection operation, including the base image, 
dictionary used, corners of detected markers, corners of rejected markers, rotation vectors, and translation vectors.
@author Owen Kulik
@author Nicholas Mataczynski
*/

public class DetectorResults {
	private final Mat baseMatrix;
	private final Dictionary dict;
	private final Mat ids;
	private final List<Mat> corners;
	private final List<Mat> rejected;
	private final Mat rotationVectors;
	private final Mat translationVectors;
	private final CalibrationInformation calibrationInformation;

	/**Constructs a DetectorResults with the specified values.
	*/
	DetectorResults(Mat base, Dictionary dict, Mat ids, List<Mat> corners, List<Mat> rejected, Mat rotationVectors, Mat translationVectors, CalibrationInformation ci){
		this.baseMatrix = base;
		this.dict = dict;
		this.ids = ids;
		this.corners = corners;
		this.rejected = rejected;
		this.rotationVectors = rotationVectors;
		this.translationVectors = translationVectors;
		this.calibrationInformation = ci;
	}

	/**Constructs a DetectorResults with the same values as the original, but a new base Mat
	*/
	public DetectorResults(Mat base, DetectorResults oldDetector){
		this.baseMatrix = base;
		this.dict = oldDetector.dict;
		this.ids = oldDetector.ids;
		this.corners = oldDetector.corners;
		this.rejected = oldDetector.rejected;
		this.rotationVectors = oldDetector.rotationVectors;
		this.translationVectors = oldDetector.translationVectors;
		this.calibrationInformation = oldDetector.calibrationInformation;
	}

	/**Returns a MarkerInformation object representing information for the marker specified by id.
	@throws IllegalArgumentException if id < 0.
	@return a MarkerInformation object representing information for the marker specified by id, or null if the marker was not detected.
	*/
	public MarkerInformation getMarkerInformation(int id){
		if(id < 0){
			throw new IllegalArgumentException();
		}
		int index = -1;
		for(int i = 0; i < this.ids.rows(); i++){
			if((int)ids.get(i, 0)[0] == id){
				index = i;
				break;
			}
		}
		if(index == -1){
			return null;
		}
		return new MarkerInformation(id, corners.get(index), new Pose(rotationVectors.row(index), translationVectors.row(index)));
	}

	/**Returns the base image.
	@return the base image.
	*/
	public Mat baseImage(){
		return MatMathUtils.copyof(this.baseMatrix);
	}

	/**Returns the dictionary used to detect markers.
	@return the dictionary used to detect markers.
	*/
	public Dictionary dictionary(){
		return dict;
	}

	/**Returns a list of all ids detected in a frame.
	@return a list of all ids detected in a frame.
	*/
	public Mat getIds(){
		return MatMathUtils.copyof(ids);
	}

	/**Returns the corners of all detected markers.
	@return the corners of all detected markers.
	*/
	public List<Mat> corners(){
		List<Mat> answer = new LinkedList<Mat>();
		for(Mat m : this.corners){
			answer.add(MatMathUtils.copyof(m));
		}
		return List.copyOf(answer);
	}

	/**Returns the corners of all rejected markers.
	@return the corners of all rejected markers.
	*/
	public List<Mat> rejected(){
		List<Mat> answer = new LinkedList<Mat>();
		for(Mat m : this.rejected){
			answer.add(MatMathUtils.copyof(m));
		}
		return List.copyOf(answer);
	}

	/**Returns the rotation vectors for all detected markers.
	@return the rotation vectors for all detected markers.
	*/
	public Mat rotationVectors(){
		return MatMathUtils.copyof(this.rotationVectors);
	}

	/**Returns the translation vectors for all detected markers.
	@return the translation vectors for all detected markers.
	*/
	public Mat translationVectors(){
		return MatMathUtils.copyof(this.translationVectors);
	}

	/**Returns the calibration information used in this marker detection.
	@return the calibration information used in this marker detection.
	*/
	public CalibrationInformation calibrationInformation(){
		return this.calibrationInformation;
	}
}