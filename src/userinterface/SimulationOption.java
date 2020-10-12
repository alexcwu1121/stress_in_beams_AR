package userinterface;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import util.*;

/**Option class used for Simulations.<br>
The type of this option class is OptionalSimulationParameters. <br>
This allows for the paramters to be remembered even when the simulation is not running.
@author Owen Kulik
*/

public class SimulationOption<V> extends Option<OptionalSimulationParameters, V>{
	/**Constructs a SimulationOption from the given values.<br> 
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public SimulationOption(String name, String message, OptionalSimulationParameters defaultValue, Reader<OptionalSimulationParameters, V> reader, Enactor<OptionalSimulationParameters, V> enactor){
		super(name, message, defaultValue, reader, enactor);
	}

	/**Returns an OptionEvaluator for this SimulationOption.<br>
	This OptionEvaluator's getComponent method will return a JPanel consisting of a checkbox and a button.<br>
	The checkbox indicates whether the simulation is running, and the button opens a window in which the simulation paramters can be edited.<br>
	The button will be omitted if the SimulationParamters has no paramters.
	@param currentValue the initial value.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this SimulationOption. 
	*/
	@Override
	public OptionEvaluator<OptionalSimulationParameters> getEvaluator(OptionalSimulationParameters currentValue){
		return this.new SimulationOptionEvaluator(currentValue);
	}

	private class SimulationOptionEvaluator implements OptionEvaluator<OptionalSimulationParameters>{
		private JPanel component;
		private SimulationParameters params;
		private JCheckBox checkbox;

		@SuppressWarnings("unchecked")
		private SimulationOptionEvaluator(OptionalSimulationParameters currentValue){
			this.params = currentValue.getParameters();
			this.component = new JPanel();
			this.checkbox = new JCheckBox();
			this.checkbox.setSelected(currentValue.isRunning());
			this.component.add(this.checkbox);

			if(this.params.numberOfParameters() > 0){
				JButton button = new JButton("Edit Simulation Parameters");
				button.setEnabled(currentValue.isRunning());
				this.checkbox.addActionListener((action) -> button.setEnabled(this.checkbox.isSelected()));
				button.addActionListener((action) -> {
					JOptionPane pane = UserInterfaceUtils.optionPane(this.getOptions());
					JDialog dialog = new JDialog(null, "Settings", JDialog.ModalityType.APPLICATION_MODAL);
					dialog.setContentPane(pane);
					dialog.pack();
					dialog.setLocationRelativeTo(this.component);
					dialog.setVisible(true);
				});
				this.component.add(button);
			}
		}

		public JComponent getComponent(){
			return this.component;
		}

		public OptionalSimulationParameters evaluate(){
			return new OptionalSimulationParameters(this.checkbox.isSelected(), this.params.copy());
		}

		@SuppressWarnings("unchecked")
		private List<InstanceOption<?, ?>> getOptions(){
			List<InstanceOption<?, ?>> options = new ArrayList<>();
			List<Parameter> parameters = this.params.getAllParameterTypes();
			for(int index = 0; index < parameters.size(); index++){
				final int i = index;
				Parameter p = parameters.get(i);
				Class<? extends Option> optionClass = UserInterfaceUtils.getOptionForClass(p.getType());
				HumanReadableName hrn = p.getDeclaredAnnotation(HumanReadableName.class);
				String parameterName = hrn == null ? p.getName() : hrn.value();
				Description d = p.getDeclaredAnnotation(Description.class);
				String description = d == null ? parameterName : d.value();
				Constructor<? extends Option> constructor = null;
				for(Constructor<? extends Option> c : (Constructor<? extends Option>[])optionClass.getConstructors()){
					if(c.getParameterCount() == 5){
						constructor = c;
						break;
					}
				}
				if(constructor == null){
					throw new IllegalStateException(optionClass.getSimpleName() + " did not have a five-element constructor.");
				}
				Reader<Object, SimulationOptionEvaluator> r = (evaluator) -> {
					return evaluator.params.getParameter(i);
				};
				Enactor<Object, SimulationOptionEvaluator> e = (value, evaluator) -> {
					evaluator.params.fillInParameter(i, value);
				};
				try{
					options.add(InstanceOption.make(constructor.newInstance(parameterName, description, SimulationOption.this.getDefaultValue().getParameters().getParameter(i), r, e), this));
				} catch(ReflectiveOperationException exc){
					throw new RuntimeException("Option constructor threw exception.", exc);
				}
			}
			return options;
		}
	}
}