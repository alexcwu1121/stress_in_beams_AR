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


@HumanReadableName("Mask Simulation")
public class MaskSimulation implements Simulation {
    private final MultiMarkerBody trackingGroup;
    //Replace these later with multi-marker bodies
    private final MultiMarkerBody firstGroup;
    private final MultiMarkerBody secondGroup;

    public MaskSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;
    }

    public Mat drawVaxis(Mat answer, Mat p2, double width, Mat v, CalibrationInformation ci, MatOfDouble dMat, Mat zeros){
        // Draw v axis
        Mat v_top = new Mat(3, 1, CvType.CV_64FC1);
        Mat v_bot = new Mat(3, 1, CvType.CV_64FC1);
        Core.add(MatMathUtils.scalarMultiply(v,width/2),p2,v_top);
        Core.subtract(p2,MatMathUtils.scalarMultiply(v,width/2),v_bot);
        MatOfPoint3f v_top_point = new MatOfPoint3f(new Point3(v_top.get(0,0)[0],v_top.get(1,0)[0],v_top.get(2,0)[0]));
        MatOfPoint3f v_bot_point = new MatOfPoint3f(new Point3(v_bot.get(0,0)[0],v_bot.get(1,0)[0],v_bot.get(2,0)[0]));
        MatOfPoint2f v_top_projected = new MatOfPoint2f();
        MatOfPoint2f v_bot_projected = new MatOfPoint2f();

        Calib3d.projectPoints(v_top_point, zeros, zeros, ci.cameraMatrix(), dMat, v_top_projected);
        Calib3d.projectPoints(v_bot_point, zeros, zeros, ci.cameraMatrix(), dMat, v_bot_projected);

        Imgproc.line(answer, new Point(v_top_projected.get(0,0)[0],v_top_projected.get(0,0)[1]),
                new Point(v_bot_projected.get(0,0)[0],v_bot_projected.get(0,0)[1]),
                new Scalar(0, 165, 255),5);

        return answer;
    }

    public Mat drawNeutralAxis(Mat answer, Mat p1, Mat p2, Mat p3, double width, Mat u, Mat v, CalibrationInformation ci, MatOfDouble dMat, Mat zeros, double offset){
        // Calculate normal vector w cross product and compare to z vector of p3
        Mat v_p2p1 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(p1,p2,v_p2p1);
        Mat v_p2p3 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(p3,p2,v_p2p3);
        Mat u_p2p1 = MatMathUtils.unitVector(v_p2p1,MatMathUtils.norm(v_p2p1));
        Mat u_p2p3 = MatMathUtils.unitVector(v_p2p3,MatMathUtils.norm(v_p2p3));
        Mat norm = MatMathUtils.crossProduct(u_p2p1,u_p2p3);

        // Find magnitude of components of vectors p2p1 and p2p3 on u and v axes w/ dot product
        // Generate a parabolic curve to planar points (p2p1_u,p2p1_v), (0,0), (p2p3_u,p2p3_v)
        // For simple computation, assume symmetrical parabola and average sides
        Double p2p1_u = MatMathUtils.dotProduct(v_p2p1,u);
        Double p2p1_v = MatMathUtils.dotProduct(v_p2p1,v);
        Double p2p3_u = MatMathUtils.dotProduct(v_p2p3,u);
        Double p2p3_v = MatMathUtils.dotProduct(v_p2p3,v);
        Double para_c=(p2p1_v/Math.pow(p2p1_u,2)+p2p3_v/(Math.pow(p2p3_u,2)))/2;

        // Interpolate simple quadratic for twenty points
        List<Point3> world_para = new ArrayList<Point3>();
        Double inc=Math.abs((p2p3_u-p2p1_u))/20;

        for(int i = -10; i < 20; i++){
            // Calculate u mag and v mag for each interpolated x point
            Double u_mag=i*inc;
            Double v_mag=para_c*Math.pow(u_mag,2.0);
            
            // Multiply by unit vectors u and v
            Mat u_3d=MatMathUtils.scalarMultiply(u, u_mag);
            Mat v_3d=MatMathUtils.scalarMultiply(v, v_mag);

            Mat p_3d = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(u_3d,v_3d,p_3d);
            Core.add(p_3d,p2,p_3d);
            Core.add(p_3d,MatMathUtils.scalarMultiply(v,offset),p_3d);

            world_para.add(new Point3(p_3d.get(0,0)[0],p_3d.get(1,0)[0],p_3d.get(2,0)[0]));
        }
        // Convert line points to world points
        MatOfPoint3f world_points = new MatOfPoint3f(world_para.toArray(new Point3[world_para.size()]));

        // Temporary null distortional matrix for camera projection
        MatOfPoint2f para_projected = new MatOfPoint2f();
        Calib3d.projectPoints(world_points, zeros, zeros, ci.cameraMatrix(), dMat, para_projected);

        // Draw line components
        for(int i = 1; i < 20; i++){
            Imgproc.line(answer, new Point(para_projected.get(i,0)[0],para_projected.get(i,0)[1]),
                new Point(para_projected.get(i-1,0)[0],para_projected.get(i-1,0)[1]),
                new Scalar(0, 255, 0),5);
        }
        return answer;
    }

    public Mat run(DetectorResults results){
        Mat answer = results.baseImage();
        
        Mat zeros = new Mat(3, 1, CvType.CV_64FC1);zeros.put(0, 0, 0);zeros.put(1, 0, 0);zeros.put(2, 0, 0);

        // Beam width
        double width = 6;

        CalibrationInformation ci = results.calibrationInformation();
        MatOfDouble dMat = new MatOfDouble(ci.distCoeffs());
        
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);

        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }

        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());

        // Get 3D positions of each marker
        Mat p1 = first_pose.translationVector();
        Mat p2 = tracking_pose.translationVector();
        Mat p3 = second_pose.translationVector();

        /*
        Experimental: planar projection for finer 3D curve fitting

        // Project vectors v_p2p1 and v_p2p3 onto norm
        Mat proj_p2p1 = MatMathUtils.dotMultiply(v_p2p1,norm);
        Mat proj_p2p3 = MatMathUtils.dotMultiply(v_p2p3,norm);

        // Subtract norm projected vectors to derive planar coordinates
        Mat planar_p2p1 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p1,proj_p2p1,planar_p2p1);
        Mat planar_p2p3 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p3,proj_p2p3,planar_p2p3);
        */

        // Derive u and v coordinate axes of tracking marker
        Mat ex = new Mat(3, 1, CvType.CV_64FC1);ex.put(0, 0, 1);ex.put(1, 0, 0);ex.put(2, 0, 0);
        Mat ey = new Mat(3, 1, CvType.CV_64FC1);ey.put(0, 0, 0);ey.put(1, 0, 1);ey.put(2, 0, 0);
        Mat ez = new Mat(3, 1, CvType.CV_64FC1);ez.put(0, 0, 0);ez.put(1, 0, 0);ez.put(2, 0, 1);
        Mat rot_t = new Mat();Calib3d.Rodrigues(tracking_pose.rotationVector(), rot_t);
        Mat u = MatMathUtils.matMultiply(rot_t, ex);
        Mat v = MatMathUtils.matMultiply(rot_t, ey);
        Mat w = MatMathUtils.matMultiply(rot_t, ez);

        // Draw v axis
        answer = drawVaxis(answer,p2,width,v,ci,dMat,zeros);

        // Determine compressive/tensile side
        // Project n number of arrowedlines anchored onto the v axis, linearly increasing or decreasing in length.
        // Also calculate axial stress and move origin accordingly. Needs reference length
        // Compressive are red and inward facing.
        // Tensile are blue and outward facing.
        double precision = 16;
        double y_inc = width/precision;
        double scale = 3;
        List<Point3> line_def = new ArrayList<Point3>();
        List<Boolean> line_type = new ArrayList<Boolean>();

        /* WRONG - Cannot subtract euler angles. Keeping here for reference
        Either perform vector projection math or Rodriguez the rot vecs, find
        transformation rot between them, and convert back into euler angles
        // Angular (z/roll) comparison
        double p1_roll = first_pose.rotationVector().get(1,0)[0];
        double p3_roll = second_pose.rotationVector().get(1,0)[0];
        double ang_defl_roll = p1_roll-p3_roll;

        // Angular (y/yaw) comparison
        double p1_yaw = first_pose.rotationVector().get(2,0)[0];
        double p3_yaw = second_pose.rotationVector().get(2,0)[0];
        double ang_defl_yaw = p1_yaw-p3_yaw;
        */

        Mat rot_p1 = new Mat();Calib3d.Rodrigues(first_pose.rotationVector(), rot_p1);
        Mat rot_p2 = new Mat();Calib3d.Rodrigues(second_pose.rotationVector(), rot_p2);
        Mat t_rot_p1 = new Mat();Core.transpose(rot_p1, t_rot_p1);

        // Find transformation rotation mat
        Mat rot_trans = MatMathUtils.matMultiply(rot_p2,t_rot_p1);
        // Axis angle
        Mat axis_angle = MatMathUtils.toAxisAngle(rot_trans);

        // Dot product axis to ey and ez, then multiply by angle to determing roll and yaw deflection
        Mat axis = new Mat(3, 1, CvType.CV_64FC1);
        axis.put(0,0,axis_angle.get(1,0)[0]);
        axis.put(1,0,axis_angle.get(2,0)[0]);
        axis.put(2,0,axis_angle.get(3,0)[0]);

        // Find rotation components along z and y axes using dot product projection
        double ang_defl_roll = -1*MatMathUtils.dotProduct(axis,w)*axis_angle.get(0,0)[0];
        double ang_defl_yaw = -1*MatMathUtils.dotProduct(axis,v)*axis_angle.get(0,0)[0];

        /* Also incorrect. Still can't use euler angles as direct differences
        Mat euler_trans = new Mat();Calib3d.Rodrigues(rot_trans, euler_trans); 
        double ang_defl_roll = euler_trans.get(2,0)[0];
        double ang_defl_yaw = euler_trans.get(1,0)[0];
        */

        if(Math.abs(ang_defl_roll) < .2){
            ang_defl_roll = 0;}
        if(Math.abs(ang_defl_yaw) < .2){
            ang_defl_yaw = 0;}

        // Draw neutral axis
        if(Math.abs(ang_defl_roll) > 0){
            answer = drawNeutralAxis(answer,p1,p2,p3,width,u,v,ci,dMat,zeros,y_inc*ang_defl_yaw/ang_defl_roll);
        }else{
            answer = drawNeutralAxis(answer,p1,p2,p3,width,u,v,ci,dMat,zeros,0);
        }

        for(int i = 0; i < precision; i++){
            double y = (i+1)*y_inc - width/2;

            // Anchor point on v axis
            Mat axis_ref = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(MatMathUtils.scalarMultiply(v,y),p2,axis_ref);

            // vector length
            double vec_scale = scale/3*(-1*ang_defl_roll*y + ang_defl_yaw);
            //double vec_scale = scale/3*(ang_defl_roll*y);

            // End of vector
            Mat vec_end1 = new Mat(3, 1, CvType.CV_64FC1);
            Mat vec_end2 = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(MatMathUtils.scalarMultiply(u,vec_scale),axis_ref,vec_end1);
            Core.subtract(axis_ref,MatMathUtils.scalarMultiply(u,vec_scale),vec_end2);

            line_def.add(new Point3(axis_ref.get(0,0)[0],axis_ref.get(1,0)[0],axis_ref.get(2,0)[0]));
            line_def.add(new Point3(vec_end1.get(0,0)[0],vec_end1.get(1,0)[0],vec_end1.get(2,0)[0]));
            line_def.add(new Point3(vec_end2.get(0,0)[0],vec_end2.get(1,0)[0],vec_end2.get(2,0)[0]));

            // Determine direction and color of vector based on angular comparison and positioning along v
            //if(i>precision/2 && ang_defl<0 || i<precision/2 && ang_defl>0){
            if(vec_scale < 0){
                // Compressive
                line_type.add(false);
            }else{
                // Tensile
                line_type.add(true);
            }
        }

        // Project
        MatOfPoint2f line_def_projected = new MatOfPoint2f();
        Calib3d.projectPoints(new MatOfPoint3f(line_def.toArray(new Point3[line_def.size()])),
            zeros, zeros, ci.cameraMatrix(), dMat, line_def_projected);
    
        for(int i = 1; i < precision; i++){
            if(line_type.get(i) == true){
                // Compressive
                Imgproc.arrowedLine(answer, new Point(line_def_projected.get(3*i-1,0)[0],line_def_projected.get(3*i-1,0)[1]),
                    new Point(line_def_projected.get(3*i-3,0)[0],line_def_projected.get(3*i-3,0)[1]),
                    new Scalar(0, 0, 255),3);
                Imgproc.arrowedLine(answer, new Point(line_def_projected.get(3*i-2,0)[0],line_def_projected.get(3*i-2,0)[1]),
                    new Point(line_def_projected.get(3*i-3,0)[0],line_def_projected.get(3*i-3,0)[1]),
                    new Scalar(0, 0, 255),3);
            }else{
                // Tensile
                Imgproc.arrowedLine(answer, new Point(line_def_projected.get(3*i-3,0)[0],line_def_projected.get(3*i-3,0)[1]),
                    new Point(line_def_projected.get(3*i-1,0)[0],line_def_projected.get(3*i-1,0)[1]),
                    new Scalar(255, 0, 0),3);
                Imgproc.arrowedLine(answer, new Point(line_def_projected.get(3*i-3,0)[0],line_def_projected.get(3*i-3,0)[1]),
                    new Point(line_def_projected.get(3*i-2,0)[0],line_def_projected.get(3*i-2,0)[1]),
                    new Scalar(255, 0, 0),3);
            }
        }

        // Debug, draw coordinate axes on markers
        //Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_tracking.first(), p_tracking.second(), 1F);
        //Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_first.first(), p_first.second(), 1F);
        //Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_second.first(), p_second.second(), 1F);

        // Return final image
        return answer;
    }
}