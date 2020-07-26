package userinterface;

import javax.swing.*;

public class CheckboxOption<V> extends Option<Boolean, V> {
	public CheckboxOption(String name, String message, Boolean defaultValue, Reader<Boolean, V> reader, Enactor<Boolean, V> enactor){
		super(name, message, defaultValue, reader, enactor);
	}

	public OptionEvaluator<Boolean> getEvaluator(Boolean currentValue){
		return new OptionEvaluator<Boolean>(){
			private JCheckBox component;

			@Override
			public JComponent getComponent(){
				this.component = new JCheckBox();
				this.component.setSelected(currentValue);
				return this.component;
			}

			@Override
			public Boolean evaluate(){
				return this.component.isSelected();
			}
		};
	}
}