package markerdetector;

import org.opencv.core.*;
import org.json.*;

public class CalibrationInformation {
	private final Mat cameraMatrix;
	private final Mat distCoeffs;

	public CalibrationInformation(Mat cameraMatrix, Mat distCoeffs){
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
	}

	public Mat cameraMatrix(){
		return MarkerUtils.copyof(this.cameraMatrix);
	}

	public Mat distCoeffs(){
		return MarkerUtils.copyof(this.distCoeffs);
	}

	public JSONObject toJSONObject(){
		JSONObject answer = new JSONObject();
		answer.put("camera_matrix", MarkerUtils.matToJSON(this.cameraMatrix));
		answer.put("distortion_coefficients", MarkerUtils.matToJSON(this.distCoeffs));
		return answer;
	}

	public static CalibrationInformation fromJSONObject(JSONObject obj){
		return new CalibrationInformation(MarkerUtils.jsonToMat(obj.getJSONObject("camera_matrix")), MarkerUtils.jsonToMat(obj.getJSONObject("distortion_coefficients")));
	}
}