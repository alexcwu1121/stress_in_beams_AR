package markerdetector;

import java.util.*;

@FunctionalInterface
public interface Calibrator {
	CalibrationInformation calibrate(Collection<DetectorResults> frames) throws NotEnoughFramesException;
}