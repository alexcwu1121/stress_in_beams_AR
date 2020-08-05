package userinterface;

import javax.swing.*;

/**Option for a Boolean value using a JCheckBox representation.
@author Owen Kulik
*/

public class CheckboxOption<V> extends Option<Boolean, V> {
	/**Constructs a CheckboxOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or defaultValue is null.
	*/
	public CheckboxOption(String name, String message, Boolean defaultValue, Reader<Boolean, V> reader, Enactor<Boolean, V> enactor){
		super(name, message, defaultValue, reader, enactor);
		if(defaultValue == null){
			throw new NullPointerException();
		}
	}

	/**Returns an OptionEvaluator for this CheckboxOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JCheckBox.<br>
	The OptionEvaluator's evaluate method will return whether the JCheckBox is checked. It will never return null.
	@param currentValue whether to check the box initially.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this CheckboxOption.
	*/
	public OptionEvaluator<Boolean> getEvaluator(Boolean currentValue){
		if(currentValue == null){
			throw new NullPointerException();
		}
		return new OptionEvaluator<Boolean>(){
			private JCheckBox component;

			{
				this.component = new JCheckBox();
				this.component.setSelected(currentValue);
			}

			@Override
			public JComponent getComponent(){
				return this.component;
			}

			@Override
			public Boolean evaluate(){
				return this.component.isSelected();
			}
		};
	}
}