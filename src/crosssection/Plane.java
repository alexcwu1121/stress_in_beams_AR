package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.*;

import crosssection.GridSpace;

public class Plane extends JPanel {
    private final int stressCap = 255;
    private final int xSpaces;
    private final int ySpaces;
    private final int xDimension;
    private final int yDimension;
    private final GridSpace boxes[][];
    private final GridSpace reference;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.GREEN);
        g2.fill(reference.getRect());

        for (int i = 0; i < xSpaces; i++){
            for (int j = 0; j < ySpaces; j++){
                g2.setColor(boxes[i][j].getColor());
                g2.fill(boxes[i][j].getRect());
            }
        }
    }

    /**
    Creates the array of GridSpaces which make up the crosssection grid
    and initializes variables and reference line.
    @param frame_ The JFrame that the drawing is going to take place on.
    @param xDimension The length of the plane in the x dimension.
    @param yDimension The length of the plane in the y dimension.
    @param resolution The higher this variable is, the fewer girdSpaces will be generated.
    @param scale Determines the scale of the plane.
    @author Nicholas Mataczynski
    */
    public Plane(double ratio, double resolution, double scale) {
        xDimension = (int)(ratio *(double)scale * 100.0);
        yDimension = (int)((double)scale * 100.0);

        xSpaces = (int)(xDimension * resolution);
        ySpaces = (int)(yDimension * resolution);
        int xLength = xDimension / xSpaces;
        int yLength = yDimension / ySpaces;

        reference = new GridSpace(xLength * (xSpaces - 1) - 50, 0, xLength * xSpaces, 10);
        boxes = new GridSpace[xSpaces][ySpaces];

        for (int i = 0; i < xSpaces; i++){
            for (int j = 0; j < ySpaces; j++){
                boxes[i][j] = new GridSpace(i * xLength, j * yLength, xLength, yLength);
            }
        }
    }

    /**
    Updates the colors of all GridSpaces in the grid then repaints the JFrame
    @param rotL The rotational vectors of one end of the beam
    @param rotR The rotational vectors of the other end of the beam
    @param rotM The rotational vectors of the other center of the beam
    */
    public void planeUpdate(double rotL[], double rotR[], double rotM[]) {

        // z-rot / firstpose - secondpose 
        double zStress = Math.toDegrees(rotL[2]) - Math.toDegrees(rotR[2]);
        // y-rot / firstpose - secondpose 
        double yStress = Math.toDegrees(rotL[1]) - Math.toDegrees(rotR[1]);
        double stressAngle = Math.toDegrees(Math.atan(yStress/zStress));
        // flipped currently does nothing, its too unstable at the moment, if you uncomment 3 lines below the variable should work
        boolean flipped = false;
        //if (rotM[0] > 0) {
        //    flipped = true;
        //}

        //Deadzone 
        if(Math.abs(zStress) < 15 && Math.abs(yStress) < 15){
            paintGrid(0,0,false);
        }
        //Bending in the y-axis
        else if (Math.abs(stressAngle) >= 67.5){
            paintGrid(yStress,0,flipped);
        }
        //for sectors in the 1st and 3rd quardrant, y and z both negative/positive
        else if (67.5 > stressAngle && stressAngle >= 22.5){
            if (yStress > 0 && zStress > 0){
                paintGrid((yStress+zStress)/2,(yStress+zStress)/2,flipped);
            }
            else {
                paintGrid((yStress+zStress)/2,(yStress+zStress)/2,flipped);
            }
        }
        //Bending in the z-axis
        else if(22.5 > stressAngle && stressAngle >= -22.5){
            paintGrid(0,zStress,flipped);
        }
        //for sectors in the 2nd and 4th quardrant, y xor z negative
        else if(-22.5 > stressAngle && stressAngle > -67.5){
            if (zStress > 0){
                paintGrid(-(-yStress+zStress)/2,(-yStress+zStress)/2,flipped);
            }
            else {
                paintGrid((yStress-zStress)/2,-(yStress-zStress)/2,flipped);
            }
        }
    }

    /**
    Updates the colors of all GridSpaces in the grid then repaints the JFrame
    @param yStress The degree rotation of the y-axis between the end poses
    @param zStress The degree rotation of the z-axis between the end poses
    @param flip Wether or not the x-axis has a positive rotation
    */
    public void paintGrid(double yStress, double zStress, boolean flip) {
        //If the x-axis has a positive rotation the other rotations flip, this boolean is to conteract that
        int xMiddle = xSpaces / 2;
        int yMiddle = ySpaces / 2;
        int magnifier = 4;
        for (int i = 0; i < xSpaces; i++){
            for (int j = 0; j < ySpaces; j++){
                int lColor = (int)(((double)i / (double)xMiddle - 1) * Math.min(Math.max(zStress, -stressCap), stressCap) * magnifier);
                int rColor = (int)(((double)j / (double)yMiddle - 1) * Math.min(Math.max(yStress, -stressCap), stressCap) * magnifier);
                int combinedColor = lColor - rColor;
                if (combinedColor >= 0){
                    // BLUE / TENSION
                    if (flip){
                        //flipped
                        boxes[i][j].setColor(Math.max(255 - Math.abs(combinedColor), 0), Math.max(255 - Math.abs(combinedColor), 0), 255);
                    }
                    else{
                        //orginial
                        boxes[i][j].setColor(255, Math.max(255 - combinedColor, 0), Math.max(255 - combinedColor, 0));
                    }
                } else {
                    // RED / COMPRESSION
                    if (flip){
                        //flipped
                        boxes[i][j].setColor(255, Math.max(255 - Math.abs(combinedColor), 0), Math.max(255 - Math.abs(combinedColor), 0));
                    }
                    else{
                        //orginial
                        boxes[i][j].setColor(Math.max(255 - Math.abs(combinedColor), 0), Math.max(255 - Math.abs(combinedColor), 0), 255);
                    }
                }
            }
        }
    }

    /**
    Updates the colors of all GridSpaces in the grid then repaints the JFrame
    @param rotL The rotational vectors of one end of the beam
    @param rotR The rotational vectors of the other end of the beam
    */
    public void planeUpdate(Mat rotL, Mat rotR, Mat rotM) {
        planeUpdate(new double[]{rotL.get(0,0)[0], rotL.get(1,0)[0], rotL.get(2,0)[0]}, new double[]{rotR.get(0,0)[0],rotR.get(1,0)[0],rotR.get(2,0)[0]}, new double[]{rotM.get(0,0)[0], rotM.get(1,0)[0], rotM.get(2,0)[0]});
    }

    /**Returns a BufferedImage representing this Plane. The image is in BufferedImage.TYPE_3BYTE_BGR.
    @return the image.
    */
    public BufferedImage getImage() {
        BufferedImage answer = new BufferedImage(xDimension, yDimension, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = (Graphics2D)answer.getGraphics();

        g2.setColor(Color.GREEN);
        g2.fill(reference.getRect());

        for (int i = 0; i < xSpaces; i++){
            for (int j = 0; j < ySpaces; j++){
                g2.setColor(boxes[i][j].getColor());
                g2.fill(boxes[i][j].getRect());
            }
        }
        return answer;
    }
}