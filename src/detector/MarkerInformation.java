package detector;

import org.opencv.core.*;

/**Class representing detection results for a single marker.
*/

public class MarkerInformation {
	private final int id;
	private final Mat corners;
	private final Mat rotationVector;
	private final Mat translationVector;

	/**Constructs a MarkerInformation with the given information.
	*/
	MarkerInformation(int id, Mat corners, Mat rotation, Mat translation){
		this.id = id;
		this.corners = corners;
		this.rotationVector = rotation;
		this.translationVector = translation;
	}

	/**Returns this marker's id.
	@return this marker's id.
	*/
	public int id(){
		return id;
	}

	/**Returns this marker's corners.
	@return this marker's corners.
	*/
	public Mat corners(){
		return corners;
	}

	/**Returns this marker's rotation vector.
	@return this marker's rotation vector.
	*/
	public Mat rotationVector(){
		return rotationVector;
	}

	/**Returns this marker's translation vector.
	@return this marker's translation vector.
	*/
	public Mat translationVector(){
		return translationVector;
	}


}