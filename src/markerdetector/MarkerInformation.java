package markerdetector;

import org.opencv.core.*;
import org.opencv.calib3d.*;
import util.*;

/**Class representing detection results for a single marker.<br>
Note that methods returning standard rvecs and tvecs return a 1x1 matrix with three channels,<br>
Whereas methods returning converted rvecs and tvecs return a 3X1 matrix with one channel.<br>
I have no idea if this is correct or not.
@author Owen Kulik
*/

public class MarkerInformation {
	private final int id;
	private final Mat corners;
	private final Mat rotationVector;
	private final Mat translationVector;
	private Mat rotationVector3D;
	private Mat translationVector3D;

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
		return MarkerUtils.copyof(this.corners);
	}

	/**Returns this marker's rotation vector.
	@return this marker's rotation vector.
	*/
	public Mat rotationVector(){
		return MarkerUtils.copyof(this.rotationVector);
	}

	/**Returns this marker's translation vector.
	@return this marker's translation vector.
	*/
	public Mat translationVector(){
		return MarkerUtils.copyof(this.translationVector);
	}

	/**Returns this marker's rotation vector relative to the camera.
	@return this marker's rotation vector relative to the camera.
	*/
	public Mat rotationVector3D(){
		if(this.rotationVector3D == null){
			this.setUp3D();
		}
		return MarkerUtils.copyof(this.rotationVector3D);
	}

	/**Returns this marker's translation vector relative to the camera.
	@return this marker's translation vector relative to the camera.
	*/
	public Mat translationVector3D(){
		if(this.translationVector3D == null){
			this.setUp3D();
		}
		return MarkerUtils.copyof(this.translationVector3D);
	}

	private void setUp3D(){
		Pair<Mat, Mat> mats = MarkerUtils.get3DCoords(this.rotationVector, this.translationVector);
		this.rotationVector3D = mats.first();
		this.translationVector3D = mats.second();
	}

}