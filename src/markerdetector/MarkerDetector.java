package markerdetector;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import org.opencv.imgcodecs.*;
import java.io.*;
import java.util.Scanner;
import util.*;
import java.util.List;
import java.util.LinkedList;

import org.json.*;
import java.io.FileWriter;

public class MarkerDetector {
    private Dictionary markers;
    private DetectorParameters params = DetectorParameters.create();
    private CalibrationInformation calibrationInformation;

    /**Constructs a detector.
    @param detectorConfig Path to a file containing the detector options.
    @param cameraConfig Path to a file containing the camera matrix and distortion coefficients.
    */
    public MarkerDetector(String detectorConfig, String cameraConfig) throws IOException {
        //this.readDetectorParameters(detectorConfig);
        this.readCameraParameters(cameraConfig);
        //cameraMatrix = Mat.eye(3, 3, 1);
        //distCoeffs = new Mat();
    }

    public MarkerDetector(CalibrationInformation ci){
        this.calibrationInformation = ci;
    }

    public MarkerDetector(){}

    /**public Detector(String detectorConfig) throws IOException {
        this.readDetectorParameters(detectorConfig);
        //calibrate/save camera parameters
        //this.readCameraParameters(cameraConfig);
    }*/

    private void readDetectorParameters(String filename) throws IOException {
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
    }

    private void readCameraParameters(String filename) throws IOException {
        String content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
        JSONObject obj = new JSONObject(content);
        this.calibrationInformation = CalibrationInformation.fromJSONObject(obj);
    }

    /**Returns the camera information for this camera.
    @return a Pair of mats, the first is the camera matrix, the second is the distortion coefficients.
    */
    public CalibrationInformation getCameraInformation(){
        return this.calibrationInformation;
    }

    /**Detects all markers within the source mat.
    @param src The mat to detect markers from.
    @param dict_id The dictionary to get markers from.
    @return a DetectorResults object containing the results of this detection.
    */
    public DetectorResults detectMarkers(Mat src, int dict_id){
        Dictionary markers = Aruco.getPredefinedDictionary(dict_id);
        List<Mat> corners = new LinkedList<Mat>();
        Mat ids = new Mat();
        List<Mat> rejectedImgPoints = new LinkedList<Mat>();
        Mat rvecs = null;
        Mat tvecs = null;
        if(this.calibrationInformation == null){
            Aruco.detectMarkers(src, markers, corners, ids, this.params, rejectedImgPoints);
        } else {
            rvecs = new Mat();
            tvecs = new Mat();
            Aruco.detectMarkers(src, markers, corners, ids, this.params, rejectedImgPoints, this.calibrationInformation.cameraMatrix(), this.calibrationInformation.distCoeffs());
            Aruco.estimatePoseSingleMarkers(corners, 1.0f, this.calibrationInformation.cameraMatrix(), this.calibrationInformation.distCoeffs(), rvecs, tvecs);
        }
        return new DetectorResults(src, markers, ids, corners, rejectedImgPoints, rvecs, tvecs, this.calibrationInformation);
    }
}
