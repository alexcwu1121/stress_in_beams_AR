package config;

import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;

public class MMBConfigGenerator {
	public static void main(String[] args) throws IOException {
		JSONObject mmb = new JSONObject();
		mmb.put("tolerance", .2);
		
		JSONArray markerParams = new JSONArray();
		markerParams.put(markerArray(0, 0d, 0d, 0d, .85, -.85, 0d));
		markerParams.put(markerArray(1, 0d, 0d, 0d, -.85, -.85, 0d));
		markerParams.put(markerArray(2, 0d, 0d, 0d, .85, .85, 0d));
		markerParams.put(markerArray(3, 0d, 0d, 0d, -.85, .85, 0d));

		mmb.put("markerParams", markerParams);

		FileWriter f = new FileWriter("./config/quadmarker.json");
		f.write(mmb.toString());
		f.close();
	}

	public static JSONArray markerArray(int id, Double xrot, Double yrot, Double zrot, Double xtrans, Double ytrans, Double ztrans){
		JSONArray marker = new JSONArray();
		marker.put(id);
		marker.put(xrot);
		marker.put(yrot);
		marker.put(zrot);
		marker.put(xtrans);
		marker.put(ytrans);
		marker.put(ztrans);
		return marker;
	}

	private static void saveConfig(String path, Object config) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(path));
    	out.print(config.toString());
	}
}