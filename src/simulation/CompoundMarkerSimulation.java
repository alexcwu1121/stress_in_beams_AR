package simulation;

import org.opencv.core.*;
import markerdetector.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import java.io.FileReader;
import util.*;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opencv.calib3d.Calib3d;

@HumanReadableName("Compound Marker Simulation")
public class CompoundMarkerSimulation implements Simulation {
	private final LinkedList<Integer> ids;
	private final MultiMarkerBody cMarker;

	@Internal
	public CompoundMarkerSimulation(LinkedList<Integer> ids){
		this.ids = ids;

		this.cMarker = new MultiMarkerBody(.2, new HashMap<Integer, MarkerOffset>() {{
        put(ids.get(0), new MarkerOffset(ids.get(0), 0, 0, 0, .85, -.85, 0));
        put(ids.get(1), new MarkerOffset(ids.get(1), 0, 0, 0, -.85, -.85, 0));
    	put(ids.get(2), new MarkerOffset(ids.get(2), 0, 0, 0, .85, .85, 0));
		put(ids.get(3), new MarkerOffset(ids.get(3), 0, 0, 0, -.85, .85, 0));}});
	}

	public CompoundMarkerSimulation(String config){
		this.ids = new LinkedList<Integer>();

		JSONParser parser = new JSONParser();
		JSONObject mmbConfig = new JSONObject();
		try {
			mmbConfig = (JSONObject) parser.parse(new FileReader(config));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Double tolerance = (Double) mmbConfig.get("tolerance");
		
		JSONArray markers = (JSONArray) mmbConfig.get("markerParams");

		HashMap<Integer, MarkerOffset> markerSet = new HashMap<Integer, MarkerOffset>();
		for (int i = 0; i < markers.length(); i++) {
			JSONArray marker = (JSONArray) markers.getJSONArray(i);
		
			int id = marker.getInt(0);
			this.ids.add(id);
			markerSet.put(id, new MarkerOffset(id, marker.getDouble(1),
				marker.getDouble(2),
				marker.getDouble(3),
				marker.getDouble(4),
				marker.getDouble(5),
				marker.getDouble(6)));
		}

		this.cMarker = new MultiMarkerBody(tolerance, markerSet);
	}

	public Mat run(DetectorResults results){
		/*
		Example usage of multi marker body must include null checking
		*/
		Pair<Mat, Mat> prediction = this.cMarker.predictCenter(results);
		if(prediction == null){
			return results.baseImage();
		}

		Mat finalMatrix = results.baseImage();

		CalibrationInformation ci = results.calibrationInformation();
		Calib3d.drawFrameAxes(finalMatrix, ci.cameraMatrix(), ci.distCoeffs(), prediction.first(), prediction.second(), 0.5F);
		return finalMatrix;
	}
}
