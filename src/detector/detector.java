package detector;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import java.io.FileInputStream;
import org.opencv.imgcodecs.*;
import java.io.File;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileReader;

public class Detector {

    // no idea if these types are right
    private static Dictionary markers;
    private static DetectorParameters params;

    public Detector(String detectorConfig) {
        params = DetectorParameters.create();
        readDetectorParameters(detectorConfig);
    }

    public static boolean readDetectorParameters(String filename){
        try{
            String content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(content);
            params.set_adaptiveThreshWinSizeMin(obj.getInt("adaptiveThreshWinSizeMin"));
            params.set_adaptiveThreshWinSizeMax(obj.getInt("adaptiveThreshWinSizeMax"));
            params.set_adaptiveThreshWinSizeStep(obj.getInt("adaptiveThreshWinSizeStep"));
            params.set_adaptiveThreshConstant(obj.getDouble("adaptiveThreshConstant"));
            params.set_minMarkerPerimeterRate(obj.getDouble("minMarkerPerimeterRate"));
            params.set_maxMarkerPerimeterRate(obj.getDouble("maxMarkerPerimeterRate"));
            params.set_polygonalApproxAccuracyRate(obj.getDouble("polygonalApproxAccuracyRate"));
            params.set_minCornerDistanceRate(obj.getDouble("minCornerDistance"));
            params.set_minDistanceToBorder(obj.getInt("minDistanceToBorder"));
            params.set_minMarkerDistanceRate(obj.getDouble("minMarkerDistance"));
            params.set_cornerRefinementMethod(obj.getInt("cornerRefinementMethod"));
            params.set_cornerRefinementWinSize(obj.getInt("cornerRefinementWinSize"));
            params.set_cornerRefinementMaxIterations(obj.getInt("cornerRefinementMaxIterations"));
            params.set_cornerRefinementMinAccuracy(obj.getDouble("cornerRefinementMinAccuracy"));
            params.set_markerBorderBits(obj.getInt("markerBorderBits"));
            params.set_perspectiveRemovePixelPerCell(obj.getInt("perspectiveRemovePixelPerCell"));
            params.set_perspectiveRemoveIgnoredMarginPerCell(obj.getDouble("perspectiveRemoveIgnoredMarginPerCell"));
            params.set_maxErroneousBitsInBorderRate(obj.getDouble("maxErroneousBitsInBorderRate"));
            params.set_minOtsuStdDev(obj.getDouble("minOtsuStdDev"));
            params.set_errorCorrectionRate(obj.getDouble("errorCorrectionRate"));

            return true;
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public static readCameraParameters(string filename, Mat camMatrix, Mat distCoeffs) {
        FileStorage fs(filename, FileStorage::READ);
        if(!fs.isOpened()){
            return false;
        }
        fs["camera_matrix"] >> camMatrix;
        fs["distortion_coefficients"] >> distCoeffs;
        return true;
    }

    public static Mat detectMarkers(Mat src, int dict_id, boolean estimatePose){
        // return an empty mat containing all lines
        markers = getPredefinedDictionary(Aruco.PREDEFINED_DICTIONARY_NAME(dict_id));

        Mat camMatrix, distCoeffs;
        if(estimatePose) {
            boolean readOk = readCameraParameters(parser.get<string>("c"), camMatrix, distCoeffs);
            if(!readOk) {
                cerr << "Invalid camera file" << endl;
                return 0;
            }
        }
        //detectMarkers(image, dictionary, corners, ids, detectorParams, rejected);
        return src;
    }

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Detector test = new Detector("detector_params.json");
    }

}
