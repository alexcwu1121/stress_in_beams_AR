package util;

import java.io.*;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.cli.*;
import org.json.*;
import org.opencv.aruco.*;
import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class Marker {

    // GOAL: Make marker organization and printing better
    // Goes through each marker and assign a number
    // Markers with same number get grouped together and printed out together

    public static void main(String[] args) throws IOException {

    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // Reader for taking in from console

        System.out.print("Enter the dictionary: ");
        int dict_id = Integer.parseInt(reader.readLine()); // Could be taken in by some other method other than console

        System.out.print("Enter the number of groups: ");
        Integer numGroups = Integer.parseInt(reader.readLine()); // Could be taken in by some other method other than console

        List<Integer> groups = new ArrayList<Integer>();
        for (int i=0; i<numGroups; i++) {

            System.out.print("Enter the number of markers for group " + (i+1) + ": ");
            Integer input = Integer.parseInt(reader.readLine()); // Once again, could be changed to read from elsewhere other than console
            groups.add(input);
        }

        Integer total = 0;
        Dictionary dict = Aruco.getPredefinedDictionary(dict_id);
        
        for (int i=0; i<numGroups; i++) { // Printing of groups

        	GridBoard board = GridBoard.create(groups.get(i), 1, (float)0.04, (float)0.01, dict, total);
        	Mat boardImage = new Mat();
        	org.opencv.core.Size s = new org.opencv.core.Size(600,500);
        	board.draw(s, boardImage, 10, 1);
            total += groups.get(i);

			String board_name = "group_" + (i+1) + ".png";
     		Imgcodecs.imwrite(board_name, boardImage);
        }
	}
}
