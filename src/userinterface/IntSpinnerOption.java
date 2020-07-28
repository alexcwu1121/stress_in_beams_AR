package userinterface;

import javax.swing.*;

public class IntSpinnerOption<V> extends Option<Integer, V>{
	private final int minValue;
	private final int maxValue;
	private final int step;

	public IntSpinnerOption(String name, String message, Integer defaultValue, Reader<Integer, V> reader, Enactor<Integer, V> enactor, int minValue, int maxValue, int step){
		super(name, message, defaultValue, reader, enactor);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}

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