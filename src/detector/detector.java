package detector;

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

public class Detector {

    // no idea if these types are right
    private Dictionary markers;
    private DetectorParameters params = DetectorParameters.create();
    private Mat cameraMatrix;
    private Mat distCoeffs;

    /**Constructs a detector.
    @param detectorConfig Path to a file containing the detector options.
    @param cameraConfig Path to a file containing the camera matrix and distortion coefficients.
    */
    public Detector(String detectorConfig, String cameraConfig) throws IOException {
        //this.readDetectorParameters(detectorConfig);
        this.readCameraParameters(cameraConfig);
        //cameraMatrix = Mat.eye(3, 3, 1);
        //distCoeffs = new Mat();
    }

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
        this.cameraMatrix = parseMat(obj.getJSONObject("camera_matrix"));
        this.distCoeffs = parseMat(obj.getJSONObject("distortion_coefficients"));
    }

    private static Mat parseMat(JSONObject obj){
        int rows = obj.getInt("rows");
        int columns = obj.getInt("cols");
        Mat answer = new Mat(rows, columns, 5);
        JSONArray ja = obj.getJSONArray("data");
        for(int i = 0; i < ja.length(); i++){
            //System.out.println(ja.getDouble(i));
            answer.put(i/columns, i%columns, ja.getDouble(i));
        }
        /*System.out.println(answer);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                System.out.print(answer.get(i, j)[0] + ", ");
            }
            System.out.println();
        }*/
        return answer;
    }

    /**Returns the camera information for this camera.
    @return a Pair of mats, the first is the camera matrix, the second is the distortion coefficients.
    */
    public Pair<Mat, Mat> getCameraInformation(){
        return new Pair<Mat, Mat>(this.cameraMatrix, this.distCoeffs);
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
        Aruco.detectMarkers(src, markers, corners, ids, this.params, rejectedImgPoints, this.cameraMatrix, this.distCoeffs);
        Mat rvecs = new Mat();
        Mat tvecs = new Mat();
        Aruco.estimatePoseSingleMarkers(corners, 1.0f, this.cameraMatrix, this.distCoeffs, rvecs, tvecs);
        return new DetectorResults(src, markers, ids, corners, rejectedImgPoints, rvecs, tvecs);
    }
}
