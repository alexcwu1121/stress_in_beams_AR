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

    public Mat run(DetectorResults results){
        Mat answer = results.baseImage();
        //Edit this section of code to change the conditions on which the simulation does not run, as well as declare variables holding marker information.

        CalibrationInformation ci = results.calibrationInformation();
        
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);

        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }
        //End section

        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());

        // Get 3D positions of each marker
        Mat p1 = first_pose.translationVector();
        Mat p2 = tracking_pose.translationVector();
        Mat p3 = second_pose.translationVector();

        // Calculate normal vector w cross product and compare to z vector of p3
        Mat v_p2p1 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(p1,p2,v_p2p1);
        Mat v_p2p3 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(p3,p2,v_p2p3);
        Mat u_p2p1 = MatMathUtils.unitVector(v_p2p1,MatMathUtils.norm(v_p2p1));
        Mat u_p2p3 = MatMathUtils.unitVector(v_p2p3,MatMathUtils.norm(v_p2p3));
        Mat norm = MatMathUtils.crossProduct(u_p2p1,u_p2p3);

        // Project vectors v_p2p1 and v_p2p3 onto norm
        Mat proj_p2p1 = MarkerUtils.dotMultiply(v_p2p1,norm);
        Mat proj_p2p3 = MarkerUtils.dotMultiply(v_p2p3,norm);

        // Subtract norm projected vectors to derive planar coordinates
        Mat planar_p2p1 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p1,proj_p2p1,planar_p2p1);
        Mat planar_p2p3 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p3,proj_p2p3,planar_p2p3);

        // Derive u and v coordinate axes of tracking marker
        Mat ex = new Mat(3, 1, CvType.CV_64FC1);ex.put(0, 0, 1);ex.put(1, 0, 0);ex.put(2, 0, 0);
        Mat ey = new Mat(3, 1, CvType.CV_64FC1);ey.put(0, 0, 0);ey.put(1, 0, 1);ey.put(2, 0, 0);
        Mat rot_t = new Mat();Calib3d.Rodrigues(tracking_pose.rotationVector(), rot_t);
        Mat u = MarkerUtils.matMultiply(rot_t, ex);
        Mat v = MarkerUtils.matMultiply(rot_t, ey);

        // Test draw u and v axes
        /*
        Mat u_axis = new Mat(3, 1, CvType.CV_64FC1);
        Mat v_axis = new Mat(3, 1, CvType.CV_64FC1);
        Core.add(MarkerUtils.scalarMultiply(u,3.0),p2,u_axis);
        Core.add(MarkerUtils.scalarMultiply(v,3.0),p2,v_axis);
       
        MatOfPoint3f u_axis_point = new MatOfPoint3f(new Point3(u_axis.get(0,0)[0],u_axis.get(1,0)[0],u_axis.get(2,0)[0]));
        MatOfPoint3f v_axis_point = new MatOfPoint3f(new Point3(v_axis.get(0,0)[0],v_axis.get(1,0)[0],v_axis.get(2,0)[0]));
        MatOfPoint3f tracking_point = new MatOfPoint3f(new Point3(p2.get(0,0)[0],p2.get(1,0)[0],p2.get(2,0)[0]));
       
        MatOfPoint2f u_projected = new MatOfPoint2f();
        MatOfPoint2f v_projected = new MatOfPoint2f();
        MatOfPoint2f tracking_projected = new MatOfPoint2f();
        MatOfDouble dMat = new MatOfDouble(0,0,0,0,0);
        Calib3d.projectPoints(u_axis_point, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, u_projected);
        Calib3d.projectPoints(v_axis_point, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, v_projected);
        Calib3d.projectPoints(tracking_point, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, tracking_projected);
        Imgproc.line(answer, new Point(u_projected.get(0,0)[0],u_projected.get(0,0)[1]), new Point(tracking_projected.get(0,0)[0],tracking_projected.get(0,0)[1]),new Scalar(100, 100, 0),5);
        Imgproc.line(answer, new Point(v_projected.get(0,0)[0],v_projected.get(0,0)[1]), new Point(tracking_projected.get(0,0)[0],tracking_projected.get(0,0)[1]),new Scalar(100, 100, 0),5);
        */

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
            Mat u_3d=MarkerUtils.scalarMultiply(u, u_mag);
            Mat v_3d=MarkerUtils.scalarMultiply(v, v_mag);

            Mat p_3d = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(u_3d,v_3d,p_3d);
            Core.add(p_3d,p2,p_3d);

            world_para.add(new Point3(p_3d.get(0,0)[0],p_3d.get(1,0)[0],p_3d.get(2,0)[0]));
        }
        // Convert line points to world points
        MatOfPoint3f world_points = new MatOfPoint3f(world_para.toArray(new Point3[world_para.size()]));

        // Temporary null distortional matrix for camera projection
        MatOfDouble dMat = new MatOfDouble(0,0,0,0,0);
        MatOfPoint2f para_projected = new MatOfPoint2f();
        Calib3d.projectPoints(world_points, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, para_projected);

        // Draw line components
        for(int i = 1; i < 20; i++){
            Imgproc.line(answer, new Point(para_projected.get(i,0)[0],para_projected.get(i,0)[1]),
                new Point(para_projected.get(i-1,0)[0],para_projected.get(i-1,0)[1]),
                new Scalar(0, 255, 0),5);
        }

        // Beam width
        double width = 6;

        // Draw v axis
        Mat v_top = new Mat(3, 1, CvType.CV_64FC1);
        Mat v_bot = new Mat(3, 1, CvType.CV_64FC1);
        Core.add(MarkerUtils.scalarMultiply(v,width/2),p2,v_top);
        Core.subtract(p2,MarkerUtils.scalarMultiply(v,width/2),v_bot);
        MatOfPoint3f v_top_point = new MatOfPoint3f(new Point3(v_top.get(0,0)[0],v_top.get(1,0)[0],v_top.get(2,0)[0]));
        MatOfPoint3f v_bot_point = new MatOfPoint3f(new Point3(v_bot.get(0,0)[0],v_bot.get(1,0)[0],v_bot.get(2,0)[0]));
        MatOfPoint2f v_top_projected = new MatOfPoint2f();
        MatOfPoint2f v_bot_projected = new MatOfPoint2f();

        Calib3d.projectPoints(v_top_point, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, v_top_projected);
        Calib3d.projectPoints(v_bot_point, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, v_bot_projected);

        Imgproc.line(answer, new Point(v_top_projected.get(0,0)[0],v_top_projected.get(0,0)[1]),
                new Point(v_bot_projected.get(0,0)[0],v_bot_projected.get(0,0)[1]),
                new Scalar(0, 165, 255),5);

        // Determine compressive/tensile side
        // Project n number of arrowed anchored onto the v axis, linearly increasing or decreasing in length.
        // Also calculate axial stress and move origin accordingly. Needs reference length
        // Compressive are red and inward facing.
        // Tensile are blue and outward facing.
        double precision = 10;
        double y_inc = width/precision;
        double scale = 3;
        List<Point3> line_def = new ArrayList<Point3>();
        List<Boolean> line_type = new ArrayList<Boolean>();
        for(int i = 0; i < precision; i++){
            double y = (i+1)*y_inc - width/2;
            Mat axis_ref = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(MarkerUtils.scalarMultiply(v,y),p2,axis_ref);

            // Angular (z) comparison
            double p1_yaw = first_pose.rotationVector().get(1,0)[0];
            double p3_yaw = second_pose.rotationVector().get(1,0)[0];
            double ang_defl = p1_yaw-p3_yaw;

            // Angular (y) comparison
            //double p1_yaw = first_pose.rotationVector().get(2,0)[0];
            //double p3_yaw = second_pose.rotationVector().get(2,0)[0];
            //double ang_defl = p1_yaw-p3_yaw;

            if(Math.abs(ang_defl) < .1){
                ang_defl = 0;
            }

            // Length of longest vector
            double vec_scale = scale*Math.abs(ang_defl)*Math.abs(y)/3;
            // End of vector
            Mat vec_end1 = new Mat(3, 1, CvType.CV_64FC1);
            Mat vec_end2 = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(MarkerUtils.scalarMultiply(u,vec_scale),axis_ref,vec_end1);
            Core.subtract(axis_ref,MarkerUtils.scalarMultiply(u,vec_scale),vec_end2);

            line_def.add(new Point3(axis_ref.get(0,0)[0],axis_ref.get(1,0)[0],axis_ref.get(2,0)[0]));
            line_def.add(new Point3(vec_end1.get(0,0)[0],vec_end1.get(1,0)[0],vec_end1.get(2,0)[0]));
            line_def.add(new Point3(vec_end2.get(0,0)[0],vec_end2.get(1,0)[0],vec_end2.get(2,0)[0]));

            // Determine direction and color of vector based on angular comparison and positioning along v
            if(i>precision/2 && ang_defl<0 || i<precision/2 && ang_defl>0){
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
            new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, line_def_projected);
    
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
        /*
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_tracking.first(), p_tracking.second(), 1F);
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_first.first(), p_first.second(), 1F);
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_second.first(), p_second.second(), 1F);        
        */

        // Return final image
        return answer;
    }
}