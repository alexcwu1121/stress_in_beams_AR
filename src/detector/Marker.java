
// import java.lang.Math;
import java.util.*;
import org.apache.commons.cli.*;
// import org.opencv.core.*;
// import org.opencv.core.Point;
// import org.opencv.imgproc.Imgproc;
// import org.opencv.aruco.*;
// import org.opencv.imgcodecs.*;

public class Marker {

    // namespace {
    String about = "Create an ArUco marker image";
    String[] keys  =
             { "@outfile |<none> | Output image ",
            "d        |       | dictionary: DICT_4X4_50=0, DICT_4X4_100=1, DICT_4X4_250=2,",
            "DICT_4X4_1000=3, DICT_5X5_50=4, DICT_5X5_100=5, DICT_5X5_250=6, DICT_5X5_1000=7, ",
            "DICT_6X6_50=8, DICT_6X6_100=9, DICT_6X6_250=10, DICT_6X6_1000=11, DICT_7X7_50=12,",
            "DICT_7X7_100=13, DICT_7X7_250=14, DICT_7X7_1000=15, DICT_ARUCO_ORIGINAL = 16",
            "id       |       | Marker id in the dictionary ",
            "ms       | 200   | Marker size in pixels ",
            "bb       | 1     | Number of bits in marker borders ",
            "si       | false | show generated image "};
    // }


    public static void main(int argc, String argv[]) {

        // Options options = new Options();


        CommandLineParser parser = new CommandLineParser(argc, argv, keys);
        parser.about(about);

        if(argc < 4) {
            parser.printMessage();
            return;
        }

        int dictionaryId = parser.get("d");
        int markerId = parser.get("id");
        int borderBits = parser.get("bb");
        int markerSize = parser.get("ms");
        Boolean showImage = parser.get("si");

        String out = parser.get(0);

        if(!parser.check()) {
            parser.printErrors();
            return;
        }

    Ptr<Dictionary> dictionary =
        getPredefinedDictionary(PREDEFINED_DICTIONARY_NAME(dictionaryId));

        Mat markerImg;
        drawMarker(dictionary, markerId, markerSize, markerImg, borderBits);

        if(showImage) {
            imshow("marker", markerImg);
            waitKey(0);
        }

        imwrite(out, markerImg);

    }

}