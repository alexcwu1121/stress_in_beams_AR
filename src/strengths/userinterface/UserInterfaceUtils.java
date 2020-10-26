package userinterface;

import javax.swing.*;
import java.util.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**Uninstantiatable class containing utility methods for the userinterface package.
@author Owen Kulik
*/

public class UserInterfaceUtils {
	private UserInterfaceUtils(){}

	private static final Map<Class<?>, Class<? extends Option>> optionClassMappings = new HashMap<>();
	static {
		optionClassMappings.put(Integer.class, IntSpinnerOption.class);
		optionClassMappings.put(int.class, IntSpinnerOption.class);
		//optionClassMappings.put(Long.class, IntSpinnerOption.class);
		optionClassMappings.put(Short.class, IntSpinnerOption.class);
		optionClassMappings.put(short.class, IntSpinnerOption.class);
		optionClassMappings.put(Byte.class, IntSpinnerOption.class);
		optionClassMappings.put(byte.class, IntSpinnerOption.class);
		optionClassMappings.put(Double.class, DoubleSpinnerOption.class);
		optionClassMappings.put(double.class, DoubleSpinnerOption.class);
		optionClassMappings.put(Float.class, FloatSpinnerOption.class);
		optionClassMappings.put(float.class, FloatSpinnerOption.class);
		optionClassMappings.put(Boolean.class, CheckboxOption.class);
		optionClassMappings.put(boolean.class, CheckboxOption.class);
		optionClassMappings.put(OptionalSimulationParameters.class, SimulationOption.class);
	}

	/**Returns an Option class which is suitable for use with values of the given class.
	@param cl The class to return an option for
	@throws IllegalArgumentException if no Option class was found for the given class.
	@throws NullPointerException if cl is null
	@return an Option class which is suitable for use with values of the given class.
	*/
	public static Class<? extends Option> getOptionForClass(Class<?> cl){
		if(cl == null){
			throw new NullPointerException();
		}
		if(!optionClassMappings.containsKey(cl)){
			throw new IllegalArgumentException("No Option found for " + cl.toString());
		}
		return optionClassMappings.get(cl);
	}

	/**Returns a JOptionPane for the given InstanceOption objects.<br>
	The JOptionPane will consist of a row for each option.<br>
	Each row will have the option's message on the left side and the option's component on the right side.<br>
	The pane will have three buttons at the bottom: "Apply and Close", "Reset to Defaults", and "Cancel", each of which do what their names suggest.
	@param optionMappings the InstanceOptions to use.
	@throws NullPointerException if optionMappings or any value in optionMappings is null.
	@return a JOptionPane for the given InstanceOption objects.
	*/
	public static JOptionPane optionPane(List<InstanceOption<?, ?>> optionMappings){
		return optionPaneWithContent(getTab(optionMappings), optionMappings);
	}
	
	/**Returns a JOptionPane with tabs for the given Strings and InstanceOptions.<br>
	Each entry in the given map represents a tab in the option pane.<br>
	The key is the name of the pane, and the value is the options which will go in that pane.<br>
	The content of the tabs in the returned option pane are the same as the content of a pane returned by the optionPane() method.
	@param optionTabMappings the option tab mappings.
	@throws NullPointerException if optionTabMappings or any value in optionTabMappings is null.
	@return a JOptionPane for the given mappings.
	*/
	public static JOptionPane tabbedOptionPane(Map<String, List<InstanceOption<?, ?>>> optionTabMappings){
		JTabbedPane panel = new JTabbedPane();
		List<InstanceOption<?, ?>> options = new LinkedList<InstanceOption<?, ?>>();
		for(Map.Entry<String, List<InstanceOption<?, ?>>> me : optionTabMappings.entrySet()){
			panel.addTab(me.getKey(), getTab(me.getValue()));
			options.addAll(me.getValue());
		}
		return optionPaneWithContent(panel, options);
	}

	//Returns a JOptionPane with "Apply and Close", "Reset to Defaults", and "Cancel" buttons which work for the given options, and has content as its content panel.
	private static JOptionPane optionPaneWithContent(JComponent content, List<InstanceOption<?, ?>> options){
		JButton apply = new JButton("Apply and Close");
		JButton reset = new JButton("Reset to Defaults");
		JButton cancel = new JButton("Cancel");
		for(InstanceOption<?, ?> option : options){
			registerOption(option, apply, reset);
		}
		apply.addActionListener((action) -> {
			SwingUtilities.windowForComponent(content).dispose();
		});
		reset.addActionListener((action) -> {
			SwingUtilities.windowForComponent(content).dispose();
		});
		cancel.addActionListener((action) -> {
			SwingUtilities.windowForComponent(content).dispose();
		});
		Object[] buttons =  new Object[]{apply, reset, cancel};
		JOptionPane optionPane = new JOptionPane(content, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
		return optionPane;
	}

	//Returns a JPanel representing option rows for the given instance options.
	private static JPanel getTab(List<InstanceOption<?, ?>> optionMappings){
		JPanel tab = new JPanel();
		tab.setLayout(new GridBagLayout());
		int row = 0;
		for(InstanceOption<?, ?> entry : optionMappings){
			OptionEvaluator evaluator = entry.getEvaluator();
			tab.add(new JLabel(entry.getOption().getMessage()), makeGridBagConstraint(0, row, 1, 1, GridBagConstraints.WEST));
			tab.add(evaluator.getComponent(), makeGridBagConstraint(1, row, 1, 1, GridBagConstraints.WEST));
			row++;
		}
		return tab;
	}

	//Adds actionlisteners relating to the given InstanceOption the the given apply and reset buttons.
	private static <Q, V> void registerOption(InstanceOption<Q, V> instanceOption, JButton apply, JButton reset){
		apply.addActionListener((action) -> {
			instanceOption.enact();
		});
		reset.addActionListener((action) -> {
			instanceOption.resetToDefault();
		});
	}

	//Method to easily construct a GridBagConstratints object.
	private static GridBagConstraints makeGridBagConstraint(int x, int y, int width, int height, int anchor){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.anchor = anchor;
		gbc.ipadx = 10;
		//gbc.weightx = 1.0;
		return gbc;
	}
}