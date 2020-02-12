package userinterface;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

public class settingsWindow {
   private JFrame mainFrame;
   private static Boolean start = false;
   private JTextField placeholder_T;
   static HashMap<String, String> map = new HashMap<>();
   
   private String tutorialText = "Choose your settings here and when ready to start the program hit \"Start\"\n"
   									+ "Hold the selected beam in front of the camera in a well lit area\n"
   									+ "and make sure you are not wearing red";
   
   public settingsWindow(){
      prepareGUI();
   }
   
   /**
   Calls to set up frame and add input fields then waits for user to hit start
   */
   public static HashMap<String, String> generateGUI(){
      settingsWindow swingListenerDemo = new settingsWindow();  
      swingListenerDemo.createInputFields();
      while(start == false){
    	  try {
    		  Thread.sleep(200);
    	  } catch(InterruptedException e) {
    		  
    	  }	
      }
      return map;
   }
   
   /**
   Sets up the frame
   */
   private void prepareGUI(){
      mainFrame = new JFrame("Settings");
      mainFrame.setSize(800,800);
      
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });
      mainFrame.setVisible(true);
   }
   
   /**
   Creates Input fields and adds action listeners to Buttons. Waits until user hits start
   */
   private void createInputFields(){   

      JPanel panel = new JPanel();
      panel.setMaximumSize(new Dimension(400, 400));
      panel.setBackground(Color.yellow);
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      JLabel placeholder_L = new JLabel("Placeholder Text Field");
      placeholder_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
      
      JLabel BW_L = new JLabel("Display in black and white?");
      BW_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
      
      JLabel BoxCyl_L = new JLabel("Are you using a box or cylinder?");
      BoxCyl_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
      
      placeholder_T = new JTextField("400",16);
      placeholder_T.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
      placeholder_T.setMaximumSize(new Dimension(200,30));
      
      JButton BWButton = new JButton("False");
      BWButton.setText("False");
      map.put("BW", "false");
      BWButton.setMaximumSize(new Dimension(80, 50));
      BWButton.addActionListener(new BWListener());
      
      JButton BoxCylButton = new JButton("Box");
      BoxCylButton.setText("Box");
      map.put("BoxCyl", "Box");
      BoxCylButton.setMaximumSize(new Dimension(80, 50));
      BoxCylButton.addActionListener(new BoxCylListener());
      
      JButton startButton = new JButton("start");
      startButton.setMaximumSize(new Dimension(80, 50));
      startButton.addActionListener(new startListener());
      
      JTextArea tutorial = new JTextArea(tutorialText);
      tutorial.setEditable(false);
      tutorial.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
      tutorial.setBackground(Color.yellow);
      tutorial.setAlignmentX(0);
      tutorial.setMaximumSize(new Dimension(800, 300));
      
      panel.add(placeholder_L);
      panel.add(placeholder_T);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(BW_L);
      panel.add(BWButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(BoxCyl_L);
      panel.add(BoxCylButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(startButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(tutorial);
      mainFrame.add(panel);
      mainFrame.setVisible(true);
   }
   
   /**
   When button is clicked toggle the dictionary to either true or false and change text
   */
   class BWListener implements ActionListener{
	      public void actionPerformed(ActionEvent e) {
	    	 Object source = e.getSource();
	    	 JButton btn = (JButton)source;
	    	 if (btn.getText() == "True") {
	    		 btn.setText("Flase");
	    		 map.put("BW", "false");
	    	 } else {
		    	 map.put("BW", "true");
		    	 btn.setText("True");
	    	 }
	      }
	   }
   
   class BoxCylListener implements ActionListener{
	      public void actionPerformed(ActionEvent e) {
	    	 Object source = e.getSource();
	    	 JButton btn = (JButton)source;
	    	 if (btn.getText() == "Box") {
	    		 btn.setText("Cylinder");
	    		 map.put("BoxCyl", "cyl");
	    	 } else {
		    	 map.put("BoxCyl", "box");
		    	 btn.setText("Box");
	    	 }
	      }
	   }
   
   /**
   When start is clicked, add all text fields to the setting dictionary and move on
   */
   class startListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
    	 start = true;
         mainFrame.setVisible(false);
         map.put("placeholder_T",placeholder_T.getText());
      }
   }
   
}