package driver;

import markerdetector.*;
import crosssection.*;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.Imgproc;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.*;

/**Simulation which draws a crossection on the screen.<br>
The crosssection will follow the marker being tracked. In doing so, it will rotate and scale itself to match the marker.<br>
Additionally, lines are drawn from the crosssection to the marker as a visual aid.
@author Owen Kulik
*/

public class CrossSimulation implements Simulation {
	private final Plane cross;
	private final Mat cameraMatrix;
	private final Mat distCoeffs;
	private final int trackingID;

	/**Constructs a CrossSimulation using the given values.
	@param cameraMatrix the camera matrix to use.
	@param distCoeffs the distortion coefficients to use.
	@param idToTrack the marker id to base data off of.
	@throws IllegalArgumentException if idToTrack is negative.
	*/
	//Will probably need to change the signature of this constructor to take extra data about which IDs to look for.
	public CrossSimulation(Mat cameraMatrix, Mat distCoeffs, int idToTrack){
		if(idToTrack < 0){
			throw new IllegalArgumentException("Marker ids cannot be negative");
		}
		if(cameraMatrix == null || distCoeffs == null){
			throw new NullPointerException();
		}
		this.cameraMatrix = cameraMatrix;
		this.distCoeffs = distCoeffs;
		trackingID = idToTrack;

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
  		if(tracking){
  			x = (int)corners.get(0, 0)[1];
  			y = (int)corners.get(0, 0)[0];
  			changeInX = (int)(corners.get(0, 0)[1] - corners.get(0, 1)[1]);
  			changeInY = (int)(corners.get(0, 0)[0] - corners.get(0, 1)[0]);
  		} else {
  			x = 0;
  			y = 0;
  			changeInX = 0;
  			changeInY = 0;
  		}
  		double angle = Math.atan2(changeInY, changeInX);
  		//End section

  		Mat answer = results.baseImage();
  		for(int i = 0; i < mat.rows(); i++){
  			for(int j = 0; j < mat.cols(); j++){
  				//double theta = Math.atan2((double)j + changeInY, (double)i + changeInX)/* + angle*/;
  				double theta = Math.atan2((double)j, (double)i) + angle;
  				double r = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2));
  				putSafe(answer, x + (int)Math.round(r*Math.cos(theta)), y + (int)Math.round(r*Math.sin(theta)), mat.get(i, j));
  			}
  		}
	}*/

	private static void putSafe(Mat dest, int x, int y, double[] data){
		if(x >= dest.rows() || y >= dest.cols() || x < 0 || y < 0){
			return;
		}
		dest.put(x, y, data);
	}
}
