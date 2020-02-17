package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import crosssection.gridSpace;

public class plane extends JPanel {
  	public int stressCap = 5;
  	public int xSpaces = 10;
  	public int ySpaces = 10;
  	public gridSpace boxes[][];
  	public JFrame frame;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < xSpaces; i++){
    		for (int j = 0; j < ySpaces; j++){
    			g2.setColor(boxes[i][j].getColor());
    			g2.fill(boxes[i][j].getRect());
    		}
        }
    }

    /**
    Creates the array of rectangles which make up the crosssection grid
    @param frame_ The JFrame that the drawing is going to take place on.
    @param xSpaces_ The amount of gridSpaces that will be generated every row.
    @param ySpaces_ The amount of gridSpaces that will be generated every column.
	@author Nicholas Mataczynski
    */
    public plane(JFrame frame_, int xSpaces_, int ySpaces_) {
    	xSpaces = xSpaces_;
    	ySpaces = ySpaces_;
    	frame = frame_;

    	Rectangle r = frame.getBounds();
		int h = r.height;
		int w = r.width;
		int xLength = 300 / xSpaces;
		int yLength = 300 / ySpaces;

		boxes = new gridSpace[xSpaces][ySpaces];

    	for (int i = 0; i < xSpaces; i++){
    		for (int j = 0; j < ySpaces; j++){
    			boxes[i][j] = new gridSpace(w - i * xLength - 50, j * yLength + 10, xLength, yLength);
    		}
        }
    }

    /**
    Updates the colors of all gridSpaces in the grid then repaints the JFrame
    @param frame The JFrame that the drawing is going to take place on.
    @param vecX The X rotational vector
    @param vecY The Y rotational vector
	@author Nicholas Mataczynski
    */
    public void planeUpdate(JFrame frame,  int vecX[], int vecY[]) {

    	int xStress = vecX[0] - vecY[0];
    	int yStress = vecX[1] - vecY[1];
    	int middle = xSpaces / 2;
    	int xMagnifier = (125 / stressCap) / (xSpaces / 2);
    	int yMagnifier = (125 / stressCap) / (ySpaces / 2);

    	for (int i = 0; i < xSpaces; i++){
    		for (int j = 0; j < ySpaces; j++){
    			int xColor = (middle - i) * Math.min(Math.max(xStress, -stressCap), stressCap) * xMagnifier;
    			int yColor = (middle - j) * Math.min(Math.max(yStress, -stressCap), stressCap) * yMagnifier;
    			int combinedColor = xColor - yColor;
    			if (combinedColor >= 0){
    				boxes[i][j].setColor(255, 255 - combinedColor, 255 - combinedColor);
    			} else {
    				boxes[i][j].setColor(255 - Math.abs(combinedColor), 255 - Math.abs(combinedColor), 255);
    			}
    		}
        }
        frame.repaint();
    }
}