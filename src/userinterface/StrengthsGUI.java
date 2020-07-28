package userinterface;

import java.util.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.io.*;

import org.json.*;
import org.opencv.core.*;

import driver.*;
import markerdetector.*;
import util.*;

//Settings: Which simulations to turn on, markerbuffering
//Menus: Calibrate camera, stop simulation at frame

//TODO: write menu action listeners, figure out option tabs, write simulationPanel methods for reading and removing simulations,
//figure out how to disable certain buttons, write documentation

public class StrengthsGUI{
	private JFrame frame;
	private List<SimulationPanel> simulationPanels;
	private CalibrationInformation calibrationInformation;
	private int numPanels;
	private MarkerDetector detector;

	private static VideoCap webcam = new VideoCap();

	private static MenuBarSkeleton<StrengthsGUI> bar;
	//private static OptionPaneSkeleton<StrengthsGUI> optionPane;
	private static List<Option<?, StrengthsGUI>> options;
	private static List<Option<?, SimulationPanel>> panelOptions;

	//Hardcoding eligible simulations: maybe not a great idea? 
	//At the same time it's difficult to think of a much better way. 
	//Reflection-based operations such as getting all subclasses or using annotations would be very expensive, 
	//although they would allow developers to use new simulations without editing this file.
	private static List<Class<? extends Simulation>> eligibleSimulations = List.of(
		CrossSimulation.class,
		DividedSimulation.class,
		CoordinateTestSimulation.class,
		SimpleSimulation.class
	);

	private static final StaticActionListener<StrengthsGUI> calibrateCamera = (action, gui) -> {
		gui.calibrateCamera();
	};

	private static final StaticActionListener<StrengthsGUI> loadCalibration = (action, gui) -> {
		//gui.frame = null;
	};

	private static final StaticActionListener<StrengthsGUI> settings = (action, gui) -> {
		Map<String, List<InstanceOption<?, ?>>> tabs = new LinkedHashMap<String, List<InstanceOption<?, ?>>>();
		List<InstanceOption<?, ?>> instanceOptions = new LinkedList<InstanceOption<?, ?>>();
		for(Option<?, StrengthsGUI> o : options){
			instanceOptions.add(InstanceOption.make(o, gui));
		}
		tabs.put("General", instanceOptions);
		for(int i = 0; i < gui.simulationPanels.size(); i++){
			SimulationPanel sp = gui.simulationPanels.get(i);
			instanceOptions = new LinkedList<InstanceOption<?, ?>>();
			for(Option<?, SimulationPanel> o : panelOptions){
				instanceOptions.add(InstanceOption.make(o, sp));
			}
			tabs.put("Simulation " + i, instanceOptions);
		}
		JOptionPane pane = UserInterfaceUtils.tabbedOptionPane(tabs);
		JDialog dialog = new JDialog(gui.frame, "Settings", JDialog.ModalityType.APPLICATION_MODAL);
		dialog.setContentPane(pane);
		dialog.pack();
		dialog.setLocationRelativeTo(gui.frame);
		dialog.setVisible(true);
	};

	private static final StaticActionListener<StrengthsGUI> pause = (action, gui) -> {
		//gui.frame = null;
	};

	private static final StaticActionListener<StrengthsGUI> resume = (action, gui) -> {
		//gui.frame = null;
	};

