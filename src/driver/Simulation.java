package driver;

import org.opencv.core.*;
import markerdetector.*;

/**Interface representing a specific type of simulation.
*/

public interface Simulation {
	/**"Runs" this simulation by drawing a gradient on the given base picture using the given DetectorResults.
	@param results the results of a detection.
	@return A mat consisting of the base picture with the gradient drawn on it.
	*/
	Mat run(DetectorResults results);
}