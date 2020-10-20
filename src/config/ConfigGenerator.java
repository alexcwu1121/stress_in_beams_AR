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
		saveConfig("./config/eligibleSimulations.json", "[\"simulation.CoordinateTestSimulation\", \"simulation.CrossSimulation\", \"simulation.DividedSimulation\", \"simulation.SimpleSimulation\"]");
	}

	private static void saveConfig(String path, Object config) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(path));
    	out.print(config.toString());
	}
}