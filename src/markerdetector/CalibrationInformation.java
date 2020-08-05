package markerdetector;

import org.opencv.core.*;
import org.json.*;

/**Class which represents a camera matrix and a distortion coefficients matrix, the matrices related to camera calibration.
@author Owen Kulik
*/

public class CalibrationInformation {
	private final Mat cameraMatrix;
	private final Mat distCoeffs;

	/**Constructs a CalibrationInformation object using the given matrices.
	@param cameraMatrix the camera matrix.
	@param distCoeffs the distortion coefficients.
	@throws NullPointerException if any parameter is null.
	*/
	public CalibrationInformation(Mat cameraMatrix, Mat distCoeffs){
		if(cameraMatrix == null || distCoeffs == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
	}

	/**Returns this CalibrationInformation's camera matrix.
	@return this CalibrationInformation's camera matrix.
	*/
	public Mat cameraMatrix(){
		return MarkerUtils.copyof(this.cameraMatrix);
	}

	/**Returns this CalibrationInformation's distortion coefficients matrix.
	@return this CalibrationInformation's distortion coefficients matrix.
	*/
	public Mat distCoeffs(){
		return MarkerUtils.copyof(this.distCoeffs);
	}

	/**Constructs and returns a JSON object representing this CalibrationInformation.<br>
	This json object will have two keys: {@code "camera_matrix"} pointing to the camera matrix, and {@code "distortion_coefficients"} pointing to the distortion coefficients matrix.<br>
	The matices' JSON representation will be as specified in the MarkerUtils.matToJSON method.
	@return the JSON object.
	*/
	public JSONObject toJSONObject(){
		JSONObject answer = new JSONObject();
		answer.put("camera_matrix", MarkerUtils.matToJSON(this.cameraMatrix));
		answer.put("distortion_coefficients", MarkerUtils.matToJSON(this.distCoeffs));
		return answer;
	}

	/**Constructs and returns a CalibrationInformation object from the given JSON object.<br>
	This object must adhere to the specification in the toJSONObject method.
	@param obj The JSON object to read from.
	@throws NullPointerException if the obj is null.
	@throws JSONException if the given JSON object does not meet the specification.
	*/
	public static CalibrationInformation fromJSONObject(JSONObject obj){
		return new CalibrationInformation(MarkerUtils.jsonToMat(obj.getJSONObject("camera_matrix")), MarkerUtils.jsonToMat(obj.getJSONObject("distortion_coefficients")));
	}
}