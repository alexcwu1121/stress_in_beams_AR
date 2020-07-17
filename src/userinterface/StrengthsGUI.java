package userinterface;

import java.util.*;
import javax.swing.*;
import java.awt.GridLayout;

import driver.*;
import markerdetector.DetectorResults;

//Settings: Which simulations to turn on, markerbuffering
//Menus: Calibrate camera, stop simulation at frame

//TODO: Convert SimulationFrame to SimulationPanel, Include spinner, checkbox options, figure out how to have dynamic number of option panels, write menu action listeners,
//figure out how to disable certain buttons

public class StrengthsGUI{
	private JFrame frame;
	//Pretend that these are JPanels for now
	private List<SimulationFrame> simulationPanels;

	private static final MenuBarSkeleton<StrengthsGUI> bar;
	private static final OptionPaneSkeleton<StrengthsGUI> optionPane = null;

	private static final StaticActionListener<StrengthsGUI> calibrateCamera = (action, gui) -> {
		//gui.frame = null;
	};

	private static final StaticActionListener<StrengthsGUI> loadCalibration = (action, gui) -> {
		//gui.frame = null;
	};

	private static final StaticActionListener<StrengthsGUI> settings = (action, gui) -> {
		//gui.frame = null;
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
	}

	{
		this.frame = new JFrame("Strengths 💪");
		this.simulationPanels = List.of(new SimulationFrame(NullSimulation.get()));
		this.updatePanels();

		//this.frame.setContentPane(this.simulationFrame);
		this.frame.setJMenuBar(bar.getComponent(this));
		this.frame.setBounds(0, 0, 1080, 720);
        this.frame.setVisible(true);
	}

	private void updatePanels(){
		JPanel contentPane = new JPanel(new GridLayout());
		for(SimulationFrame sp : this.simulationPanels){
			contentPane.add(sp);
		}
		this.frame.setContentPane(contentPane);
	}

	public void updateSimulations(DetectorResults results){
        for(SimulationFrame sp : this.simulationPanels){
        	sp.simulate(results);
        	//Possibly unnecessary?
        	sp.repaint();
        }
	}

	public static void main(String[] args){
		new StrengthsGUI();
	}
}