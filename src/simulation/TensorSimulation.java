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

    private double tensorXPos;
    private double tensorYPos;
    private double tensorZPos;
    private double tensorAngle;

    public TensorSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup,
        @Description("Tensor X Position") double tensorXPos,
        @Description("Tensor Y Position") double tensorYPos,
        @Description("Tensor Z Position") double tensorZPos,
        @Description("Tensor Angle") double tensorAngle){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;

        this.tensorXPos = tensorXPos;
        this.tensorYPos = tensorYPos;
        this.tensorZPos = tensorZPos;
        this.tensorAngle = tensorAngle;
    }

    public Mat drawStress(Mat answer, Mat center, Mat offset, Mat u, double magnitude, CalibrationInformation ci, MatOfDouble dMat, Boolean shear){
        Mat anchor = new Mat();Core.add(center,offset,anchor);
        Mat end = new Mat();Core.add(anchor,MarkerUtils.scalarMultiply(u,Math.abs(magnitude)),end);
        List<Point3> endpoints = new ArrayList<Point3>();

        endpoints.add(new Point3(anchor.get(0,0)[0],anchor.get(1,0)[0],anchor.get(2,0)[0]));
        endpoints.add(new Point3(end.get(0,0)[0],end.get(1,0)[0],end.get(2,0)[0]));

        MatOfPoint2f axial_projected = new MatOfPoint2f();
        Calib3d.projectPoints(new MatOfPoint3f(endpoints.toArray(new Point3[endpoints.size()])),
            Mat.zeros(3, 1, CvType.CV_64FC1),
            Mat.zeros(3, 1, CvType.CV_64FC1),
            ci.cameraMatrix(), dMat, axial_projected);

        if(magnitude < 0){
            Scalar color = new Scalar(255,0,0);
            if(shear){
                color = new Scalar(0,0,255);
            }
            Imgproc.arrowedLine(answer, new Point(axial_projected.get(0,0)[0],axial_projected.get(0,0)[1]),
                        new Point(axial_projected.get(1,0)[0],axial_projected.get(1,0)[1]),
                        color,2,0,0,.25);
        }else{
            Scalar color = new Scalar(0,0,255);
            if(shear){
                color = new Scalar(255,0,0);
            }
            Imgproc.arrowedLine(answer, new Point(axial_projected.get(1,0)[0],axial_projected.get(1,0)[1]),
                        new Point(axial_projected.get(0,0)[0],axial_projected.get(0,0)[1]),
                        color,2,0,0,.25);
        }
        return answer;
    }

    public Mat run(DetectorResults results){
        // Initialize final image buffer
        Mat answer = results.baseImage();

        // Relative translation and z rotation of stress tensor to tracking mmb
        Mat rel_pos = new Mat(3, 1, CvType.CV_64FC1);
        rel_pos.put(0, 0, this.tensorXPos);rel_pos.put(1, 0, this.tensorYPos);rel_pos.put(2, 0, this.tensorZPos);
        //double z_rot = 3.1415/4;
        double z_rot = this.tensorAngle;
        Mat zRotm = MatMathUtils.zRot(z_rot);

        double side_length = 2;

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
        Mat u = MarkerUtils.matMultiply(rot_t, MarkerUtils.matMultiply(zRotm,ex));
        Mat v = MarkerUtils.matMultiply(rot_t, MarkerUtils.matMultiply(zRotm,ey));

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
        double scale = 6;
        Mat sigma = Mat.zeros(2, 2, CvType.CV_64FC1);
        double sigma_princ = scale/3*(-1*ang_defl_roll*rel_pos.get(1,0)[0] + ang_defl_yaw);
        sigma.put(0, 0, sigma_princ);

        Mat rot_plane = Mat.zeros(2, 2, CvType.CV_64FC1);
        rot_plane.put(0, 0, Math.cos(z_rot));
        rot_plane.put(0, 1, -1*Math.sin(z_rot));
        rot_plane.put(1, 0, Math.sin(z_rot));
        rot_plane.put(1, 1, Math.cos(z_rot));
        Mat rot_plane_t = new Mat();Core.transpose(rot_plane,rot_plane_t);
        // Final stress tensor
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
        Core.addWeighted(answer,1,tensor_image,0.7,1,answer);

        Mat shear_offset1 = new Mat();Core.add(MarkerUtils.scalarMultiply(u_off,1.1),v_off,shear_offset1);
        Mat shear_offset2 = new Mat();Core.add(u_off,MarkerUtils.scalarMultiply(v_off,1.1),shear_offset2);
        Mat shear_offset5 = new Mat();Core.add(MarkerUtils.scalarMultiply(u_off,-1.1),MarkerUtils.scalarMultiply(v_off,-1.0),shear_offset5);
        Mat shear_offset6 = new Mat();Core.add(MarkerUtils.scalarMultiply(u_off,-1.0),MarkerUtils.scalarMultiply(v_off,-1.1),shear_offset6);

        // Draw stress vectors onto tensor. Generally will only be eight.
        // Mat answer, Mat center, Mat offset, double magnitude, CameraInformation ci, Mat dMat
        answer = drawStress(answer, center, u_off, u, sigma_prime.get(0,0)[0],ci,dMat,false);
        answer = drawStress(answer, center, MarkerUtils.scalarMultiply(u_off,-1), MarkerUtils.scalarMultiply(u,-1), sigma_prime.get(0,0)[0],ci,dMat,false);
        answer = drawStress(answer, center, v_off, v, sigma_prime.get(1,1)[0],ci,dMat,false);
        answer = drawStress(answer, center, MarkerUtils.scalarMultiply(v_off,-1), MarkerUtils.scalarMultiply(v,-1), sigma_prime.get(1,1)[0],ci,dMat,false);

        answer = drawStress(answer, center, shear_offset2, MarkerUtils.scalarMultiply(u,-1), -1*sigma_prime.get(0,1)[0],ci,dMat,true);
        answer = drawStress(answer, center, shear_offset1, MarkerUtils.scalarMultiply(v,-1), -1*sigma_prime.get(0,1)[0],ci,dMat,true);
        answer = drawStress(answer, center, shear_offset5, MarkerUtils.scalarMultiply(v,1), -1*sigma_prime.get(0,1)[0],ci,dMat,true);
        answer = drawStress(answer, center, shear_offset6, MarkerUtils.scalarMultiply(u,1), -1*sigma_prime.get(0,1)[0],ci,dMat,true);

        return answer;
    }
}