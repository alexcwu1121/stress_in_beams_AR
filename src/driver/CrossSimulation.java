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
  		int originalX;
  		int originalY;
  		double angle;
  		int width;
  		int height;
  		int expectedSideLengths = 100;
  		if(tracking){
  			int offset = 25;
  			int changeInX = (int)(corners.get(0, 1)[1] - corners.get(0, 0)[1] + corners.get(0, 2)[1] - corners.get(0, 3)[1])/2;
  			int changeInY = (int)(corners.get(0, 1)[0] - corners.get(0, 0)[0] + corners.get(0, 2)[0] - corners.get(0, 3)[0])/2;
  			angle = Math.atan2(changeInY, changeInX);
  			height = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 3)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 3)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 1)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 1)[0], 2)))/2;
  			width = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 1)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 1)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 3)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 3)[0], 2)))/2;
  			x = (int)corners.get(0, 0)[1] - (int)Math.round(offset*Math.sin(angle));
  			y = (int)corners.get(0, 0)[0] + (int)Math.round(offset*Math.cos(angle));
  			originalX = ((int)corners.get(0, 0)[1] + (int)corners.get(0, 1)[1])/2;
  			originalY = ((int)corners.get(0, 0)[0] + (int)corners.get(0, 1)[0])/2;
  		} else {
  			x = 0;
  			y = 0;
  			originalX = 0;
  			originalY = 0;
  			angle = 0.0;
  			width = expectedSideLengths;
  			height = expectedSideLengths;
  		}
  		//End section

  		Mat answer = results.baseImage();
  		for(int i = 0; i < mat.rows(); i++){
  			for(int j = 0; j < mat.cols(); j++){
  				double actualX = (j - (width/2)) * ((double)width/expectedSideLengths);
  				double actualY = i * ((double)height/expectedSideLengths);
  				double theta = Math.atan2(actualY, actualX) + angle;
  				double r = Math.sqrt(Math.pow(actualX, 2) + Math.pow(actualY, 2));
  				int currentX = x + (int)Math.round(r*Math.cos(theta));
  				int currentY = y + (int)Math.round(r*Math.sin(theta));
  				putSafe(answer, currentX, currentY, mat.get(i, j));
  				if(i == 0 && j == 0 || i == 0 && j == mat.cols() - 1){
  					Imgproc.line(answer, new Point(currentY, currentX), new Point(originalY, originalX), new Scalar(81.0, 255.0, 0.0), 2, Imgproc.LINE_AA);
  				}
  			}
  		}
  		return answer;
	}

	/*private static void drawLine(Mat dest, int fromX, int fromY, int toX, int toY, int thickness, double[] color){
		double slope = (fromY - toY)/(double)(fromX - toX);
  		int yIntercept = (int)Math.round(-fromX*slope + fromY);

  		int largerX = Math.max(fromX, toX);
  		int smallerX = Math.min(fromx, toX);
  		for(int i = smallerX; i <= largerX; i++){
  			for(int j = 0; j < ((slope != Double.POSITIVE_INFINITY && slope != Double.NEGATIVE_INFINITY) ? Math.abs(slope) : Math.abs(fromY - toY)); j++){
  				for(int k = -thickness/2; k <= thickness/2; k++){
  					for(int l = -thickness/2; l <= thickness/2; l++){
  						putSafe(dest, i + l, (int)Math.round(slope*i) + yIntercept + k + j, color);
  					}
  				}
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
