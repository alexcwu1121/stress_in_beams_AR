package crosssection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.concurrent.TimeUnit;

import crosssection.plane;

public class gradient extends JPanel{

  	/**
	Class which creates the crosssection shape <br>
	@author Nicholas Mataczynski
  	*/
	public static void main(String[] args) {

		//settings = settingsWindow.generateGUI();


	    JFrame frame = new JFrame("Crosssection");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(400, 400);
	    frame.setLocationRelativeTo(null);

	    int vec1[] = {0, 2, 0};
	    int vec2[] = {5, 5, 0};

	    plane cross = new plane(frame, 21, 21);
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
			    Thread.sleep(1000);
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}

		   	vec1[0] = vecx;
		   	vec1[1] = vecy;
	    	vec2[0] = vecx2;
	    	vec2[1] = vecy2;

	    	if(Math.random() < 0.5) {
			    vecx++;
			}
			if(Math.random() < 0.5) {
			    vecx2++;
			}
			if(Math.random() < 0.5) {
			    vecy++;
			}
			if(Math.random() < 0.5) {
			    vecy2++;
			}
			if(Math.random() < 0.5) {
			    vecx--;
			}
			if(Math.random() < 0.5) {
			    vecx2--;
			}
			if(Math.random() < 0.5) {
			    vecy--;
			}
			if(Math.random() < 0.5) {
			    vecy2--;
			}
	    }
  	}
}