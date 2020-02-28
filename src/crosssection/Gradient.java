package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.concurrent.TimeUnit;

import crosssection.Plane;

public class Gradient extends JPanel{

  	/**
	Class which creates the crosssection Plane
	@author Nicholas Mataczynski
  	*/
	public static void main(String[] args) {

		//settings = settingsWindow.generateGUI();


	    JFrame frame = new JFrame("Crosssection");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(600, 600);
	    frame.setLocationRelativeTo(null);

<<<<<<< HEAD
	    double vec1[] = {0, 0, 0};
	    double vec2[] = {0.5, 0.5, 0.5};

	    Plane cross = new Plane(1.5, 0.3, 1);
	    frame.add(cross);

	    double vecx = 0;
	    double vecy = 0;
	    double vecx2 = 0;
	    double vecy2 = 0;

	    frame.setVisible(true);
	    while (true){
		    cross.planeUpdate(vec1,vec2);
		    frame.repaint();
  
			try
			{
			    Thread.sleep(1);
=======
	    int vec1[] = {0, 0, 0};
	    int vec2[] = {0, 0, 0};

	    Plane cross = new Plane(frame, 3000, 3000, 0.2, 1);
	    frame.add(cross);

	    int vecx = 0;
	    int vecy = 0;
	    int vecx2 = 0;
	    int vecy2 = 0;

	    frame.setVisible(true);
	    while (true){
		    cross.planeUpdate(frame,vec1,vec2);
  
			try
			{
			    Thread.sleep(25);
>>>>>>> f1f14f9... Re-added files
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}

<<<<<<< HEAD
			System.out.println(vecx - vecx2);
			System.out.println(vecy - vecy2);

		    	vec1[0] = vecx;
		    	vec1[1] = vecy;
	     	vec2[0] = vecx2;
	     	vec2[1] = vecy2;

	     	if(Math.random() < 0.5) {
			     vecx += 1;
			 }
			 if(Math.random() < 0.5) {
			     vecx2 += 1;
			 }
			if(Math.random() < 0.5) {
			    vecy += 1;
			}
			if(Math.random() < 0.5) {
			    vecy2 += 1;
			}
			if(Math.random() < 0.5) {
			    vecx -= 1;
			}
			if(Math.random() < 0.5) {
			    vecx2 -= 1;
			}
			if(Math.random() < 0.5) {
			    vecy -= 1;
			}
			if(Math.random() < 0.5) {
			    vecy2 -= 1;
=======
			//System.out.println(vecx - vecx2);
			//System.out.println(vecy - vecy2);

		   	vec1[0] = vecx;
		   	vec1[1] = vecy;
	    	vec2[0] = vecx2;
	    	vec2[1] = vecy2;

	    	if(Math.random() < 0.5) {
			    vecx += 10;
			}
			if(Math.random() < 0.5) {
			    vecx2 += 10;
			}
			if(Math.random() < 0.5) {
			    vecy += 10;
			}
			if(Math.random() < 0.5) {
			    vecy2 += 10;
			}
			if(Math.random() < 0.5) {
			    vecx -= 10;
			}
			if(Math.random() < 0.5) {
			    vecx2 -= 10;
			}
			if(Math.random() < 0.5) {
			    vecy -= 10;
			}
			if(Math.random() < 0.5) {
			    vecy2 -= 10;
>>>>>>> f1f14f9... Re-added files
			}
	    }
  	}
}