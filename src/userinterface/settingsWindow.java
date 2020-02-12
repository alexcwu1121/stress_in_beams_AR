package userinterface;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.*;


public class settingsWindow {
   private JFrame mainFrame;
   private static Boolean start = false;
   private JTextField placeholder_T;
   static HashMap<String, String> map = new HashMap<>();
   
   private String tutorialText = "Choose your settings here and when ready hit start";
   
   public settingsWindow(){
      prepareGUI();
   }
   
   /**
   Calls to set up frame and add input fields then waits for user to hit start then waits for user to hit start.
   @returns a HashMap<String, String> of setting titles and values.
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
   Sets up the look and feel and frame
   @throws exceptions if nimbus is not set up properly
   */
   private void prepareGUI(){
      UIManager.put("nimbusBase", new Color(119,136,153));
      UIManager.put("nimbusBlueGrey", new Color(119,136,153));
      UIManager.put("control", new Color(119,136,153));

      try {
          for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
              if ("Nimbus".equals(info.getName())) {
                  UIManager.setLookAndFeel(info.getClassName());
                  break;
              }
          }
      } catch (UnsupportedLookAndFeelException e) {
          throw new IllegalStateException("Nimbus is unssupported");
      } catch (ClassNotFoundException e) {
          throw new IllegalStateException("Nimbus is not found");
      } catch (InstantiationException e) {
          throw new IllegalStateException("Nimbus cannot be Instantiated");
      } catch (IllegalAccessException e) {
          throw new IllegalStateException("Nimbus cannot be accessed");
      }
      mainFrame = new JFrame("Settings");
      mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
      mainFrame.setSize(800,800);

         
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });
      mainFrame.setVisible(true);
   }
   
   /**
   Creates Input fields and adds action listeners to Buttons.
   */
   private void createInputFields(){   

      JPanel panel = new JPanel();
      panel.setAlignmentX(Component.CENTER_ALIGNMENT);
      Dimension expectedDimension = new Dimension(800, 800);
      panel.setPreferredSize(expectedDimension);
      panel.setMaximumSize(expectedDimension);
      panel.setMinimumSize(expectedDimension);
      panel.setBackground(new Color(192, 192, 192));
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      JLabel placeholder_L = new JLabel("Placeholder Text Field", SwingConstants.CENTER);
      JLabel scale_L = new JLabel("Size of crossection:");
      JLabel boxCyl_L = new JLabel("Box or Cylinder:");
      JLabel tutorial = new JLabel(tutorialText);
      JButton scaleButton = new JButton("Medium");
      JButton boxcylButton = new JButton("Box");
      JButton startButton = new JButton("Start");
      placeholder_T = new JTextField("400",16);

      placeholder_L.setAlignmentX(Component.CENTER_ALIGNMENT);
      scale_L.setAlignmentX(Component.CENTER_ALIGNMENT);
      boxCyl_L.setAlignmentX(Component.CENTER_ALIGNMENT);
      placeholder_T.setAlignmentX(Component.CENTER_ALIGNMENT);
      scaleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      boxcylButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      tutorial.setAlignmentX(Component.CENTER_ALIGNMENT);

      placeholder_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));   
      scale_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));   
      boxCyl_L.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
      placeholder_T.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
      scaleButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
      boxcylButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
      startButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
      tutorial.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));

      scaleButton.setText("Medium");
      boxcylButton.setText("Box");
      
      placeholder_T.setMaximumSize(new Dimension(200,30));
      scaleButton.setMaximumSize(new Dimension(100, 50));
      boxcylButton.setMaximumSize(new Dimension(100, 50));
      startButton.setMaximumSize(new Dimension(100, 50));
      tutorial.setMaximumSize(new Dimension(525, 50));

      scaleButton.addActionListener(new scaleListener());
      boxcylButton.addActionListener(new BoxCylListener());
      startButton.addActionListener(new startListener());
      
      tutorial.setBackground(new Color(192, 192, 192));

      map.put("scale", "2");
      map.put("BoxCyl", "Box");
      
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(placeholder_L);
      panel.add(placeholder_T);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(scale_L);
      panel.add(scaleButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(boxCyl_L);
      panel.add(boxcylButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(startButton);
      panel.add(Box.createRigidArea(new Dimension(0,10)));
      panel.add(tutorial);

      mainFrame.add(panel);
      mainFrame.setVisible(true);
   }
   
   /**
   When button is clicked toggle the HashMap value to the next size and change corresponding HashMap value.
   */
   class scaleListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
             Object source = e.getSource();
             JButton btn = (JButton)source;
             if (btn.getText() == "Large") {
                    btn.setText("Small");
                    map.put("scale", "1");
             } else if (btn.getText() == "Small"){
                    btn.setText("Medium");
            map.put("scale",  "2");
             } else {
            btn.setText("Large");
            map.put("scale",  "3");
         }
            }
         }
   
   /**
   When button is clicked toggle the BoxCyl button and change the corresponding HashMap value.
   */
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
   When start is clicked, add all text fields to the setting dictionary and move on.
   */
   class startListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
       start = true;
         mainFrame.setVisible(false);
         map.put("placeholder_T",placeholder_T.getText());
      }
   }
   
}