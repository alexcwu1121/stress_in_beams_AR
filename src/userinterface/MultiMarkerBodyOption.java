package userinterface;

import markerdetector.*;
import javax.swing.*;
import java.util.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

//TODO: Consider showing an error for multiple offsets with same ID

/**Option for a MultiMarkerBody. Reprsented as a Jpanel containing spinners for tolerance and number of offsets, and MarkerOffsetOptions for the offsets.
@author Owen Kulik
*/

public class MultiMarkerBodyOption<V> extends Option<MultiMarkerBody, V>{
	private static final double FILTERTOL_MIN = -Integer.MAX_VALUE;
	private static final double FILTERTOL_MAX = Integer.MAX_VALUE;
	private static final double FILTERTOL_STEP = 0.1;

	private static final int OFFSETS_MIN = 1;
	private static final int OFFSETS_MAX = 9;
	private static final int OFFSETS_STEP = 1;


	/**Constructs a MultiMarkerBodyOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or defaultValue is null.
	*/
	public MultiMarkerBodyOption(String name, String message, MultiMarkerBody defaultValue, Reader<MultiMarkerBody, V> reader, Enactor<MultiMarkerBody, V> enactor){
		super(name, message, defaultValue, reader, enactor);
		if(defaultValue == null){
			throw new NullPointerException();
		}
	}

	/**Returns an OptionEvaluator for this MultiMarkerBodyOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JPanel.<br>
	The OptionEvaluator's evaluate method will never return null.
	@param currentValue the current MultiMarkerBody value.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this MultiMarkerBodyOption.
	*/
	public OptionEvaluator<MultiMarkerBody> getEvaluator(MultiMarkerBody currentValue){
		if(currentValue == null){
			throw new NullPointerException();
		}
		return new MultiMarkerBodyOptionEvaluator(currentValue);
	}

	private class MultiMarkerBodyOptionEvaluator implements OptionEvaluator<MultiMarkerBody>{
		private JPanel component;
		private JSpinner filterTol;
		private JSpinner numberOfOffsets;
		private List<JLabel> offsetLabels;
		private List<MarkerOffset> offsets;
		private List<MarkerOffset> defaultOffsets;
		private List<InstanceOption<MarkerOffset, MultiMarkerBodyOptionEvaluator>> offsetButtons;
		private int numberOfButtons = 0;

		private MultiMarkerBodyOptionEvaluator(MultiMarkerBody currentValue){
			this.offsets = new ArrayList<>(currentValue.getOffsets());
			this.defaultOffsets = new ArrayList<>(MultiMarkerBodyOption.this.getDefaultValue().getOffsets());
			this.component = new JPanel();
			this.component.setLayout(new GridBagLayout());
			this.filterTol = new JSpinner(new SpinnerNumberModel(currentValue.getFilterTol(), FILTERTOL_MIN, FILTERTOL_MAX, FILTERTOL_STEP));
			this.numberOfOffsets = new JSpinner(new SpinnerNumberModel(offsets.size(), OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP));
			this.offsetLabels = new ArrayList<>();
			this.offsetButtons = new ArrayList<>();
			this.updateButtons();

			this.component.add(new JLabel("Filter Tol"), UserInterfaceUtils.makeGridBagConstraint(0, 0, 1, 1, GridBagConstraints.WEST));
			this.component.add(this.filterTol, UserInterfaceUtils.makeGridBagConstraint(1, 0, 1, 1, GridBagConstraints.WEST));
			this.component.add(new JLabel("Number of Offsets"), UserInterfaceUtils.makeGridBagConstraint(0, 1, 1, 1, GridBagConstraints.WEST));
			this.component.add(this.numberOfOffsets, UserInterfaceUtils.makeGridBagConstraint(1, 1, 1, 1, GridBagConstraints.WEST));

			this.numberOfOffsets.addChangeListener((change) -> {
				this.updateButtons();
			});
			
		}

		@Override
		public JComponent getComponent(){
			return this.component;
		}

		@Override
		public MultiMarkerBody evaluate(){
			for(InstanceOption<MarkerOffset, MultiMarkerBodyOptionEvaluator> io : this.offsetButtons){
				io.enact();
			}
			Map<Integer, Integer> ids = new HashMap<>();
			for(MarkerOffset mo : this.offsets){
				ids.put(mo.id(), ids.getOrDefault(mo.id(), 0) + 1);
			}
			boolean duplicate = false;
			for(MarkerOffset mo : this.offsets){
				if(ids.get(mo.id()) > 1){
					duplicate = true;
				}
			}
			if(duplicate){
				JOptionPane.showMessageDialog(null, "Warning: Multiple offsets had same marker ID value. The resulting Multi-Marker Body may behave unexpectedly.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
			return new MultiMarkerBody((double)this.filterTol.getValue(), this.offsets);
		}

		private void updateButtons(){
			//System.out.println("updateButtons called");
			int oldNumber = numberOfButtons;
			int newNumber = (int)this.numberOfOffsets.getValue();
			if(newNumber < oldNumber){
				for(int i = newNumber; i < oldNumber; i++){
					this.component.remove(this.offsetLabels.get(i));
					this.component.remove(this.offsetButtons.get(i).getEvaluator().getComponent());
				}
			} else if(newNumber > oldNumber){
				if(newNumber > this.offsetButtons.size()){
					for(int i = this.offsetButtons.size(); i < newNumber; i++){
						final int ibert = i;

						this.offsetLabels.add(new JLabel("Offset " + (i + 1)));
						this.offsetButtons.add(InstanceOption.make(new MarkerOffsetOption<MultiMarkerBodyOptionEvaluator>("offset" + i, "Offset" + (i + 1), 
									this.defaultOffsets.size() > i ? this.defaultOffsets.get(i) : new MarkerOffset(i, 0, 0, 0, 0, 0, 0), (evaluator) -> {
							if(evaluator.offsets.size() <= ibert){
								for(int j = evaluator.offsets.size(); j <= ibert; j++){
									evaluator.offsets.add(new MarkerOffset(ibert, 0, 0, 0, 0, 0, 0));
								}
							}
							return evaluator.offsets.get(ibert);
						}, (value, evaluator) -> {
							if(evaluator.offsets.size() <= ibert){
								for(int j = evaluator.offsets.size(); j <= ibert; j++){
									evaluator.offsets.add(null);
								}
							}
							evaluator.offsets.set(ibert, value);
						}), this));
					}
				}
				for(int i = oldNumber; i < newNumber; i++){
					this.component.add(this.offsetLabels.get(i), UserInterfaceUtils.makeGridBagConstraint(0, 2 + i, 1, 1, GridBagConstraints.WEST));
					this.component.add(this.offsetButtons.get(i).getEvaluator().getComponent(), UserInterfaceUtils.makeGridBagConstraint(1, 2 + i, 1, 1, GridBagConstraints.WEST));
				}
			}
			this.numberOfButtons = newNumber;
			this.component.revalidate();
		}
	}
}