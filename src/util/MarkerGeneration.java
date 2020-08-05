package util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.opencv.aruco.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;

public class MarkerGeneration {

    public static Mat pad_mat(int spacing, int border_width, Mat marker_mat){
        //Construct empty, white matrix
        Mat padded_mat = new Mat((int)marker_mat.size().width+2*(spacing + border_width),
            (int)marker_mat.size().height+2*(spacing + border_width),
            0,
            new Scalar(255));

        //Generate black border
        for(int row = 0; row < padded_mat.size().width; row++){
            for(int col = 0; col < padded_mat.size().height; col++){
                if((row < border_width || row > marker_mat.size().width + border_width + 2*spacing) ||
                    (col < border_width || col > marker_mat.size().height + border_width + 2*spacing)){
                    padded_mat.put(row, col, 0);
                }
            }
        }

        //Insert original mat
        for(int row = 0; row < marker_mat.size().width; row++){
            for(int col = 0; col < marker_mat.size().height; col++){
                padded_mat.put(row + spacing + border_width,
                                col + spacing + border_width,
                                marker_mat.get(row, col));
            }
        }

        //padded_mat.put(row, col, marker_mat.get(row-spacing-border_width, col-spacing-border_width));
        return padded_mat;
    }

    public static void main(String[] args) throws IOException {

    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    	//ArrayList<org.opencv.aruco.Dictionary> dictionaries = new ArrayList<org.opencv.aruco.Dictionary>();
    	//dictionaries.add(Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50));
        Dictionary markers = Aruco.getPredefinedDictionary(4);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // Reader for taking in from console

        System.out.print("Enter the number of groups: ");
        Integer numGroups = Integer.parseInt(reader.readLine()); // Could be taken in by some other method other than console
        System.out.println();

        List<Integer> groups = new ArrayList<Integer>();
        for (int i = 0; i < numGroups; i++) {

          System.out.print("Enter the number of markers for group " + (i+1) + ": ");
          Integer input = Integer.parseInt(reader.readLine()); // Once again, could be changed to read from elsewhere other than console
          groups.add(input);
        }

        Integer total = 0;
        for(int i = 0; i < numGroups; i++){
            for(int j = 0; j < groups.get(i); j++){
                Mat m = new Mat();
                Aruco.drawMarker(markers, total, 256, m, 1);
                Mat padded = MarkerGeneration.pad_mat(40, 20, m);
                Imgcodecs.imwrite("./group_" + Integer.toString(i) + "_dictid_" + Integer.toString(total) + ".png", padded);
                total += 1;
            }
        }
	}
}