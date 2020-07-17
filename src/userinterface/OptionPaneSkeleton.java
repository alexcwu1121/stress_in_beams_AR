package userinterface;

import javax.swing.*;
import java.util.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class OptionPaneSkeleton<V>{
	//Note that all mentions of Option and OptionComponentSkeleton in this class are "raw" types; they do not include the template.
	private final List<Option<?, V>> options;

	public OptionPaneSkeleton(List<Option<?, V>> options){
		this.options = List.copyOf(options);
	}

	public JOptionPane getComponent(V enactOn){
		JPanel tab = new JPanel();
		tab.setLayout(new GridBagLayout());
		JButton apply = new JButton("Apply and Close");
		JButton reset = new JButton("Rest to Defaults");
		JButton cancel = new JButton("Cancel");
		int row = 0;
		for(Option<?, V> o : this.options){
			OptionEvaluator evaluator = registerOption(o, apply, reset, enactOn);
			tab.add(new JLabel(o.getMessage()), makeGridBagConstraint(0, row, 1, 1, GridBagConstraints.WEST));
			tab.add(evaluator.getComponent(), makeGridBagConstraint(1, row, 1, 1, GridBagConstraints.EAST));
			row++;
		}
		Object[] buttons =  new Object[]{apply, reset, cancel};
		JOptionPane optionPane =  new JOptionPane(tab, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
		return optionPane;
	}

	private <T> OptionEvaluator<T> registerOption(Option<T, V> option, JButton apply, JButton reset, V enactOn){
		OptionEvaluator<T> evaluator = option.getEvaluator();
		apply.addActionListener((action) -> {
			option.enact(evaluator.evaluate(), enactOn);
		});
		reset.addActionListener((action) -> {
			option.resetToDefault(enactOn);
		});
		return evaluator;
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
		return gbc;
	}
}