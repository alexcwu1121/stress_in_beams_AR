package userinterface;

import javax.swing.*;

/**Option class used for Strings.<br>
This option is represented as a JTextField.
@author Owen Kulik
*/

public class StringOption<V> extends Option<String, V>{
	/**Constructs a StringOption from the given values.<br> 
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public StringOption(String name, String message, String defaultValue, Reader<String, V> reader, Enactor<String, V> enactor){
		super(name, message, defaultValue, reader, enactor);
	}

	/**Returns an OptionEvaluator for this StringOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JTextField.<br>
	The OptionEvaluator's evaluate method will return what is in the JTextField. It will never return null.
	@param currentValue the initial value for the JTextField.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this StringOption.
	*/
	public OptionEvaluator<String> getEvaluator(String currentValue){
		if(currentValue == null){
			throw new NullPointerException();
		}
		return new OptionEvaluator<String>(){
			private JTextField component;

			{
				this.component = new JTextField();
				this.component.setText(currentValue);
			}

			@Override
			public JComponent getComponent(){
				return this.component;
			}

			@Override
			public String evaluate(){
				return this.component.getText();
			}
		};
	}
}