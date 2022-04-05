package markerdetector;

import org.opencv.core.*;
import org.opencv.calib3d.*;
import java.lang.Math;

/**Class holding a rotation vector and translation vector.
@author Owen Kulik
*/

public class Pose {
	protected double xRotation;
	protected double yRotation;
	protected double zRotation;
	protected final double xTranslation;
	protected final double yTranslation;
	protected final double zTranslation;
	public int counter = 0;

	/**Constructs a Pose with the given vector values.
	*/
	public Pose(double xRot, double yRot, double zRot, double xTrans, double yTrans, double zTrans){
		this.xRotation = xRot;
		this.yRotation = yRot;
		this.zRotation = zRot;
		this.xTranslation = xTrans;
		this.yTranslation = yTrans;
		this.zTranslation = zTrans;
	}

	/**Constructs a Pose with the given vectors.
	@param rvecs The rotation vector.
	@param tvecs The translation vector.
	@throws NullPointerException if any parameter is null
	@throws IllegalArgumentException if the dimensions of either mat are incompatible (ie if the mats don't have exactly three values)
	*/
	public Pose(Mat rvecs, Mat tvecs){
		if(rvecs == null || tvecs == null){
			throw new NullPointerException();
		}

		if(rvecs.rows() == 3 && rvecs.cols() == 1 && rvecs.channels() == 1){
			this.xRotation = rvecs.get(0, 0)[0];
			this.yRotation = rvecs.get(1, 0)[0];
			this.zRotation = rvecs.get(2, 0)[0];
		} else if(rvecs.rows() == 1 && rvecs.cols() == 3 && rvecs.channels() == 1){
			this.xRotation = rvecs.get(0, 0)[0];
			this.yRotation = rvecs.get(0, 1)[0];
			this.zRotation = rvecs.get(0, 2)[0];
		} else if(rvecs.rows() == 1 && rvecs.cols() == 1 && rvecs.channels() == 3){
			this.xRotation = rvecs.get(0, 0)[0];
			this.yRotation = rvecs.get(0, 0)[1];
			this.zRotation = rvecs.get(0, 0)[2];
		} else {
			throw new IllegalArgumentException("rvecs had incompatible dimensions: " + rvecs.rows() + " rows, " + rvecs.cols() + " cols, " + rvecs.channels() + " channels.");
		}
		if(tvecs.rows() == 3 && tvecs.cols() == 1 && tvecs.channels() == 1){
			this.xTranslation = tvecs.get(0, 0)[0];
			this.yTranslation = tvecs.get(1, 0)[0];
			this.zTranslation = tvecs.get(2, 0)[0];
		} else if(tvecs.rows() == 1 && tvecs.cols() == 3 && tvecs.channels() == 1){
			this.xTranslation = tvecs.get(0, 0)[0];
			this.yTranslation = tvecs.get(0, 1)[0];
			this.zTranslation = tvecs.get(0, 2)[0];
		} else if(tvecs.rows() == 1 && tvecs.cols() == 1 && tvecs.channels() == 3){
			this.xTranslation = tvecs.get(0, 0)[0];
			this.yTranslation = tvecs.get(0, 0)[1];
			this.zTranslation = tvecs.get(0, 0)[2];
		} else {
			throw new IllegalArgumentException("tvecs had incompatible dimensions: " + tvecs.rows() + " rows, " + tvecs.cols() + " cols, " + tvecs.channels() + " channels.");
		}
	}

	public void setRotationVector(Mat rvecs){
		this.xRotation = rvecs.get(0,0)[0];
		this.yRotation = rvecs.get(1,0)[0];
		this.zRotation = rvecs.get(2,0)[0];
	}

	/**Returns this Pose's x rotation.
	@return this Pose's x rotation.
	*/
	public double xRotation(){
		return this.xRotation;
	}

	/**Returns this Pose's y rotation.
	@return this Pose's y rotation.
	*/
	public double yRotation(){
		return this.yRotation;
	}

	/**Returns this Pose's z rotation.
	@return this Pose's z rotation.
	*/
	public double zRotation(){
		return this.zRotation;
	}

	/**Returns this Pose's x translation.
	@return this Pose's x translation.
	*/
	public double xTranslation(){
		return this.xTranslation;
	}

	/**Returns this Pose's y translation.
	@return this Pose's y translation.
	*/
	public double yTranslation(){
		return this.yTranslation;
	}

	/**Returns this Pose's z translation.
	@return this Pose's z translation.
	*/
	public double zTranslation(){
		return this.zTranslation;
	}

	/**Returns a 3x1, one-channel matrix representing this Pose's rotation vector.<br>
	Use rotationVector.get(i, 0)[0] to get values, with 0 <= i < 3.
	@return a matrix representing this Pose's rotation vector.
	*/
	public Mat rotationVector(){
		Mat rotationVector = new Mat(3, 1, CvType.CV_64FC1);
		rotationVector.put(0, 0, this.xRotation);
		rotationVector.put(1, 0, this.yRotation);
		rotationVector.put(2, 0, this.zRotation);
		return rotationVector;
	}

