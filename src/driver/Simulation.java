package driver;


import org.opencv.core.*;

/**Interface representing a specific type of simulation.
*/

public interface Simulation {
	/**"Runs" this simulation by drawing a gradient on the given base picture using the given matrices.
	@param basePicture The base picture to draw on.
	@param rotationMatrix The rotation matrix.
	@param translationMatrix The translation matrix.
	@param cameraMatrix The camera matrix.
	@param distCoeffs The distortion coefficients.
	@return A mat consisting of the base picture with the gradient drawn on it.
	*/
	Mat run(Mat basePicture, Mat rotationMatrix, Mat translationMatrix, Mat cameraMatrix, Mat distCoeffs);
}