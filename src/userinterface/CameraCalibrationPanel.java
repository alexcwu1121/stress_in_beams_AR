package userinterface;

import javax.swing.*;
import simulation.*;
import markerdetector.*;
import java.awt.event.*;
import org.opencv.core.*;
import org.opencv.aruco.*;
import java.util.*;
import util.*;

/**Subclass of SimulationPanel which is used for camera calibration.
@author Owen Kulik
*/

public class CameraCalibrationPanel extends SimulationPanel implements KeyListener {
	private CalibrationInformation calibration;
	private CameraCalibrationSimulation simulation;
	private Calibrator calibrator;

    //Java mandates this stupid design pattern
    private CameraCalibrationPanel(Calibrator calibrator, SimulationParameters<CameraCalibrationSimulation> ccs){
    	super(List.of(OptionalSimulationParameters.of(true, ccs)));
    	if(calibrator == null){
    		throw new NullPointerException();
    	}
    	this.simulation = this.getRunningSimulation(CameraCalibrationSimulation.class);
    	this.calibrator = calibrator;
    }

    /**Constructs a CameraCalibrationPanel from the given Calibrator.
    @param calibrator the calibration algorithm to use.
    @throws NullPointerException if calibrator is null.
    */
	public CameraCalibrationPanel(Calibrator calibrator){
		this(calibrator, new SimulationParameters<CameraCalibrationSimulation>(CameraCalibrationSimulation.class));
	}

	/**Returns the results of camera calibration, blocking until results are available.
	@throws InterruptedException if the thread is interrupted while waiting for results.
	@return the results of camera calibration
	*/
	public synchronized CalibrationInformation calibrateCamera() throws InterruptedException {
		this.wait();
		return this.calibrateCameraNonBlocking();
	}

	/**Returns the results of camera calibration, or null if results are not currently available.
	@return the results of camera calibration
	*/
	public synchronized CalibrationInformation calibrateCameraNonBlocking(){
		return this.calibration;
	}

	public void keyPressed(KeyEvent ke){
		if(ke.getKeyCode() == KeyEvent.VK_ENTER){
			try{
				this.calibrate();
			} catch(NotEnoughFramesException e){
				JOptionPane.showMessageDialog(this, "Only " + e.framesCaptured() + " frames were captured out of a required " + e.framesRequired(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void keyReleased(KeyEvent ke){}

	public void keyTyped(KeyEvent ke){
		if(ke.getKeyChar() == 'c'){
			this.simulation.captureNextFrame = true;
		}
	}

	private synchronized void calibrate() throws NotEnoughFramesException {
    	this.calibration = this.calibrator.calibrate(this.simulation.capturedFrames);
	    this.notifyAll();
	}

	private static class CameraCalibrationSimulation implements Simulation {
		private boolean captureNextFrame;
		private List<DetectorResults> capturedFrames = new LinkedList<DetectorResults>();

		public CameraCalibrationSimulation(){}

		public Mat run(DetectorResults results){
			DetectorParameters params = DetectorParameters.create();
			Mat image = results.baseImage();
			List<Mat> corners = results.corners();

            Mat imageCopy = image;
            if(results.getIds().size().area() > 0) Aruco.drawDetectedMarkers(imageCopy, corners);

			if(this.captureNextFrame){
				this.captureNextFrame = false;
				capturedFrames.add(results);
			}
			return imageCopy;
		}
	}
}