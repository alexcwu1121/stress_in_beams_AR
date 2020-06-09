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

public class Charuco {

    public static void main(String[] args) throws IOException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // Reader for taking in from console

        System.out.print("Enter the dictionary: ");
        int dict_id = Integer.parseInt(reader.readLine()); // Could be taken in by some other method other than console

        System.out.print("Enter the width: ");
        int width = Integer.parseInt(reader.readLine());

        System.out.print("Enter the height: ");
        int height = Integer.parseInt(reader.readLine());

        Dictionary dict = Aruco.getPredefinedDictionary(dict_id);
        
        CharucoBoard board = CharucoBoard.create(width, height, (float)3, (float)2, dict);
        Mat boardImage = new Mat();
        org.opencv.core.Size s = new org.opencv.core.Size(600,500);
        board.draw(s, boardImage, 10, 1);

        String board_name = "charuco_board.png";
        Imgcodecs.imwrite(board_name, boardImage);
    }
}
