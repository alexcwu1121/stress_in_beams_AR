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
        params = new DetectorParameters();
        readDetectorParameters(detectorConfig);
    }

    public static boolean readDetectorParameters(String filename){
        try{
            String content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(content);
            params.set_adaptiveThreshWinSizeMin(obj.getInt("adaptiveThreshWinSizeMin"));
            params.set_adaptiveThreshWinSizeMax(obj.getInt("adaptiveThreshWinSizeMax"));
            params.set_adaptiveThreshWinSizeStep(obj.getInt("adaptiveThreshWinSizeStep"));
            params.set_adaptiveThreshConstant(obj.getDouble("params.adaptiveThreshConstant"));
            params.set_minMarkerPerimeterRate(obj.getDouble("minMarkerPerimeterRate"));
            params.set_maxMarkerPerimeterRate(obj.getDouble("maxMarkerPerimeterRate"));
            params.set_polygonalApproxAccuracyRate(obj.getDouble("polygonalApproxAccuracyRate"));
            params.set_minCornerDistanceRate(obj.getDouble("minCornerDistanceRate"));
            params.set_minDistanceToBorder(obj.getInt("minDistanceToBorder"));
            params.set_minMarkerDistanceRate(obj.getDouble("minMarkerDistanceRate"));
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

    public static Mat detectMarkers(Mat src){
        // return an empty mat containing all lines
        //detectMarkers(image, dictionary, corners, ids, detectorParams, rejected);
        return src;
    }

    public static void main(String[] args){
        Detector test = new Detector("detector_params.json");
    }

}
