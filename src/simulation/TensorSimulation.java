package simulation;

import markerdetector.*;
import crosssection.*;

import org.opencv.core.*;
import org.opencv.aruco.*;
import org.opencv.calib3d.Calib3d;
import org.opencv.imgproc.Imgproc;
import util.HumanReadableName;
import java.util.*;
import util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.*;


@HumanReadableName("Tensor Simulation")
public class TensorSimulation implements Simulation {
    private final MultiMarkerBody trackingGroup;
    //Replace these later with multi-marker bodies
    private final MultiMarkerBody firstGroup;
    private final MultiMarkerBody secondGroup;

    public TensorSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;
    }

    public Mat run(DetectorResults results){
        // Initialize final image buffer
        Mat answer = results.baseImage();

        // Relative translation and z rotation of stress tensor to tracking mmb
        Mat rel_pos = new Mat(3, 1, CvType.CV_64FC1);
        rel_pos.put(0, 0, 0);rel_pos.put(1, 0, 0);rel_pos.put(2, 0, 0);
        double z_rot = 0;

        double side_length = 1;

        // Camera matrix and distortional coefficients
        CalibrationInformation ci = results.calibrationInformation();
        MatOfDouble dMat = new MatOfDouble(ci.distCoeffs());

        // Predict center of mmbs and derive poses
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);
        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }
        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // Get 3D positions of each marker
        Mat p1 = first_pose.translationVector();
        Mat p2 = tracking_pose.translationVector();
        Mat p3 = second_pose.translationVector();

        // Derive u and v coordinate axes of tracking marker
        Mat ex = Mat.zeros(3, 1, CvType.CV_64FC1);ex.put(0, 0, 1);
        Mat ey = Mat.zeros(3, 1, CvType.CV_64FC1);ey.put(1, 0, 1);
        Mat rot_t = new Mat();Calib3d.Rodrigues(tracking_pose.rotationVector(), rot_t);
        Mat u = MarkerUtils.matMultiply(rot_t, ex);
        Mat v = MarkerUtils.matMultiply(rot_t, ey);

        // Euler rotation difference from coordinate frame of mmb 1 to mmb 3
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Mat rot_p1 = new Mat();Calib3d.Rodrigues(first_pose.rotationVector(), rot_p1);
        Mat rot_p2 = new Mat();Calib3d.Rodrigues(second_pose.rotationVector(), rot_p2);
        Mat t_rot_p1 = new Mat();Core.transpose(rot_p1, t_rot_p1);

        // Find transformation rotation mat
        Mat rot_trans = MarkerUtils.matMultiply(rot_p2,t_rot_p1);
        Mat euler_trans = new Mat();Calib3d.Rodrigues(rot_trans, euler_trans);
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        // Assemble 2D tensor matrix under uniaxial load
        double ang_defl_roll = euler_trans.get(2,0)[0];
        double ang_defl_yaw = euler_trans.get(1,0)[0];
        if(Math.abs(ang_defl_roll) < .2){
            ang_defl_roll = 0;}
        if(Math.abs(ang_defl_yaw) < .2){
            ang_defl_yaw = 0;}

        // Compute stress state at current planar position
        double scale = 3;
        Mat sigma = Mat.zeros(2, 2, CvType.CV_64FC1);
        double sigma_princ = scale/3*(-1*ang_defl_roll*rel_pos.get(1,0)[0] + ang_defl_yaw);
        sigma.put(0, 0, sigma_princ);

        Mat rot_plane = Mat.zeros(2, 2, CvType.CV_64FC1);
        rot_plane.put(0, 0, Math.cos(z_rot));
        rot_plane.put(0, 1, -1*Math.sin(z_rot));
        rot_plane.put(1, 0, Math.cos(z_rot));
        rot_plane.put(1, 1, Math.sin(z_rot));
        Mat rot_plane_t = new Mat();Core.transpose(rot_plane,rot_plane_t);

        Mat sigma_prime = MarkerUtils.matMultiply(MarkerUtils.matMultiply(rot_plane,sigma),rot_plane_t);

        // Corner offsets
        Mat u_off = MarkerUtils.scalarMultiply(u,side_length/2);
        Mat v_off = MarkerUtils.scalarMultiply(v,side_length/2);

        // Project tensor world coordinates to camera frame
        Mat center = new Mat();Core.add(rel_pos,p2,center);
        List<Point3> corners = new ArrayList<Point3>();
        Mat c1 = new Mat();Core.add(center,u_off,c1);Core.add(c1,v_off,c1);
        corners.add(new Point3(c1.get(0,0)[0],c1.get(1,0)[0],c1.get(2,0)[0]));

        Mat c2 = new Mat();Core.subtract(center,u_off,c2);Core.add(c2,v_off,c2);
        corners.add(new Point3(c2.get(0,0)[0],c2.get(1,0)[0],c2.get(2,0)[0]));

        Mat c3 = new Mat();Core.subtract(center,u_off,c3);Core.subtract(c3,v_off,c3);
        corners.add(new Point3(c3.get(0,0)[0],c3.get(1,0)[0],c3.get(2,0)[0]));

        Mat c4 = new Mat();Core.add(center,u_off,c4);Core.subtract(c4,v_off,c4);
        corners.add(new Point3(c4.get(0,0)[0],c4.get(1,0)[0],c4.get(2,0)[0]));

        MatOfPoint2f tensor_projected = new MatOfPoint2f();
        Calib3d.projectPoints(new MatOfPoint3f(corners.toArray(new Point3[corners.size()])),
            Mat.zeros(3, 1, CvType.CV_64FC1),
            Mat.zeros(3, 1, CvType.CV_64FC1),
            ci.cameraMatrix(), dMat, tensor_projected);
        MatOfPoint tensor_projected_mat = new MatOfPoint(tensor_projected.toArray());
        List<MatOfPoint> tensor_projection_list = new ArrayList<MatOfPoint>();
        tensor_projection_list.add(tensor_projected_mat);

        Mat tensor_image = Mat.zeros(answer.size(),CvType.CV_8UC3);
        Imgproc.fillPoly(tensor_image,tensor_projection_list,new Scalar(0, 255, 0));
        Core.addWeighted(answer,1,tensor_image,0.5,1,answer);

        return answer;
    }
}