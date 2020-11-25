package userinterface;

import javax.swing.*;

/**Option for an Double value using a JSpinner representation.
@author Owen Kulik
*/

public class DoubleSpinnerOption<V> extends Option<Double, V>{
	private final double minValue;
	private final double maxValue;
	private final double step;

	private static final double DEFAULT_MIN_VALUE = Long.MIN_VALUE;//-Double.MAX_VALUE;
	private static final double DEFAULT_MAX_VALUE = Long.MAX_VALUE;//Double.MAX_VALUE;
	private static final double DEFAULT_STEP = .1;

	/**Constructs an DoubleSpinnerOption from the given values.<br> 
	Uses Double.MIN_VALUE, Double.MAX_VALUE, and 1 for min value, max value, and step, respectively.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPodoubleerException if reader, enactor or DefaultValue is null.
	*/
	public DoubleSpinnerOption(String name, String message, Double defaultValue, Reader<Double, V> reader, Enactor<Double, V> enactor){
		this(name, message, defaultValue, reader, enactor, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_STEP);
	}

	/**Constructs an DoubleSpinnerOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@param minValue the spinner's minimum value.
	@param maxValue the spinner's maximum value.
	@param step the doubleerval between valid spinner values.
	@throws NullPodoubleerException if reader, enactor or DefaultValue is null.
	*/
	public DoubleSpinnerOption(String name, String message, Double defaultValue, Reader<Double, V> reader, Enactor<Double, V> enactor, double minValue, double maxValue, double step){
		super(name, message, defaultValue, reader, enactor);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}

	/**Returns an OptionEvaluator for this DoubleSpinnerOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JSpinner.<br>
	The OptionEvaluator's evaluate method will return the JSpinner's value. It will never return null.
	@param currentValue the JSpinner's initial value.
	@throws NullPodoubleerException if currentValue is null.
	@return an OptionEvaluator for this DoubleSpinnerOption.
	*/
	@Override
	public OptionEvaluator<Double> getEvaluator(Double currentValue){
		return new OptionEvaluator<Double>(){
			private JSpinner component;

			{
				this.component = new JSpinner(new SpinnerNumberModel((double)currentValue, DoubleSpinnerOption.this.minValue, DoubleSpinnerOption.this.maxValue, DoubleSpinnerOption.this.step));
			}

			@Override
			public JComponent getComponent(){
				return this.component;
			}

			@Override
			public Double evaluate(){
				return (Double)this.component.getValue();
			}
		};
	}
}