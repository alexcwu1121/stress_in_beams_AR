package markerdetector;

import org.opencv.core.*;
import java.util.*;
import java.io.*;
import org.json.*;

/**Class containing a marker ID as well as rotational and translational offset information.
@author Owen Kulik
*/
public class MarkerOffset extends Pose {
	private final int id;

	/**Constructs a MarkerOffset object with the given fields.
	*/
	public MarkerOffset(int id, double xRot, double yRot, double zRot, double xTrans, double yTrans, double zTrans){
		super(xRot, yRot, zRot, xTrans, yTrans, zTrans);
		this.id = id;
	}

	/**Returns this MarkerOffset's id.
	@return this MarkerOffset's id.
	*/
	public int id(){
		return this.id;
	}

	/**Returns a JSONObject representation of this MarkerOffset, suitable for use with the fromJSONObject method.
	@return a JSONObject representation of this MarkerOffset.
	*/
	public JSONObject toJSONObject(){
		JSONObject marker = new JSONObject();
		marker.put("id", this.id);
		marker.put("xRot", this.xRotation);
		marker.put("yRot", this.yRotation);
		marker.put("zRot", this.zRotation);
		marker.put("xTrans", this.xTranslation);
		marker.put("yTrans", this.yTranslation);
		marker.put("zTrans", this.zTranslation);
		return marker;
	}

	/**Constructs and returns a MarkerOffset from the given JSONObject.<br>
	JSONObjects produced by the toJSONObject method are compatible with this method.
	@param js The JSONObject to parse
	@throws JSONException if js is malformed
	@throws NullPointerException if js is null
	@return the constructed MarkerOffset.
	*/
	public static MarkerOffset fromJSONObject(JSONObject js){
		return new MarkerOffset(js.getInt("id"), js.getDouble("xRot"), js.getDouble("yRot"), js.getDouble("zRot"), js.getDouble("xTrans"), js.getDouble("yTrans"), js.getDouble("zTrans"));
	}

	/**Returns a hash code for this MarkerOffset.
	@return a hash code for this MarkerOffset.
	*/
	@Override
	public int hashCode(){
		return (int)(this.id + this.zRotation + this.yRotation + this.zRotation + this.xTranslation + this.yTranslation + this.zTranslation);
	}

	/**Returns a boolean indicating whether this MarkerOffset is equals to the provided object.<br>
	This will return true if and only if the other object is a MarkerOffset and its id, rotation offsets, and translation offsets are the same.
	@param other The object to compare to
	@return a boolean indicating whether this MarkerOffset is equals to the provided object.
	*/
	@Override
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
		return this.id.equals(mo.id()) && this.xRotation.equals(mo.xRotation()) && this.yRotation.equals(mo.yRotation()) && this.zRotation.equals(mo.zRotation()) && 
					this.xTranslation.equals(mo.xTranslation()) && this.yTranslation.equals(mo.yTranslation()) && this.zTranslation.equals(mo.zTranslation());
	}

	/**Returns a String representation of this MarkerOffset.
	@return a String representation of this MarkerOffset.
	*/
	@Override
	public String toString(){
		return "MarkerOffset - id: " + this.id + " 3drvec: [" + this.xRotation + ", " + this.yRotation + ", " + this.zRotation + 
					"] 3dtvec: [" + this.xTranslation + ", " + this.yTranslation + ", " + this.zTranslation + "]";
	}

	//Test cases
	/*public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		MultiMarkerBody lmo = MultiMarkerBody.fromJSONFile("markerdetector/test_marker_offset.json");
		System.out.println(lmo);
	}*/
}