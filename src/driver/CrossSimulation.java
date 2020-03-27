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
	    MarkerInformation information = results.getMarkerInformation(trackingID);
	    if(information == null){
	    	return results.baseImage();
	    }

	    //Edit this section of code to change the values put into the crossection.
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

  		//Edit this section of code to change where the crossection is drawn on the screen.
  		Mat corners = information.corners();
  		int x; 
  		int y;
  		int changeInX;
  		int changeInY;
  		int width;
  		int height;
  		int expectedSideLengths = 100;
  		if(tracking){
  			x = (int)corners.get(0, 0)[1];
  			y = (int)corners.get(0, 0)[0];
  			changeInX = (int)(corners.get(0, 1)[1] - corners.get(0, 0)[1] + corners.get(0, 2)[1] - corners.get(0, 3)[1])/2;
  			changeInY = (int)(corners.get(0, 1)[0] - corners.get(0, 0)[0] + corners.get(0, 2)[0] - corners.get(0, 3)[0])/2;
  			height = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 3)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 3)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 1)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 1)[0], 2)))/2;
  			width = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 1)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 1)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 3)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 3)[0], 2)))/2;

  			//System.out.println(width);
  			//System.out.println(height);
  			//System.out.println();
  		} else {
  			x = 0;
  			y = 0;
  			changeInX = 0;
  			changeInY = 0;
  			width = expectedSideLengths;
  			height = expectedSideLengths;
  		}
  		double angle = Math.atan2(changeInY, changeInX);
  		
  		//End section

  		Mat answer = results.baseImage();
  		for(int i = 0; i < mat.rows(); i++){
  			for(int j = 0; j < mat.cols(); j++){
  				//double theta = Math.atan2((double)j + changeInY, (double)i + changeInX)/* + angle*/;
  				//double proportion = (double)width/height;
  				double actualX = j * ((double)width/expectedSideLengths);
  				double actualY =  i * ((double)height/expectedSideLengths);
  				double theta = Math.atan2(actualY, actualX) + angle;
  				double r = Math.sqrt(Math.pow(actualX, 2) + Math.pow(actualY, 2));
  				putSafe(answer, x + (int)Math.round(r*Math.cos(theta)), y + (int)Math.round(r*Math.sin(theta)), mat.get(i, j));
  			}
  		}
		return answer;
	}

	private static void putSafe(Mat dest, int x, int y, double[] data){
		if(x >= dest.rows() || y >= dest.cols() || x < 0 || y < 0){
			return;
		}
		dest.put(x, y, data);
	}
}