package markerdetector;

public class PoseBuffer {
	private Pose currentPose;

	public PoseBuffer(){}

	public PoseBuffer(Pose initial){
		this.currentPose = initial;
	}

	//Returns true if the pose is updated.
	public boolean updatePose(Pose next){
		if(this.currentPose == null){
			this.currentPose = next;
			return true;
		}
	}

	public Pose getPose(){
		return this.currentPose;
	}
}