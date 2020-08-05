package userinterface;

import java.util.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;

import org.json.*;
import org.opencv.core.*;

import driver.*;
import markerdetector.*;
import util.*;
import simulation.*;

//Settings: Which simulations to turn on, markerbuffering, camera id setting
//Menus: Calibrate camera, stop simulation at frame

//TODO: write menu action listeners, move annotations to util package, write documentation

//Current application design questions:
//Should users be able to manually save/load calibration files or should there just be a hardcoded location that users don't know about?
//Hardcoding eligible simulations
//How should human-readable names and marking external parameters be done? Should it be annotations in the simulation package or code in the userinterface package?
//userinterface package/StrengthsGUI class too powerful?
//Should CameraCalibrationSimulation be in userinterface?

/**The main GUI class and current main method class of the project.
@author Owen Kulik
*/

public class StrengthsGUI{
	private JFrame frame;
	private List<SimulationPanel> simulationPanels;
	private CalibrationInformation calibrationInformation;
	private int numPanels;
	private MarkerDetector detector;
	private State state;
	private Map<State, List<JComponent>> stateEnablings = new EnumMap<State, List<JComponent>>(State.class);
	{
		for(State s : State.values()){
			stateEnablings.put(s, new LinkedList<JComponent>());
		}
	}

	private static final String CALIBRATION_FILE = "../config/calibration.json";

	private static VideoCap webcam = new VideoCap();

	private static MenuBarSkeleton<StrengthsGUI> bar;
	//private static OptionPaneSkeleton<StrengthsGUI> optionPane;
	private static List<Option<?, StrengthsGUI>> options;
	private static List<Option<?, SimulationPanel>> panelOptions;

	//Hardcoding eligible simulations: maybe not a great idea? 
	//At the same time it's difficult to think of a much better way. 
	//Reflection-based operations such as getting all subclasses or using annotations would be very expensive, 
	//although they would allow developers to use new simulations without editing this file.
	private static final List<Class<? extends Simulation>> eligibleSimulations = List.of(
		CrossSimulation.class,
		DividedSimulation.class,
		CoordinateTestSimulation.class,
		SimpleSimulation.class
	);

	private static final Calibrator calibrator = new ArucoCalibrator();

	private static final StaticActionListener<StrengthsGUI> calibrateCamera = (action, gui) -> {
		gui.calibrateCamera();
	};

