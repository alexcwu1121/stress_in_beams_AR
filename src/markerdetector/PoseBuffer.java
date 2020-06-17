package markerdetector;

import java.util.logging.Logger;
import java.util.logging.Level;

public class PoseBuffer {
	/*
	Temporary note

	There will be one pose per multimarker body, updating every time a multimarker body returns
	an aggregate pose

	Should implement a method to change image sampling rate, not here though
	*/
	private Pose currentPose;
	private float angleThreshold;
	private float translationThreshold;
	private Logger logger;

	public PoseBuffer(float _angleThreshold, float _translationThreshold){
		logger = Logger.getLogger("PoseBufferLog");
		logger.setLevel(Level.WARNING);
		angleThreshold = _angleThreshold;
		translationThreshold = _translationThreshold;
	}

	//Returns true if the pose is updated.
	public boolean updatePose(Pose next){
		if(this.currentPose == null || checkThresholds(next)){
			this.currentPose = next;
			return true;
		}
		//this.currentPose = next;
		return false;
		/* Check the differentials between the current pose and the proposed next pose's attributes. 
			If any attribute's differential exceeds a threshold, do not update the pose
		*/
	}

	public boolean checkThresholds(Pose next){
		boolean flag = false;
		if(Math.abs(next.xRotation() - currentPose.xRotation()) > angleThreshold){
			logger.warning("xRotation threshold exceeded");
			flag = true;
		}
		if(Math.abs(next.yRotation() - currentPose.yRotation()) > angleThreshold){
			logger.warning("yRotation threshold exceeded");
			flag = true;
		}
		if(Math.abs(next.zRotation() - currentPose.zRotation()) > angleThreshold){
			logger.warning("zRotation threshold exceeded");
			flag = true;
		}
		if(Math.abs(next.xTranslation() - currentPose.xTranslation()) > translationThreshold){
			logger.warning("xTranslation threshold exceeded");
			flag = true;
		}
		if(Math.abs(next.yTranslation() - currentPose.yTranslation()) > translationThreshold){
			logger.warning("yTranslation threshold exceeded");
			flag = true;
		}
		if(Math.abs(next.zTranslation() - currentPose.zTranslation()) > translationThreshold){
			logger.warning("zTranslation threshold exceeded");
			flag = true;
		}

		if(flag == true){
			return false;
		}
		return true;
	}

	public Pose getPose(){
		return this.currentPose;
	}
}