package driver;

import org.opencv.core.*;
import detector.*;

/**Simulation class made entirely for example/test purposes.
This simulation's run method simply returns the base matrix.
*/

public class NullSimulation implements Simulation {
	private static NullSimulation singleton;

	static {
		singleton = new NullSimulation();
	}

	private NullSimulation(){}

	public static NullSimulation get(){
		return singleton;
	}

	public Mat run(DetectorResults results){
		return results.baseImage();
	}
}