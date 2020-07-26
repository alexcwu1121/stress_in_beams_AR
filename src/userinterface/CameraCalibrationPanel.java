package userinterface;

import javax.swing.*;
import driver.Simulation;
import markerdetector.*;
import java.awt.event.*;
import org.opencv.core.*;
import org.opencv.aruco.*;
import java.util.*;
import util.*;

public class CameraCalibrationPanel extends SimulationPanel implements KeyListener {
	private Mat cameraMatrix;
	private Mat distCoeffs;
	private CameraCalibrationSimulation simulation;
	private boolean calibrationComplete = false;

	private static final int DICT_ID = 4;
	private static final org.opencv.aruco.Dictionary DICTIONARY = Aruco.getPredefinedDictionary(DICT_ID);
	private static final int MIN_FRAMES = 5;
	private static final int MARKERS_X = 5;
    private static final int MARKERS_Y = 4;
    private static final float MARKER_LENGTH = .043f;
    private static final float MARKER_SEPARATION = .016f;

    //Java mandates this stupid design pattern
    private CameraCalibrationPanel(CameraCalibrationSimulation ccs){
    	super(List.of(ccs));
    	simulation = ccs;
    }

	public CameraCalibrationPanel(){
		this(new CameraCalibrationSimulation());
	}

	public synchronized CalibrationInformation calibrateCamera() throws InterruptedException {
		this.wait();
		return new CalibrationInformation(this.cameraMatrix, this.distCoeffs);
	}

	public synchronized CalibrationInformation calibrateCameraNonBlocking(){
		return this.calibrationComplete ? new CalibrationInformation(this.cameraMatrix, this.distCoeffs) : null;
	}

	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			if(this.simulation.allCorners.size() < MIN_FRAMES){
				JOptionPane.showMessageDialog(this, "You must capture at least " + MIN_FRAMES + " frames to calibrate.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.calibrate();
		}
	}

	public void keyReleased(KeyEvent e){}

	public void keyTyped(KeyEvent e){
		if(e.getKeyChar() == 'c'){
			this.simulation.captureNextFrame = true;
		}
	}

	private synchronized void calibrate(){
    	Board board = GridBoard.create(MARKERS_X, MARKERS_Y, MARKER_LENGTH, MARKER_SEPARATION, DICTIONARY);
		this.cameraMatrix = new Mat();
	    this.distCoeffs = new Mat();
	    Vector<Mat> rvecs = new Vector<Mat>();
	    Vector<Mat> tvecs = new Vector<Mat>();
	    double repError;

	    Vector<Mat> allCornersConcatenated = new Vector<Mat>();
	    int total = 0;
	    for(int i = 0; i < this.simulation.allCorners.size(); i++){
	        total += this.simulation.allCorners.get(i).size();
	    }
	    Mat allIdsConcatenated = new Mat(total, 1, 4);
	    Mat markerCounterPerFrame = new Mat(this.simulation.allCorners.size(), 1, CvType.CV_32SC1);
	    
	    int index = 0;
	    for(int i = 0; i < this.simulation.allCorners.size(); i++) {
	        markerCounterPerFrame.put(i, 0, this.simulation.allCorners.get(i).size());
	        for(int j = 0; j < this.simulation.allCorners.get(i).size(); j++) {
	            allCornersConcatenated.add(this.simulation.allCorners.get(i).get(j));
	            //allIdsConcatenated.push_back(allIds.get(i));
	            allIdsConcatenated.put(index, 0, this.simulation.allIds.get(i).get(j, 0));
	            index++;
	        }
	    }

	    repError = Aruco.calibrateCameraAruco(allCornersConcatenated, allIdsConcatenated,
	                                       markerCounterPerFrame, board, this.simulation.imgSize, this.cameraMatrix,
	                                       this.distCoeffs, rvecs, tvecs);
	    this.calibrationComplete = true;
	    this.notifyAll();
	}

	private static class CameraCalibrationSimulation implements Simulation {
		private boolean captureNextFrame;
		private Vector<Vector<Mat>> allCorners = new Vector<Vector<Mat>>();
    	private Vector<Mat> allIds = new Vector<Mat>();
    	private Size imgSize = new Size();

		public Mat run(DetectorResults results){
			DetectorParameters params = DetectorParameters.create();
			Mat image = results.baseImage();
			Mat ids = new Mat();
            Vector<Mat> corners = new Vector<Mat>();
            Vector<Mat> rejected = new Vector<Mat>();
            // detect markers
            Aruco.detectMarkers(image, DICTIONARY, corners, ids, params, rejected);

            Mat imageCopy = image;
            if(ids.size().area() > 0) Aruco.drawDetectedMarkers(imageCopy, corners);

			if(this.captureNextFrame){
				//System.out.println("Loaded frame");
				this.captureNextFrame = false;
				if(!corners.isEmpty()){
					this.allCorners.add(corners);
	                this.allIds.add(ids);
	                this.imgSize = image.size();
				}
			}
			return imageCopy;
		}
	}
}