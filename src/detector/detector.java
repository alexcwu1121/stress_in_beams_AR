package detector;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import org.opencv.imgcodecs.*;

public class Detector {

    // no idea if these types are right
    private static Dictionary markers;
    private List<Integer> r_ids;
    private List<Integer> l_ids;
    private Ptr<aruco::DetectorParameters> params;

    public Detector(File detector_config) {
        params = aruco.DetectorParameters.create();
        readDetectorParameters();
    }

    private static readDetectorParameters(string filename, Ptr<aruco::DetectorParameters> params) {
        FileStorage fs(filename, FileStorage::READ);
        if(!fs.isOpened())
            return false;
        fs["adaptiveThreshWinSizeMin"] >> params->adaptiveThreshWinSizeMin;
        fs["adaptiveThreshWinSizeMax"] >> params->adaptiveThreshWinSizeMax;
        fs["adaptiveThreshWinSizeStep"] >> params->adaptiveThreshWinSizeStep;
        fs["adaptiveThreshConstant"] >> params->adaptiveThreshConstant;
        fs["minMarkerPerimeterRate"] >> params->minMarkerPerimeterRate;
        fs["maxMarkerPerimeterRate"] >> params->maxMarkerPerimeterRate;
        fs["polygonalApproxAccuracyRate"] >> params->polygonalApproxAccuracyRate;
        fs["minCornerDistanceRate"] >> params->minCornerDistanceRate;
        fs["minDistanceToBorder"] >> params->minDistanceToBorder;
        fs["minMarkerDistanceRate"] >> params->minMarkerDistanceRate;
        fs["cornerRefinementMethod"] >> params->cornerRefinementMethod;
        fs["cornerRefinementWinSize"] >> params->cornerRefinementWinSize;
        fs["cornerRefinementMaxIterations"] >> params->cornerRefinementMaxIterations;
        fs["cornerRefinementMinAccuracy"] >> params->cornerRefinementMinAccuracy;
        fs["markerBorderBits"] >> params->markerBorderBits;
        fs["perspectiveRemovePixelPerCell"] >> params->perspectiveRemovePixelPerCell;
        fs["perspectiveRemoveIgnoredMarginPerCell"] >> params->perspectiveRemoveIgnoredMarginPerCell;
        fs["maxErroneousBitsInBorderRate"] >> params->maxErroneousBitsInBorderRate;
        fs["minOtsuStdDev"] >> params->minOtsuStdDev;
        fs["errorCorrectionRate"] >> params->errorCorrectionRate;
        return true;
}

    public static Mat detectMarkers(){
        // return an empty mat containing all lines
        detectMarkers(image, dictionary, corners, ids, detectorParams, rejected);
        return src;
    }

}