	/*private static final StaticActionListener<StrengthsGUI> loadCalibration = (action, gui) -> {
		
	};

	private static final StaticActionListener<StrengthsGUI> saveCalibration = (action, gui) -> {

	};*/

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
		gui.changeState(State.PAUSED);
	};

	private static final StaticActionListener<StrengthsGUI> resume = (action, gui) -> {
		gui.changeState(State.PLAYING);
	};

	static {
		MenuItemSkeleton<StrengthsGUI> calibrateCameraItem = new StateMenuItemSkeleton("Calibrate Camera...", calibrateCamera, State.PLAYING, State.PAUSED);
		//MenuItemSkeleton<StrengthsGUI> loadCalibrationItem = new MenuItemSkeleton<StrengthsGUI>("Load Calibration File...", loadCalibration);
		//MenuItemSkeleton<StrengthsGUI> saveCalibrationItem = new MenuItemSkeleton<StrengthsGUI>("Save Calibration File...", loadCalibration);
		MenuSkeleton<StrengthsGUI> calibrationMenu = new MenuSkeleton<StrengthsGUI>("Calibration", List.of(calibrateCameraItem/*, loadCalibrationItem, saveCalibrationItem*/));

		MenuItemSkeleton<StrengthsGUI> settingsItem = new StateMenuItemSkeleton("Settings...", settings, State.PLAYING, State.PAUSED);
		MenuSkeleton<StrengthsGUI> preferencesMenu = new MenuSkeleton<StrengthsGUI>("Preferences", List.of(settingsItem));

		MenuItemSkeleton<StrengthsGUI> pauseItem = new StateMenuItemSkeleton("Pause Simulations", pause, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK), -1, State.PLAYING);
		MenuItemSkeleton<StrengthsGUI> resumeItem = new StateMenuItemSkeleton("Resume Simulations", resume, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), -1, State.PAUSED);
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
			HumanReadableName hrn = cl.getAnnotation(HumanReadableName.class);
			String message = hrn == null ? className : hrn.value();
			panelOptionList.add(new CheckboxOption<SimulationPanel>(className, message, false, (panel) -> {
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

	/**Constructs a StrengthsGUI. Since no calibration information is available, the user will be immediately prompted to calibrate their camera.
	*/
	public StrengthsGUI(){
		this(null);
	}

	/**Constructs a StrengthsGUI with the given CalibrationInformation. If ci is null, the user will be prompted to calibrate their camera.
	@param ci the calibration information.
	*/
	public StrengthsGUI(CalibrationInformation ci){
		this.calibrationInformation = ci;
		this.frame.setVisible(true);
		this.updateDetector();
		this.changeState(State.PLAYING);
		if(this.calibrationInformation == null){
			JOptionPane.showMessageDialog(this.frame, "Your camera has not been calibrated yet. We will now start camera calibration.");
			this.calibrateCamera();
		}
	}

	//Updates the detector with the current calibration information.
	private void updateDetector(){
		this.detector = new MarkerDetector(this.calibrationInformation);
	}

	//Updates the frame's content pane to reflect any changes in the simulationPanels variable.
	private void updatePanels(){
		JPanel contentPane = new JPanel(new GridLayout());
		for(SimulationPanel sp : this.simulationPanels){
			contentPane.add(sp);
		}
		this.frame.setContentPane(contentPane);
		this.frame.revalidate();
		this.frame.repaint();
	}

	/**Updates the simulations to the current frame by one frame using this StrengthsGUI's MarkerDetector.
	Does nothing if the simulations are currently paused.
	*/
	public void updateSimulations(){
		Mat m = webcam.getOneFrame();
        DetectorResults results = detector.detectMarkers(m, 4);
        this.updateSimulations(results);
	}

	/**Updates the simulations using the provided DetectorResults.
	@param results the detector results.
	@throws NullPointerException if results is null.
	*/
	public void updateSimulations(DetectorResults results){
		if(this.state != State.PAUSED){
	        for(SimulationPanel sp : this.simulationPanels){
	        	sp.simulate(results);
	        	//Possibly unnecessary?
	        	sp.repaint();
	        }
    	}
	}

	//Calibrates the user's camera.
	private void calibrateCamera(){
		List<SimulationPanel> tmpPanels = this.simulationPanels;
		CameraCalibrationPanel ccp = new CameraCalibrationPanel(getCalibrator());
		this.simulationPanels = List.of(ccp);
		this.updatePanels();
		this.changeState(State.CALIBRATING);
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
			this.changeState(State.PLAYING);
			this.saveCalibrationFile();
		}).start();
	}

	//Changes the current state by disabling items limited to the old state and enabling items which are enabled for the new state.
	private void changeState(State newState){
		if(this.state == null){
			for(State s : state.values()){
				this.setEnabledForState(s, false);
			}
		} else {
			this.setEnabledForState(this.state, false);
		}
		this.state = newState;
		this.setEnabledForState(this.state, true);
	}

	//Sets whether a component is enabled for all components for the given State.
	private void setEnabledForState(State state, boolean enabled){
		for(JComponent jc : this.stateEnablings.get(state)){
			jc.setEnabled(enabled);
		}
	}

	//Saves the current calibrationInformation.
	private void saveCalibrationFile(){
		try (PrintStream out = new PrintStream(new FileOutputStream(CALIBRATION_FILE))) {
    		out.print(this.calibrationInformation.toJSONObject().toString());
		} catch(IOException e){
			JOptionPane.showMessageDialog(this.frame, "Calibration information could not be saved. The calibration will only apply for this session.");
		}
	}

	//Returns all eligible simulations, ie simulations which users can manually enable. Change this method if a better way of marking eligible simulations is found.
	private static List<Class<? extends Simulation>> getAllEligibleSimulations(){
		return eligibleSimulations;
	}

	//Returns the calibrator currently being used. Change if a different method of determining the calibrator is used.
	private static Calibrator getCalibrator(){
		return calibrator;
	}

	//Enum used to mark the GUI as being in a certain state.
	private static enum State{
		PLAYING, PAUSED, CALIBRATING;
	}

	//Subclass of MenuItemSkeleton which is used to mark MenuItems as only being enabled in certain states.
	private static class StateMenuItemSkeleton extends MenuItemSkeleton<StrengthsGUI>{
		private State[] enabledStates;

		public StateMenuItemSkeleton(String message, StaticActionListener<StrengthsGUI> listener, State... enabledStates){
			super(message, listener);
			this.enabledStates = enabledStates;
		}

		public StateMenuItemSkeleton(String message, StaticActionListener<StrengthsGUI> listener, KeyStroke accelerator, int mnemonic, State... enabledStates){
			super(message, listener, accelerator, mnemonic);
			this.enabledStates = enabledStates;
		}

		public JMenuItem getComponent(StrengthsGUI enactOn){
			JMenuItem answer = super.getComponent(enactOn);
			for(State st : this.enabledStates){
				enactOn.stateEnablings.get(st).add(answer);
			}
			return answer;
		}
	}

	public static void main(String[] args) throws IOException {
		StrengthsGUI gui;
		try{
			String content = new Scanner(new File(CALIBRATION_FILE)).useDelimiter("\\Z").next();
        	JSONObject obj = new JSONObject(content);
			gui = new StrengthsGUI(CalibrationInformation.fromJSONObject(obj));
		} catch(IOException e) {
			gui = new StrengthsGUI();
		}
        while(true){
            gui.updateSimulations();
        }
	}
}