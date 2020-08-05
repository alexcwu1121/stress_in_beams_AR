package markerdetector;

import java.util.*;

/**Interface which represents a method of calibrating a user's camera.
@author Owen Kulik
*/

@FunctionalInterface
public interface Calibrator {
	/**Using the provided DetectorResults objects as input, returns a CalibrationInformation object representing the camera's calibration.
	@param frames The input frames, represented as DetectorResults objects.
	@throws NullPointerException if frames is null.
	@throws NotEnoughFramesException if not enough frames were provided.
	@return the camera's calibration.
	*/
	CalibrationInformation calibrate(Collection<DetectorResults> frames) throws NotEnoughFramesException;
}