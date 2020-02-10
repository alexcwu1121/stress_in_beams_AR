package detector;

import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import java.io.FileInputStream;
import org.opencv.imgcodecs.*;
import java.io.*;
import java.util.Scanner;

import org.json.*;
import java.io.FileWriter;

public class Detector {

    // no idea if these types are right
    private Dictionary markers;
    private DetectorParameters params = DetectorParameters.create();
    private Mat cameraMatrix;
    private Mat distCoeffs;

    public Detector(String detectorConfig, String cameraConfig) throws IOException {
        this.readDetectorParameters(detectorConfig);
        this.readCameraParameters(cameraConfig);
    }

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

    public static boolean saveCameraParams(String filename, Size imageSize, float aspectRatio, int flags, Mat cameraMatrix, Mat distCoeffs, double totalAvgErr){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("image_width", imageSize.width);
        jsonObject.put("image_height", imageSize.height);
        jsonObject.put("flags", flags);
        jsonObject.put("camera_matrix", cameraMatrix);
        jsonObject.put("distortion_coefficients", distCoeffs);
        jsonObject.put("avg_reprojection_error", totalAvgErr);

        try{
            FileWriter file = new FileWriter(filename);
            file.write(jsonObject.toString());
            file.close();
            return true;
            
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void readCameraParameters(String filename) throws IOException {
        String content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
        JSONObject obj = new JSONObject(content);
        //?????
        //fs["camera_matrix"] >> this.cameraMatrix;
        //?????
        //fs["distortion_coefficients"] >> this.distCoeffs;
    }

    public Pair<Mat, Mat> getCameraInfomation(){
        return new Pair<Mat, Mat>(cameraMatrix, distCoeffs);
    }

    public Pair<Mat, Mat> detectMarkers(Mat src, int dict_id, boolean estimatePose){
        Dictionary markers = Aruco.getPredefinedDictionary(dict_id);
        List<Mat> corners = new LinkedList<Mat>();
        Mat ids = new Mat();
        List<Mat> rejectedImgPoints = new LinkedList<Mat>();
        Aruco.detectMarkers(src, markers, corners, ids, this.parameters, rejectedImgPoints, this.cameraMatrix, this.distCoeffs);
        Mat rvecs = new Mat();
        Mat tvecs = new Mat();
        Aruco.estimatePoseSingleMarkers(corners, 1.0, this.cameraMatrix, this.distCoeffs, rvecs, tvecs);
        return new Pair<Mat, Mat>(rvecs, tvecs);
    }

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Detector test = new Detector("detector_params.json");
    }

}