	static {
		MenuItemSkeleton<StrengthsGUI> calibrateCameraItem = new MenuItemSkeleton<StrengthsGUI>("Calibrate Camera...", calibrateCamera);
		MenuItemSkeleton<StrengthsGUI> loadCalibrationItem = new MenuItemSkeleton<StrengthsGUI>("Load Calibration File...", loadCalibration);
		MenuSkeleton<StrengthsGUI> calibrationMenu = new MenuSkeleton<StrengthsGUI>("Calibration", List.of(calibrateCameraItem, loadCalibrationItem));

		MenuItemSkeleton<StrengthsGUI> settingsItem = new MenuItemSkeleton<StrengthsGUI>("Settings...", settings);
		MenuSkeleton<StrengthsGUI> preferencesMenu = new MenuSkeleton<StrengthsGUI>("Preferences", List.of(settingsItem));

		MenuItemSkeleton<StrengthsGUI> pauseItem = new MenuItemSkeleton<StrengthsGUI>("Pause Simulation", pause);
		MenuItemSkeleton<StrengthsGUI> resumeItem = new MenuItemSkeleton<StrengthsGUI>("Resume Simulation", resume);
		MenuSkeleton<StrengthsGUI> simulationMenu = new MenuSkeleton<StrengthsGUI>("Simulation", List.of(pauseItem, resumeItem));

		bar = new MenuBarSkeleton<StrengthsGUI>(List.of(calibrationMenu, preferencesMenu, simulationMenu));

		Option<Integer, StrengthsGUI> testSpinner = new IntSpinnerOption<StrengthsGUI>("Simulations", "Select Number of Simulations", 1, (gui) -> {
			return gui.numPanels;
		}, (value, gui) -> {
			if(gui.numPanels == value){
				return;
			}
			
			List<Simulation> nullSim = List.of();
			List<SimulationPanel> panels = new LinkedList<SimulationPanel>();
			for(int i = 0; i < value; i++){
				if(i < gui.numPanels){
					panels.add(gui.simulationPanels.get(i));
				} else {
					panels.add(new SimulationPanel(nullSim));
				}
			}
			gui.numPanels = value;
			gui.simulationPanels = panels;
			gui.updatePanels();
		}, 0, 10, 1);
		options = List.of(testSpinner);

		List<Option<?, SimulationPanel>> panelOptionList = new LinkedList<Option<?, SimulationPanel>>();

		for(Class<? extends Simulation> cl : getAllEligibleSimulations()){
			String className = cl.getSimpleName();
			panelOptionList.add(new CheckboxOption<SimulationPanel>(className, className, false, (panel) -> {
				return panel.getRunningSimulations().contains(cl);
			}, (value, panel) -> {
				if(panel.getRunningSimulations().contains(cl) == value){
					return;
				}
				if(value){
					try{
						Simulation s = cl.getDeclaredConstructor().newInstance();
						panel.addSimulation(s);
					} catch(ReflectiveOperationException e){
						System.out.println("Reflective operation failed");
					}
				} else {
					panel.removeSimulation(cl);
				}
				
			}));
		}
		panelOptions = List.copyOf(panelOptionList);
		//optionPane = new OptionPaneSkeleton<StrengthsGUI>(List.of(test, testSpinner));
	}

	{
		this.frame = new JFrame("Strengths \uD83D\uDCAA");
		this.frame.setJMenuBar(bar.getComponent(this));
		this.frame.setBounds(0, 0, 1080, 720);

		for(Option<?, StrengthsGUI> o : options){
			o.resetToDefault(this);
		}
	}

	public StrengthsGUI(){
		this(null);
	}

	public StrengthsGUI(CalibrationInformation ci){
		this.calibrationInformation = ci;
		this.frame.setVisible(true);
		this.updateDetector();
		if(this.calibrationInformation == null){
			JOptionPane.showMessageDialog(this.frame, "Your camera has not been calibrated yet. We will now start camera calibration.");
			this.calibrateCamera();
		}
	}

	private void updateDetector(){
		this.detector = new MarkerDetector(this.calibrationInformation);
	}

	private void updatePanels(){
		JPanel contentPane = new JPanel(new GridLayout());
		for(SimulationPanel sp : this.simulationPanels){
			contentPane.add(sp);
		}
		this.frame.setContentPane(contentPane);
		this.frame.revalidate();
		this.frame.repaint();
	}

	public void updateSimulations(){
		Mat m = webcam.getOneFrame();
        DetectorResults results = detector.detectMarkers(m, 4);
        this.updateSimulations(results);
	}

	public void updateSimulations(DetectorResults results){
        for(SimulationPanel sp : this.simulationPanels){
        	sp.simulate(results);
        	//Possibly unnecessary?
        	sp.repaint();
        }
	}

	private void calibrateCamera(){
		List<SimulationPanel> tmpPanels = this.simulationPanels;
		CameraCalibrationPanel ccp = new CameraCalibrationPanel();
		this.simulationPanels = List.of(ccp);
		this.updatePanels();
		//this.frame.requestFocusInWindow(); 
		this.frame.addKeyListener(ccp);
		new Thread(() -> {
			JOptionPane.showMessageDialog(this.frame, "Camera Calibration Started [instructions].");
			CalibrationInformation calibrationInformation;
			try{
				calibrationInformation = ccp.calibrateCamera();
			} catch(InterruptedException e){
				Thread.currentThread().interrupt();
				//Unreachable line of code to prevent the compiler from complaining
				throw new RuntimeException();
			}
			this.calibrationInformation = calibrationInformation;
			this.updateDetector();
			this.simulationPanels = tmpPanels;
			this.frame.removeKeyListener(ccp);
			this.updatePanels();
			JOptionPane.showMessageDialog(this.frame, "Calibration Successful.");
		}).start();
	}

	//Change this method if a better way of marking eligible simulations is found.
	private static List<Class<? extends Simulation>> getAllEligibleSimulations(){
		return eligibleSimulations;
	}

	public static void main(String[] args) throws IOException {
		StrengthsGUI gui;
		if(args.length > 0){
			String content = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
        	JSONObject obj = new JSONObject(content);
			gui = new StrengthsGUI(CalibrationInformation.fromJSONObject(obj));
		} else {
			gui = new StrengthsGUI();
		}
        while(true){
            gui.updateSimulations();
        }
	}
}