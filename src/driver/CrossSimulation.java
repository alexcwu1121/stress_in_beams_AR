package driver;

import markerdetector.*;
import crosssection.*;
import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.*;

/**Simulation which draws a crossection on the screen.<br>
The crossection can either be drawn in the top left corner or it can track the marker being tested.
@author Owen Kulik
*/

public class CrossSimulation implements Simulation {
	private final Plane cross;

	private final Mat cameraMatrix;
	private final Mat distCoeffs;
	private final int trackingID;
	private final boolean tracking;

	/**Constructs a CrossSimulation using the given values.
	@param cameraMatrix the camera matrix to use.
	@param distCoeffs the distortion coefficients to use.
	@param idToTrack the marker id to base data off of.
	@param tracking if true, the crossection will be drawn next to the tracked marker. If false, will be drawn in the top left corner.
	@throws IllegalArgumentException if idToTrack is negative.
	*/
	//Will probably need to change the signature of this constructor to take extra data about which IDs to look for.
	public CrossSimulation(Mat cameraMatrix, Mat distCoeffs, int idToTrack, boolean tracking){
		if(idToTrack < 0){
			throw new IllegalArgumentException("Marker ids cannot be negative");
		}
		if(cameraMatrix == null || distCoeffs == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		trackingID = idToTrack;
		this.tracking = tracking;

		//Hardcoded values, feel free to change
		cross = new Plane(1.0, .2, 1.5);
	}

	/**Runs the simulation.
	@param results The detector results object.
	@return The result mat.
	*/
	public Mat run(DetectorResults results){

		//EDIT THIS SECTION OF CODE TO CHANGE THE VALUES PUT INTO THE CROSSSECTION (should probably be put into another method)
	    MarkerInformation information = results.getMarkerInformation(trackingID);
	    if(information == null){
	    	return results.baseImage();
	    }
		Mat rotation = information.rotationVector();
		Mat translation = information.translationVector();
		int scale = 10;
		int[] vec1 = {scale*(int)rotation.get(0, 0)[0], scale*(int)rotation.get(0, 0)[1], scale*(int)rotation.get(0, 0)[2]};
		int[] vec2 = {scale*(int)translation.get(0, 0)[0], scale*(int)translation.get(0, 0)[1], scale*(int)translation.get(0, 0)[2]};
		cross.planeUpdate(vec1, vec2);
		//End section

		BufferedImage bi = cross.getImage();
		Mat mat = /*results.baseImage();*/new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
  		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
  		mat.put(0, 0, data);

  		Mat corners = information.corners();
  		int x; 
  		int y; 
  		if(tracking){
  			x = (int)corners.get(0, 0)[1];
  			y = (int)corners.get(0, 0)[0];
  		} else {
  			x = 0;
  			y = 0;
  		}

  		Mat answer = results.baseImage();
  		for(int i = 0; i < mat.rows(); i++){
  			for(int j = 0; j < mat.cols(); j++){
  				if(x + i < answer.rows() && y + j < answer.cols()){
  					answer.put(x + i, y + j, mat.get(i, j));
  				}
  			}
  		}
		return answer;
	}
}