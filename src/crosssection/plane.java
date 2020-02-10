package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.HashMap;

import crosssection.gridSpace;
import userinterface.settingsWindow;

public class plane extends JPanel {

	/**
	Draws the grid <br>
	@author Nicholas Mataczynski
  	*/
  	private static gridSpace boxes[][] = new gridSpace[10][10];
    private int width = 30;
    private int height = 40;
    private int startX = 10;
    private int startY = 20;

    public void planeUpdate() {
    	
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < 10; i++){
    		for (int j = 0; j < 10; j++){
    			g2.draw(boxes[i][j].getRect());
    		}
        }
    }

    public static void planeInit(JFrame frame) {
    	for (int i = 0; i < 10; i++){
    		for (int j = 0; j < 10; j++){
    			boxes[i][j] = new gridSpace(i * 10, j * 10, i * 10 + 10, j * 10 + 10);
    		}
        }
    }

  	/**
	Class which creates the crosssection shape <br>
	@author Nicholas Mataczynski
  	*/
	public static void main(String[] args) {

	    JFrame frame = new JFrame("Crosssection");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    plane grid = new plane();
	    frame.add(grid);
	    frame.setSize(360, 300);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);

	    planeInit(frame);
  	}
}