package userinterface;

import javax.swing.*;

/**Option for an Integer value using a JSpinner representation.
@author Owen Kulik
*/

public class IntSpinnerOption<V> extends Option<Integer, V>{
	private final int minValue;
	private final int maxValue;
	private final int step;

	/**Constructs an IntSpinnerOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@param minValue the spinner's minimum value.
	@param maxValue the spinner's maximum value.
	@param step teh interval between valid spinner values.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public IntSpinnerOption(String name, String message, Integer defaultValue, Reader<Integer, V> reader, Enactor<Integer, V> enactor, int minValue, int maxValue, int step){
		super(name, message, defaultValue, reader, enactor);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}

	/**Returns an OptionEvaluator for this IntSpinnerOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JSpinner.<br>
	The OptionEvaluator's evaluate method will return the JSpinner's value. It will never return null.
	@param currentValue the JSpinner's initial value.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this IntSpinnerOption.
	*/
	public OptionEvaluator<Integer> getEvaluator(Integer currentValue){
		return new OptionEvaluator<Integer>(){
			private JSpinner component;

			{
				this.component = new JSpinner(new SpinnerNumberModel((int)currentValue, IntSpinnerOption.this.minValue, IntSpinnerOption.this.maxValue, IntSpinnerOption.this.step));
			}

			@Override
			public JComponent getComponent(){
				return this.component;
			}

			@Override
			public Integer evaluate(){
				return (Integer)this.component.getValue();
			}
		};
	}
}