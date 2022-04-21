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

/**Simulation which draws a crossection on the screen.<br>
The crosssection will follow the marker being tracked. In doing so, it will rotate and scale itself to match the marker.<br>
Additionally, lines are drawn from the crosssection to the marker as a visual aid.
@author Owen Kulik
*/

@HumanReadableName("Cross-Section Simulation")
public class CrossSimulation implements Simulation {
    private final Plane cross;

    private final MultiMarkerBody trackingGroup;
    //Replace these later with multi-marker bodies
    private final MultiMarkerBody firstGroup;
    private final MultiMarkerBody secondGroup;

    /**Constructs a CrossSimulation using the given values.
    @param idToTrack the marker id to base data off of.
    @throws IllegalArgumentException if idToTrack is negative.
    */
    public CrossSimulation(@Description("Multi-Marker Body") MultiMarkerBody firstGroup,
        @Description("Multi-Marker Body") MultiMarkerBody middleGroup,
        @Description("Multi-Marker Body") MultiMarkerBody lastGroup){

        this.firstGroup = firstGroup;
        this.trackingGroup = middleGroup;
        this.secondGroup = lastGroup;

        //Hardcoded values, feel free to change
        cross = new Plane(1.0, .2, 1.5);
    }

    /**Runs the simulation.
    @param results The detector results object.
    @return The result mat.
    */
    public Mat run(DetectorResults results){
        //Edit this section of code to change the conditions on which the simulation does not run, as well as declare variables holding marker information.
        // This is here just in case / useless
        //MarkerInformation information = results.getMarkerInformation(10);
        
        Pair<Mat, Mat> p_tracking = this.trackingGroup.predictCenter(results);
        Pair<Mat, Mat> p_first = this.firstGroup.predictCenter(results);
        Pair<Mat, Mat> p_second = this.secondGroup.predictCenter(results);

        if(p_tracking == null || p_first == null || p_second == null){
            return results.baseImage();
        }
        //End section

        //first pose is on the left when looking at the simulation, second on the right
        Pose tracking_pose = new Pose(p_tracking.first(), p_tracking.second());
        Pose first_pose = new Pose(p_first.first(), p_first.second());
        Pose second_pose = new Pose(p_second.first(), p_second.second());

        //Edit this section of code to change the values put into the crossection.


        Mat answer = results.baseImage();
        // cross.planeUpdate determines the colors in the crossection
        //double rotM[] = tracking_pose.flipCoords().rotationVector();
        //String text = String(Math.toDegrees(rotM[0]));
        String text = "Rotation";
        Point position = new Point(0, 25);
        Scalar color = new Scalar(0, 0, 0);
        int font = Imgproc.FONT_HERSHEY_SIMPLEX;
        int scale = 1;
        int thickness = 3;
        //Adding text to the image
        Imgproc.putText(answer, text, position, font, scale, color, thickness);


        cross.planeUpdate(first_pose.flipCoords().rotationVector(), second_pose.flipCoords().rotationVector(),tracking_pose.flipCoords().rotationVector());
        //End section

        BufferedImage bi = cross.getImage();
        Mat mat = /*results.baseImage();*/new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        //Edit this section of code to change where the crossection is drawn on the screen.
        //Variables declared before the code block must be filled in by the end of the section.
        //x and y are the base coordinates where the crossection is drawn.
        int x = 100; 
        int y = 100;
        //originalX and originalY are where the lines are drawn to.
        int originalX = 50;
        int originalY = 50;
        //angle of the marker relative to horizontal.
        double angle = 0;
        //Width and height each represent the lengths of one side of the marker.
        int width = 50;
        int height = 50;
        //When width and height are both equal to expectedSideLengths, the crosssection is drawn at full size. 
        //Increasing this variable decreases the size of the drawn crossection.
        int expectedSideLengths = 100;
        //Proportional to the amount that the drawn crosssection is offset from the marker.
        int offset = 0;
        /*
        {
            Mat corners = results.getMarkerInformation(10).corners();
            int changeInX = (int)(corners.get(0, 1)[1] - corners.get(0, 0)[1] + corners.get(0, 2)[1] - corners.get(0, 3)[1])/2;
            int changeInY = (int)(corners.get(0, 1)[0] - corners.get(0, 0)[0] + corners.get(0, 2)[0] - corners.get(0, 3)[0])/2;
            angle = Math.atan2(changeInY, changeInX);
            height = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 3)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 3)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 1)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 1)[0], 2)))/2;
            width = (int)Math.round(Math.sqrt(Math.pow(corners.get(0, 1)[1] - corners.get(0, 0)[1], 2) + Math.pow(corners.get(0, 1)[0] - corners.get(0, 0)[0], 2)) + Math.sqrt(Math.pow(corners.get(0, 2)[1] - corners.get(0, 3)[1], 2) + Math.pow(corners.get(0, 2)[0] - corners.get(0, 3)[0], 2)))/2;
            originalX = ((int)corners.get(0, 0)[1] + (int)corners.get(0, 1)[1])/2;
            originalY = ((int)corners.get(0, 0)[0] + (int)corners.get(0, 1)[0])/2;
            x = originalX - (int)Math.round(offset*((double)width/expectedSideLengths)*Math.sin(angle));
            y = originalY + (int)Math.round(offset*((double)height/expectedSideLengths)*Math.cos(angle));
        }
        */
        //End section

        //This section appends the crosssection image to the frame
        //Mat answer = results.baseImage();
        for(int i = 0; i < mat.rows(); i++){
            for(int j = 0; j < mat.cols(); j++){
                int jmod = j - mat.rows()/2;
                int imod = i;
                double actualX = jmod * ((double)width/expectedSideLengths);
                double actualY = imod * ((double)height/expectedSideLengths);
                double theta = Math.atan2(actualY, actualX) + angle;
                double r = Math.sqrt(Math.pow(actualX, 2) + Math.pow(actualY, 2));
                int currentX = x + (int)Math.round(r*Math.cos(theta));
                int currentY = y + (int)Math.round(r*Math.sin(theta));
                putSafe(answer, currentX, currentY, mat.get(i, j));
                // draws lines from left side to a point / unnecessary
                /*if(i == 0 && j == 0 || i == 0 && j == mat.cols() - 1){
                    Imgproc.line(answer, new Point(currentY, currentX), new Point(originalY, originalX), new Scalar(81.0, 255.0, 0.0), 2, Imgproc.LINE_AA);
                }*/
            }
        }
//        Mat answer = results.baseImage();
        CalibrationInformation ci = results.calibrationInformation();
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_tracking.first(), p_tracking.second(), 1F);
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_first.first(), p_first.second(), 1F);
        Calib3d.drawFrameAxes(answer, ci.cameraMatrix(), ci.distCoeffs(), p_second.first(), p_second.second(), 1F);

        return answer;
        /*  Relations of crossection => beam
                back
            +-----------+
        top |           | bottom
            |           |
            +-----------+
                front
        */
    }

    /*private static void drawLine(Mat dest, int fromX, int fromY, int toX, int toY, int thickness, double[] color){
        double slope = (fromY - toY)/(double)(fromX - toX);
        int yIntercept = (int)Math.round(-fromX*slope + fromY);
        int largerX = Math.max(fromX, toX);
        int smallerX = Math.min(fromx, toX);
        for(int i = smallerX; i <= largerX; i++){
            for(int j = 0; j < ((slope != Double.POSITIVE_INFINITY && slope != Double.NEGATIVE_INFINITY) ? Math.abs(slope) : Math.abs(fromY - toY)); j++){
                for(int k = -thickness/2; k <= thickness/2; k++){
                    for(int l = -thickness/2; l <= thickness/2; l++){
                        putSafe(dest, i + l, (int)Math.round(slope*i) + yIntercept + k + j, color);
                    }
                }
            }
        }
    }*/

    private static void putSafe(Mat dest, int x, int y, double[] data){
        if(x >= dest.rows() || y >= dest.cols() || x < 0 || y < 0){
            return;
        }
        dest.put(x, y, data);
    }
}