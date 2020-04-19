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
import java.util.Vector;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.*;

import org.json.*;
import java.io.FileWriter;

public class CalibrateCamera {

    static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private static DetectorParameters params = DetectorParameters.create();
    private Mat cameraMatrix;
    private Mat distCoeffs;

    public CalibrateCamera(String detectorConfig, String cameraConfig, int dict_id){
        try{
            System.out.println(detectorConfig);
            //this.readDetectorParameters(detectorConfig);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        this.calibrateCamera(cameraConfig, dict_id);
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

    public static boolean saveCameraParams(String filename, Size imageSize, Mat cameraMatrix, Mat distCoeffs, double totalAvgErr){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("image_width", imageSize.width);
        jsonObject.put("image_height", imageSize.height);
        jsonObject.put("camera_matrix", MarkerUtils.matToJson(cameraMatrix));
        jsonObject.put("distortion_coefficients", MarkerUtils.matToJson(distCoeffs));
        jsonObject.put("avg_reprojection_error", totalAvgErr);

        MarkerUtils.printmat(cameraMatrix);
        System.out.println();
        MarkerUtils.printmat(distCoeffs);

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

    /*
    public void readCameraParameters(String filename) throws IOException {
        String content = new Scanner(new File(filename)).useDelimiter("\\Z").next();
        JSONObject obj = new JSONObject(content);
        
        Mat cameraMatrix, distCoeffs;
        vector< Mat > rvecs, tvecs;
        double repError;

        if(calibrationFlags & CALIB_FIX_ASPECT_RATIO) {
            cameraMatrix = Mat::eye(3, 3, CV_64F);
            cameraMatrix.at< double >(0, 0) = aspectRatio;
        }
        //?????
        //fs["camera_matrix"] >> this.cameraMatrix;
        //?????
        //fs["distortion_coefficients"] >> this.distCoeffs;
    }

    public Pair<Mat, Mat> getCameraInfomation(){
        return new Pair<Mat, Mat>(cameraMatrix, distCoeffs);
    }
    */

    public static void calibrateCamera(String outputFile, int dict_id){
        VideoCapture inputVideo = new VideoCapture(0);
        int waitTime = 20;

        /*
        markersX - number of markers in X direction
        markersY - number of markers in Y direction
        markerLength - marker side length (normally in meters)
        markerSeparation - separation between two markers (same unit as markerLength)
        */

        int markersX = 5;
        int markersY = 4;
        float markerLength = 4.3f;
        float markerSeparation = 1.6f;

        Dictionary dictionary = Aruco.getPredefinedDictionary(dict_id);
        GridBoard gridboard = GridBoard.create(markersX, markersY, markerLength, markerSeparation, dictionary);
        Board board = gridboard;

        Vector<Vector<Mat>> allCorners = new Vector<Vector<Mat>>();
        Vector<Mat> allIds = new Vector<Mat>();
        Size imgSize = new Size();

        while(inputVideo.grab()){
            //inputVideo.grab();
            Mat image = new Mat();
            inputVideo.retrieve(image);

            Mat ids = new Mat();
            Vector<Mat> corners = new Vector<Mat>();
            Vector<Mat> rejected = new Vector<Mat>();
            // detect markers
            Aruco.detectMarkers(image, dictionary, corners, ids, params, rejected);
            //System.out.println(corners.get(0).type());
            //System.out.println(ids.type());

            // refind strategy to detect more markers
            Aruco.refineDetectedMarkers(image, board, corners, ids, rejected);

            Mat imageCopy = image;
            //System.out.println(corners.size());
            if(ids.size().area() > 0) Aruco.drawDetectedMarkers(imageCopy, corners);

            HighGui.imshow("out", imageCopy);
            char key = (char)HighGui.waitKey(waitTime);
            if(key == 'S') break;
            if(key == 'C') {
                System.out.println("Loaded frame");
                allCorners.add(corners);
                allIds.add(ids);
                imgSize = image.size();
            }
        }

        Mat cameraMatrix = new Mat();
        Mat distCoeffs = new Mat();
        Vector<Mat> rvecs = new Vector<Mat>();
        Vector<Mat> tvecs = new Vector<Mat>();
        double repError;

        /*
        if(calibrationFlags & CALIB_FIX_ASPECT_RATIO) {
            cameraMatrix = Mat::eye(3, 3, CV_64F);
            cameraMatrix.at< double >(0, 0) = aspectRatio;
        }
        */

        Vector<Mat> allCornersConcatenated = new Vector<Mat>();
        Mat allIdsConcatenated = new Mat(allCorners.size(), 1, 4);
        Mat markerCounterPerFrame = new Mat(allCorners.size(), 1, CvType.CV_32SC1);
        //markerCounterPerFrame.reserve(allCorners.size());
        
        for(int i = 0; i < allCorners.size(); i++) {
            markerCounterPerFrame.put(i, 0, allCorners.get(i).size());
            for(int j = 0; j < allCorners.get(i).size(); j++) {
                allCornersConcatenated.add(allCorners.get(i).get(j));
                //allIdsConcatenated.push_back(allIds.get(i));
                allIdsConcatenated.put(i, 0, allIds.get(i).get(j, 0));
            }
        }

        repError = Aruco.calibrateCameraAruco(allCornersConcatenated, allIdsConcatenated,
                                           markerCounterPerFrame, board, imgSize, cameraMatrix,
                                           distCoeffs, rvecs, tvecs);


        boolean saveOk = saveCameraParams(outputFile, imgSize, cameraMatrix,
                                   distCoeffs, repError);
    }

    public static void main(String[] args){
        CalibrateCamera cal = new CalibrateCamera("detector_params.json", "camera_params.json", 4);
    }
}