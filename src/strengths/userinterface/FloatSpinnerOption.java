package userinterface;

import javax.swing.*;

/**Option for a Float value using a JSpinner representation.
@author Owen Kulik
*/

public class FloatSpinnerOption<V> extends Option<Float, V>{
	private final float minValue;
	private final float maxValue;
	private final float step;

	private static final float DEFAULT_MIN_VALUE = Long.MIN_VALUE;//-Float.MAX_VALUE;
	private static final float DEFAULT_MAX_VALUE = Long.MAX_VALUE;//Float.MAX_VALUE;
	private static final float DEFAULT_STEP = .1F;

	/**Constructs a FloatSpinnerOption from the given values.<br> 
	Uses -Float.MAX_VALUE, Float.MAX_VALUE, and 0.1 for min value, max value, and step, respectively.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public FloatSpinnerOption(String name, String message, Float defaultValue, Reader<Float, V> reader, Enactor<Float, V> enactor){
		this(name, message, defaultValue, reader, enactor, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, DEFAULT_STEP);
	}

	/**Constructs a FloatSpinnerOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@param minValue the spinner's minimum value.
	@param maxValue the spinner's maximum value.
	@param step the interval between valid spinner values.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public FloatSpinnerOption(String name, String message, Float defaultValue, Reader<Float, V> reader, Enactor<Float, V> enactor, float minValue, float maxValue, float step){
		super(name, message, defaultValue, reader, enactor);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}

	/**Returns an OptionEvaluator for this FloatSpinnerOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JSpinner.<br>
	The OptionEvaluator's evaluate method will return the JSpinner's value. It will never return null.
	@param currentValue the JSpinner's initial value.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this FloatSpinnerOption.
	*/
	@Override
	public OptionEvaluator<Float> getEvaluator(Float currentValue){
		return new OptionEvaluator<Float>(){
			private JSpinner component;

			{
				this.component = new JSpinner(new SpinnerNumberModel((float)currentValue, FloatSpinnerOption.this.minValue, FloatSpinnerOption.this.maxValue, FloatSpinnerOption.this.step));
			}

			@Override
			public JComponent getComponent(){
				return this.component;
			}

			@Override
			public Float evaluate(){
				return (Float)this.component.getValue();
			}
		};
	}
}