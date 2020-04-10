package markerdetector;

import org.opencv.core.*;
import java.util.*;
import java.io.*;
import org.json.*;

/**Class containing a marker ID as well as rotational and translational offset information.
@author Owen Kulik
*/
public class MarkerOffset {
	private final int id;
	private final double xRotation;
	private final double yRotation;
	private final double zRotation;
	private final double xTranslation;
	private final double yTranslation;
	private final double zTranslation;

	/**Constructs a MarkerOffset object with the given fields.
	*/
	public MarkerOffset(int id, double xRot, double yRot, double zRot, double xTrans, double yTrans, double zTrans){
		this.id = id;
		this.xRotation = xRot;
		this.yRotation = yRot;
		this.zRotation = zRot;
		this.xTranslation = xTrans;
		this.yTranslation = yTrans;
		this.zTranslation = zTrans;
	}

	/**Constructs and returns an unmodifiable list containing the Marker Offsets as specified in the given JSON file. (use test_marker_offset.json as a template)
	@param file Path to the JSON file.
	@throws IOException if an IO error occurs.
	@throws NullPointerException if file is null.
	@return returns an unmodifiable list containing the Marker Offsets as specified in the given JSON file.
	*/
	public static List<MarkerOffset> fromJSONFile(String file) throws IOException {
		String content = new Scanner(new File(file)).useDelimiter("\\Z").next();
        JSONObject obj = new JSONObject(content);
        return fromJSONObject(obj);
	}

	/**Constructs and returns an unmodifiable list containing the Marker Offsets as specified in the given JSON object. (use test_marker_offset.json as a template)
	@param source The JSON object.
	@throws NullPointerException if source is null.
	@return returns an unmodifiable list containing the Marker Offsets as specified in the given JSON object.
	*/
	public static List<MarkerOffset> fromJSONObject(JSONObject source){
		List<MarkerOffset> answer = new LinkedList<MarkerOffset>();
		for(String s : source.keySet()){
			int id = Integer.valueOf(s);
			JSONObject js = source.getJSONObject(s);
			answer.add(new MarkerOffset(id, js.getDouble("xRot"), js.getDouble("yRot"), js.getDouble("zRot"), js.getDouble("xTrans"), js.getDouble("yTrans"), js.getDouble("zTrans")));
		}
		return List.copyOf(answer);
	}

	/**Returns this MarkerOffset's id.
	@return this MarkerOffset's id.
	*/
	public int id(){
		return this.id;
	}

	/**Returns this MarkerOffset's x rotation.
	@return this MarkerOffset's x rotation.
	*/
	public double xRotation(){
		return this.xRotation;
	}

	/**Returns this MarkerOffset's y rotation.
	@return this MarkerOffset's y rotation.
	*/
	public double yRotation(){
		return this.yRotation;
	}

	/**Returns this MarkerOffset's z rotation.
	@return this MarkerOffset's z rotation.
	*/
	public double zRotation(){
		return this.zRotation;
	}

	/**Returns this MarkerOffset's x translation.
	@return this MarkerOffset's x translation.
	*/
	public double xTranslation(){
		return this.xTranslation;
	}

	/**Returns this MarkerOffset's y translation.
	@return this MarkerOffset's y translation.
	*/
	public double yTranslation(){
		return this.yTranslation;
	}

	/**Returns this MarkerOffset's z translation.
	@return this MarkerOffset's z translation.
	*/
	public double zTranslation(){
		return this.zTranslation;
	}

	/**Returns a 3x1, one-channel matrix representing this MarkerOffset's rotation vector.<br>
	Use rotationVector.get(i, 0)[0] to get values, with 0 <= i < 3.
	@return a matrix representing this MarkerOffset's rotation vector.
	*/
	public Mat rotationVector(){
		Mat rotationVector = new Mat(3, 1, CvType.CV_64FC1);
		rotationVector.put(0, 0, this.xRotation);
		rotationVector.put(1, 0, this.yRotation);
		rotationVector.put(2, 0, this.zRotation);
		return rotationVector;
	}

	/**Returns a 3x1, one-channel matrix representing this MarkerOffset's translation vector.<br>
	Use translationVector.get(i, 0)[0] to get values, with 0 <= i < 3.
	@return a matrix representing this MarkerOffset's translation vector.
	*/
	public Mat translationVector(){
		Mat translationVector = new Mat(3, 1, CvType.CV_64FC1);
		translationVector.put(0, 0, this.xTranslation);
		translationVector.put(1, 0, this.yTranslation);
		translationVector.put(2, 0, this.zTranslation);
		return translationVector;
	}

	/**Returns a hash code for this MarkerOffset.
	@return a hash code for this MarkerOffset.
	*/
	public int hashCode(){
		return (int)(this.id + this.zRotation + this.yRotation + this.zRotation + this.xTranslation + this.yTranslation + this.zTranslation);
	}

	/**Returns a boolean indicating whether this MarkerOffset is equals to the provided object.<br>
	This will return true if and only if the other object is a MarkerOffset and its id, rotation offsets, and translation offsets are the same.
	@param other The object to compare to
	@return a boolean indicating whether this MarkerOffset is equals to the provided object.
	*/
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(this == other){
			return true;
		}
		if(!(other instanceof MarkerOffset)){
			return false;
		}
		MarkerOffset mo = (MarkerOffset)other;
		return this.id == mo.id && this.xRotation == mo.xRotation && this.yRotation == mo.yRotation && this.zRotation == mo.zRotation && 
					this.xTranslation == mo.xTranslation && this.yTranslation == mo.yTranslation && this.zTranslation == mo.zTranslation;
	}

	/**Returns a String representation of this MarkerOffset.
	@return a String representation of this MarkerOffset.
	*/
	public String toString(){
		return "MarkerOffset - id: " + this.id + " 3drvec: [" + this.xRotation + ", " + this.yRotation + ", " + this.zRotation + 
					"] 3dtvec: [" + this.xTranslation + ", " + this.yTranslation + ", " + this.zTranslation + "]";
	}

	//Test cases
	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		List<MarkerOffset> lmo = MarkerOffset.fromJSONFile("markerdetector/test_marker_offset.json");
		System.out.println(lmo);
		MarkerUtils.printmat(lmo.get(1).rotationVector());
		MarkerUtils.printmat(lmo.get(1).translationVector());
	}
}