	/**Returns a 3x1, one-channel matrix representing this Pose's translation vector.<br>
	Use translationVector.get(i, 0)[0] to get values, with 0 <= i < 3.
	@return a matrix representing this Pose's translation vector.
	*/
	public Mat translationVector(){
		Mat translationVector = new Mat(3, 1, CvType.CV_64FC1);
		translationVector.put(0, 0, this.xTranslation);
		translationVector.put(1, 0, this.yTranslation);
		translationVector.put(2, 0, this.zTranslation);
		return translationVector;
	}

	/**Flips this Pose's coordinates and returns the resulting Pose.<br>
	@return a Pose with flipped coordinates.
	*/
	public Pose flipCoords(){
		Mat tvec = this.translationVector();
		Mat rvec = this.rotationVector();
		Mat r = new Mat();
		Mat translationVector3D = new Mat(3, 1, CvType.CV_64FC1);
		Mat rotationVector3D = new Mat();
		Calib3d.Rodrigues(rvec, r);
		r = r.t();
		/*double value1 = -((r.get(0, 0)[0] * tvec.get(0, 0)[0]) + (r.get(0, 1)[0] * tvec.get(0, 0)[1]) + (r.get(0, 2)[0] * tvec.get(0, 0)[2]));
		double value2 = -((r.get(1, 0)[0] * tvec.get(0, 0)[0]) + (r.get(1, 1)[0] * tvec.get(0, 0)[1]) + (r.get(1, 2)[0] * tvec.get(0, 0)[2]));
		double value3 = -((r.get(2, 0)[0] * tvec.get(0, 0)[0]) + (r.get(2, 1)[0] * tvec.get(0, 0)[1]) + (r.get(2, 2)[0] * tvec.get(0, 0)[2]));
		translationVector3D.put(0, 0, value1);
		translationVector3D.put(1, 0, value2);
		translationVector3D.put(2, 0, value3);*/
		translationVector3D = MatMathUtils.scalarMultiply(MatMathUtils.matMultiply(r, tvec), -1);
		Calib3d.Rodrigues(r, rotationVector3D);
		return new Pose(rotationVector3D, translationVector3D);
	}

	/**Returns a hash code for this Pose.
	@return a hash code for this Pose.
	*/
	@Override
	public int hashCode(){
		return (int)(this.zRotation + this.yRotation + this.zRotation + this.xTranslation + this.yTranslation + this.zTranslation);
	}

	/**Returns a boolean indicating whether this Pose is equals to the provided object.<br>
	This will return true if and only if the other object is a Pose and its rotation offsets and translation offsets are the same.
	@param other The object to compare to
	@return a boolean indicating whether this Pose is equals to the provided object.
	*/
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(this == other){
			return true;
		}
		if(!(other instanceof Pose)){
			return false;
		}
		return this.equalsInternal((Pose)other, 0.0);
	}

	/**Returns a boolean indicating whether this Pose is equal to the other Pose within the given tolerance.
	@param other The Pose to compare to.
	@param tolerance The tolerance to use in the comparison.
	@return a boolean indicating whether this Pose is equal to the other Pose within the given tolerance.
	*/
	public boolean equals(Pose other, double tolerance){
		if(other == null){
			return false;
		}
		if(this == other){
			return true;
		}
		return this.equalsInternal(other, tolerance);
	}

	private boolean equalsInternal(Pose mo, double tolerance){
		return withinTolerance(this.xRotation, mo.xRotation(), tolerance) && withinTolerance(this.yRotation, mo.yRotation(), tolerance) && 
				withinTolerance(this.zRotation, mo.zRotation(), tolerance) && withinTolerance(this.xTranslation, mo.xTranslation(), tolerance) &&
				withinTolerance(this.yTranslation, mo.yTranslation(), tolerance) &&  withinTolerance(this.zTranslation, mo.zTranslation(), tolerance);
	}

	private static boolean withinTolerance(double first, double second, double tolerance){
		return Math.abs(first - second) <= tolerance;
	}

	/**Returns a String representation of this Pose.
	@return a String representation of this Pose.
	*/
	@Override
	public String toString(){
		return "Pose - rvec: [" + this.xRotation + ", " + this.yRotation + ", " + this.zRotation + 
					"] tvec: [" + this.xTranslation + ", " + this.yTranslation + ", " + this.zTranslation + "]";
	}

	//Test cases
	/*public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		MultiMarkerBody lmo = MultiMarkerBody.fromJSONFile("markerdetector/test_marker_offset.json");
		System.out.println(lmo);
	}*/
}