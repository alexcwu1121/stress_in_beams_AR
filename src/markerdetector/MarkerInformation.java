package markerdetector;

import org.opencv.core.*;
import org.opencv.calib3d.*;
import util.*;

/**Class representing detection results for a single marker.<br>
@author Owen Kulik
*/

public class MarkerInformation {
	private final int id;
	private final Mat corners;
	private final Pose pose;

	/**Constructs a MarkerInformation with the given information.
	*/
	MarkerInformation(int id, Mat corners, Pose pose){
		this.id = id;
		this.corners = corners;
		this.pose = pose;
	}

	/**Returns this marker's id.
	@return this marker's id.
	*/
	public int id(){
		return this.id;
	}

	/**Returns this marker's corners.
	@return this marker's corners.
	*/
	public Mat corners(){
		return MatMathUtils.copyof(this.corners);
	}

	/**Returns this marker's pose object
	@return this marker's pose object
	*/
	public Pose pose(){
		return this.pose;
	}

}