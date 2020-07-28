package userinterface;

import javax.swing.*;
import java.util.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class UserInterfaceUtils {
	private UserInterfaceUtils(){}

	public static JOptionPane optionPane(List<InstanceOption<?, ?>> optionMappings){
		return optionPaneWithContent(getTab(optionMappings), optionMappings);
	}
											  //.... I can explain
	public static JOptionPane tabbedOptionPane(Map<String, List<InstanceOption<?, ?>>> optionTabMappings){
		JTabbedPane panel = new JTabbedPane();
		List<InstanceOption<?, ?>> options = new LinkedList<InstanceOption<?, ?>>();
		for(Map.Entry<String, List<InstanceOption<?, ?>>> me : optionTabMappings.entrySet()){
			panel.addTab(me.getKey(), getTab(me.getValue()));
			options.addAll(me.getValue());
		}
		return optionPaneWithContent(panel, options);
	}

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

	private static JPanel getTab(List<InstanceOption<?, ?>> optionMappings){
		JPanel tab = new JPanel();
		tab.setLayout(new GridBagLayout());
		int row = 0;
		for(InstanceOption<?, ?> entry : optionMappings){
			OptionEvaluator evaluator = entry.getEvaluator();//registerOption(entry, apply, reset);
			tab.add(new JLabel(entry.getOption().getMessage()), makeGridBagConstraint(0, row, 1, 1, GridBagConstraints.WEST));
			tab.add(evaluator.getComponent(), makeGridBagConstraint(1, row, 1, 1, GridBagConstraints.EAST));
			row++;
		}
		return tab;
	}

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