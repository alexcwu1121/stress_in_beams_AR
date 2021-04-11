package config;

import java.io.*;
import org.json.*;
import java.util.*;

public class ConfigGenerator {
	public static void main(String[] args) throws IOException {
		saveConfig("./config/SimpleSimulation.json", "[1.0]");
		saveConfig("./config/DividedSimulation.json", "[0, 10, 20]");
		saveConfig("./config/CoordinateTestSimulation.json", "[1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
		saveConfig("./config/eligibleSimulations.json", "[\"simulation.CompoundMarkerSimulation\", \"simulation.CoordinateTestSimulation\", \"simulation.CrossSimulation\", \"simulation.DividedSimulation\", \"simulation.SimpleSimulation\"]");
		
		JSONObject mmb = new JSONObject();
		mmb.put("filterTol", .25);
		JSONArray markerParams = new JSONArray();
		markerParams.put(markerObject(0, 0d, 0d, 0d, .85, -.85, 0d));
		markerParams.put(markerObject(1, 0d, 0d, 0d, -.85, -.85, 0d));
		markerParams.put(markerObject(2, 0d, 0d, 0d, .85, .85, 0d));
		markerParams.put(markerObject(3, 0d, 0d, 0d, -.85, .85, 0d));
		mmb.put("offsets", markerParams);
		JSONArray ja = new JSONArray();
		ja.put(mmb);
		saveConfig("./config/CompoundMarkerSimulation.json", ja);

		JSONArray coreGroups = new JSONArray();
		JSONObject firstGroup = new JSONObject();
		firstGroup.put("filterTol", .25);
		JSONArray firstParams = new JSONArray();
		firstParams.put(markerObject(0, .785, 0d, 0d, .85, -2.85, -1.08));
		firstParams.put(markerObject(1, .785, 0d, 0d, -.85, -2.85, -1.08));
		firstParams.put(markerObject(2, 0d, 0d, 0d, .85, -2d, -1.08));
		firstParams.put(markerObject(3, 0d, 0d, 0d, -.85, -2d, -1.08));
		firstParams.put(markerObject(4, 0d, 0d, 0d, .85, 2d, -1.08));
		firstParams.put(markerObject(5, 0d, 0d, 0d, -.85, 2d, -1.08));
		firstParams.put(markerObject(6, -.785, 0d, 0d, .85, 2.85, -1.08));
		firstParams.put(markerObject(7, -.785, 0d, 0d, -.85, 2.85, -1.08));
		firstGroup.put("offsets", firstParams);
		coreGroups.put(firstGroup);

		JSONObject middleGroup = new JSONObject();
		middleGroup.put("filterTol", .25);
		JSONArray middleParams = new JSONArray();
		middleParams.put(markerObject(8, .785, 0d, 0d, .85, -2.85, -1.08));
		middleParams.put(markerObject(9, .785, 0d, 0d, -.85, -2.85, -1.08));
		middleParams.put(markerObject(10, 0d, 0d, 0d, .85, -2d, -1.08));
		middleParams.put(markerObject(11, 0d, 0d, 0d, -.85, -2d, -1.08));
		middleParams.put(markerObject(12, 0d, 0d, 0d, .85, 2d, -1.08));
		middleParams.put(markerObject(13, 0d, 0d, 0d, -.85, 2d, -1.08));
		middleParams.put(markerObject(14, -.785, 0d, 0d, .85, 2.85, -1.08));
		middleParams.put(markerObject(15, -.785, 0d, 0d, -.85, 2.85, -1.08));
		middleGroup.put("offsets", middleParams);
		coreGroups.put(middleGroup);

		JSONObject lastGroup = new JSONObject();
		lastGroup.put("filterTol", .25);
		JSONArray lastParams = new JSONArray();
		lastParams.put(markerObject(16, .785, 0d, 0d, .85, -2.85, -1.08));
		lastParams.put(markerObject(17, .785, 0d, 0d, -.85, -2.85, -1.08));
		lastParams.put(markerObject(18, 0d, 0d, 0d, .85, -2d, -1.08));
		lastParams.put(markerObject(19, 0d, 0d, 0d, -.85, -2d, -1.08));
		lastParams.put(markerObject(20, 0d, 0d, 0d, .85, 2d, -1.08));
		lastParams.put(markerObject(21, 0d, 0d, 0d, -.85, 2d, -1.08));
		lastParams.put(markerObject(22, -.785, 0d, 0d, .85, 2.85, -1.08));
		lastParams.put(markerObject(23, -.785, 0d, 0d, -.85, 2.85, -1.08));
		lastGroup.put("offsets", lastParams);
		coreGroups.put(lastGroup);
		
		saveConfig("./config/CrossSimulation.json", coreGroups);
	}

	public static JSONObject markerObject(int id, Double xrot, Double yrot, Double zrot, Double xtrans, Double ytrans, Double ztrans){
		JSONObject marker = new JSONObject();
		marker.put("id", id);
		marker.put("xRot", xrot);
		marker.put("yRot", yrot);
		marker.put("zRot", zrot);
		marker.put("xTrans", xtrans);
		marker.put("yTrans", ytrans);
		marker.put("zTrans", ztrans);
		return marker;
	}

	private static void saveConfig(String path, Object config) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(path));
    	out.print(config.toString());
	}
}