package config;

import java.io.*;
import org.json.*;
import java.util.*;

public class ConfigGenerator {
	public static void main(String[] args) throws IOException {
		saveConfig("./config/SimpleSimulation.json", "[1.0]");
		saveConfig("./config/CrossSimulation.json", "[5, 9, 7]");
		saveConfig("./config/DividedSimulation.json", "[0, 10, 20]");
		saveConfig("./config/CoordinateTestSimulation.json", "[1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]");
		saveConfig("./config/eligibleSimulations.json", "[\"simulation.CompoundMarkerSimulation\", \"simulation.CoordinateTestSimulation\", \"simulation.CrossSimulation\", \"simulation.DividedSimulation\", \"simulation.SimpleSimulation\"]");
		JSONObject mmb = new JSONObject();
		mmb.put("filterTol", .2);
		JSONArray markerParams = new JSONArray();
		markerParams.put(markerObject(0, 0d, 0d, 0d, .85, -.85, 0d));
		markerParams.put(markerObject(1, 0d, 0d, 0d, -.85, -.85, 0d));
		markerParams.put(markerObject(2, 0d, 0d, 0d, .85, .85, 0d));
		markerParams.put(markerObject(3, 0d, 0d, 0d, -.85, .85, 0d));
		mmb.put("offsets", markerParams);
		JSONArray ja = new JSONArray();
		ja.put(mmb);
		saveConfig("./config/CompoundMarkerSimulation.json", ja);
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