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

        System.out.println(">>>>>>");
        MarkerUtils.printmat(norm);
        System.out.println(">>>>>>");

        // Project vectors v_p2p1 and v_p2p3 onto norm
        Mat proj_p2p1 = MarkerUtils.dotMultiply(v_p2p1,norm);
        Mat proj_p2p3 = MarkerUtils.dotMultiply(v_p2p3,norm);

        // Subtract norm projected vectors to derive planar coordinates
        Mat planar_p2p1 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p1,proj_p2p1,planar_p2p1);
        Mat planar_p2p3 = new Mat(3, 1, CvType.CV_64FC1);Core.subtract(v_p2p3,proj_p2p3,planar_p2p3);

        // Derive u and v coordinate axes of tracking marker
        Mat ex = new Mat(3, 1, CvType.CV_64FC1);ex.put(0, 0, 1);
        Mat ey = new Mat(3, 1, CvType.CV_64FC1);ey.put(1, 0, 1);
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
            Double v_mag=para_c*Math.pow(u_mag,2);
            // Multiply by unit vectors u and v
            Mat u_3d=MarkerUtils.scalarMultiply(u, u_mag);
            Mat v_3d=MarkerUtils.scalarMultiply(v, v_mag);

            Mat p_3d = new Mat(3, 1, CvType.CV_64FC1);
            Core.add(u_3d,v_3d,p_3d);
            Core.add(p_3d,p2,p_3d);
            world_para.add(new Point3(p_3d.get(0,0)[0],p_3d.get(1,0)[0],p_3d.get(2,0)[0]));
        }
        MatOfPoint3f world_points = new MatOfPoint3f(world_para.toArray(new Point3[world_para.size()]));

        // Temporary null distortional matrix
        MatOfDouble dMat = new MatOfDouble(0,0,0,0,0);
        MatOfPoint2f para_projected = new MatOfPoint2f();
        Calib3d.projectPoints(world_points, new Mat(3, 1, CvType.CV_64FC1), new Mat(3, 1, CvType.CV_64FC1), ci.cameraMatrix(), dMat, para_projected);

        for(int i = 1; i < 20; i++){
            Imgproc.line(answer, new Point(para_projected.get(i,0)[0],para_projected.get(i,0)[1]),
                new Point(para_projected.get(i-1,0)[0],para_projected.get(i-1,0)[1]),
                new Scalar(0, 255, 0),5);
        }

        // Return final image
        return answer;
    }
}