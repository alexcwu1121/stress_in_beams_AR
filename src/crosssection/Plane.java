package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
    @param vecX The X rotational vector
    @param vecY The Y rotational vector
	@author Nicholas Mataczynski
    */
    public void planeUpdate(int vecX[], int vecY[]) {

    	int xStress = vecX[0] - vecY[0];
    	int yStress = vecX[1] - vecY[1];
    	int xMiddle = xSpaces / 2;
    	int yMiddle = ySpaces / 2;
    	int magnifier = 255 / stressCap;

    	for (int i = 0; i < xSpaces; i++){
    		for (int j = 0; j < ySpaces; j++){
    			int xColor = (int)(((double)i / (double)xMiddle - 1) * Math.min(Math.max(xStress, -stressCap), stressCap) * magnifier);
    			int yColor = (int)(((double)j / (double)yMiddle - 1) * Math.min(Math.max(yStress, -stressCap), stressCap) * magnifier);
    			int combinedColor = xColor - yColor;
    			if (combinedColor >= 0){
    				boxes[i][j].setColor(255, Math.max(255 - combinedColor, 0), Math.max(255 - combinedColor, 0));
    			} else {
    				boxes[i][j].setColor(Math.max(255 - Math.abs(combinedColor), 0), Math.max(255 - Math.abs(combinedColor), 0), 255);
    			}
    		}
        }
        //frame.repaint();
    }

    /**Returns a BufferedImage representing this Plane. The image is in BufferedImage.TYPE_3BYTE_BGR.
    @return the image.
    @author Owen Kulik (NOT nick)
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