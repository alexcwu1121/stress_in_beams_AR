package simulation;

import org.opencv.core.*;
import markerdetector.*;
import java.io.*;

public class OptionTestSimulation implements Simulation{
	private final MarkerOffset mo;

	public OptionTestSimulation(MarkerOffset mo){
		this.mo = mo;
	}

	public Mat run(DetectorResults results){
		System.out.println(this.mo);
		System.out.println();
		return results.baseImage();
	}
}