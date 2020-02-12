package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.HashMap;

import crosssection.gridSpace;
import userinterface.settingsWindow;

public class plane extends JPanel {
  	private static gridSpace boxes[][] = new gridSpace[10][10];
  	private static HashMap<String, String> settings;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < 10; i++){
    		for (int j = 0; j < 10; j++){
    			g2.setColor(boxes[i][j].getColor());
    			g2.fill(boxes[i][j].getRect());
    		}
        }
    }

    /**
    Updates the colors of all boxes in the grid then repaints the JFrame <br>
    @param frame The JFrame that the drawing is going to take place on.
	@author Nicholas Mataczynski
    */
    public static void planeUpdate(JFrame frame) {
    	for (int i = 0; i < 10; i++){
    		for (int j = 0; j < 10; j++){
    			boxes[i][j].setColor(1, 1, 1);
    		}
        }
        frame.repaint();
    }

    /**
    Creates the array of rectangles which make up the crosssection grid <br>
    @param frame The JFrame that the drawing is going to take place on.
	@author Nicholas Mataczynski
    */
    public static void planeInit(JFrame frame) {

    	Rectangle r = frame.getBounds();
		int h = r.height;
		int w = r.width;

    	for (int i = 0; i < 10; i++){
    		for (int j = 0; j < 10; j++){
    			boxes[i][j] = new gridSpace(w - i * 30 - 50, j * 30 + 10, 30, 30);
    			boxes[i][j].setColor(i * 20, j * 20, i + j * 20);
    		}
        }
    }

  	/**
	Class which creates the crosssection shape <br>
	@author Nicholas Mataczynski
  	*/
	public static void main(String[] args) {

		//settings = settingsWindow.generateGUI();

	    JFrame frame = new JFrame("Crosssection");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    plane grid = new plane();
	    frame.add(grid);
	    frame.setSize(400, 400);
	    frame.setLocationRelativeTo(null);

	    planeInit(frame);

	    frame.setVisible(true);

  	}
}