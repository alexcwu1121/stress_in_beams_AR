package userinterface;

import javax.swing.*;
import markerdetector.*;
import java.util.*;

/**Option class for a MarkerOffset. Represented as a JButton leading to a separate option window in which the ID, rotation, and translating values are selected.
@author Owen Kulik
*/

public class MarkerOffsetOption<V> extends Option<MarkerOffset, V> {
	private static final int ID_MIN = 0;
	private static final int ID_MAX = Integer.MAX_VALUE;
	private static final int ID_STEP = 1;

	private static final double OFFSETS_MIN = -Integer.MAX_VALUE;
	private static final double OFFSETS_MAX = Integer.MAX_VALUE;
	private static final double OFFSETS_STEP = 0.1;

	/**Constructs a MarkerOffsetOption from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or defaultValue is null.
	*/
	public MarkerOffsetOption(String name, String message, MarkerOffset defaultValue, Reader<MarkerOffset, V> reader, Enactor<MarkerOffset, V> enactor){
		super(name, message, defaultValue, reader, enactor);
		if(defaultValue == null){
			throw new NullPointerException();
		}
	}

	/**Returns an OptionEvaluator for this MarkerOffsetOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JButton.<br>
	The OptionEvaluator's evaluate method will never return null.
	@param currentValue the current MarkerOffset value.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this MarkerOffsetOption.
	*/
	public OptionEvaluator<MarkerOffset> getEvaluator(MarkerOffset currentValue){
		if(currentValue == null){
			throw new NullPointerException();
		}
		return new MarkerOffsetOptionEvaluator(currentValue);
	}

	private class MarkerOffsetOptionEvaluator implements OptionEvaluator<MarkerOffset>{
		private JButton component;
		private int id;
		private double xRot;
		private double yRot;
		private double zRot;
		private double xTrans;
		private double yTrans;
		private double zTrans;

		private MarkerOffsetOptionEvaluator(MarkerOffset currentValue){
			this.id = currentValue.id();
			this.xRot = currentValue.xRotation();
			this.yRot = currentValue.yRotation();
			this.zRot = currentValue.zRotation();
			this.xTrans = currentValue.xTranslation();
			this.yTrans = currentValue.yTranslation();
			this.zTrans = currentValue.zTranslation();

			this.component = new JButton("Edit Offset...");

			this.component.addActionListener((action) -> {
				JOptionPane pane = UserInterfaceUtils.optionPane(this.getOptions());
				JDialog dialog = new JDialog(null, "Settings", JDialog.ModalityType.APPLICATION_MODAL);
				dialog.setContentPane(pane);
				dialog.pack();
				dialog.setLocationRelativeTo(this.component);
				dialog.setVisible(true);
			});
		}

		@Override
		public JComponent getComponent(){
			return this.component;
		}

		@Override
		public MarkerOffset evaluate(){
			return new MarkerOffset(this.id, this.xRot, this.yRot, this.zRot, this.xTrans, this.yTrans, this.zTrans);
		}

		private List<InstanceOption<?, ?>> getOptions(){
			List<InstanceOption<?, ?>> answer = new ArrayList<>();
			answer.add(InstanceOption.make(new IntSpinnerOption<MarkerOffsetOptionEvaluator>("id", "Marker ID", MarkerOffsetOption.this.getDefaultValue().id(), (evaluator) -> {
				return evaluator.id;
			}, (value, evaluator) -> {
				evaluator.id = value;
			}, ID_MIN, ID_MAX, ID_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("xRot", "x Rotation", MarkerOffsetOption.this.getDefaultValue().xRotation(), (evaluator) -> {
				return evaluator.xRot;
			}, (value, evaluator) -> {
				evaluator.xRot = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("yRot", "y Rotation", MarkerOffsetOption.this.getDefaultValue().yRotation(), (evaluator) -> {
				return evaluator.yRot;
			}, (value, evaluator) -> {
				evaluator.yRot = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("zRot", "z Rotation", MarkerOffsetOption.this.getDefaultValue().zRotation(), (evaluator) -> {
				return evaluator.zRot;
			}, (value, evaluator) -> {
				evaluator.zRot = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("xTrans", "x Translation", MarkerOffsetOption.this.getDefaultValue().xTranslation(), (evaluator) -> {
				return evaluator.xTrans;
			}, (value, evaluator) -> {
				evaluator.xTrans = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("yTrans", "y Translation", MarkerOffsetOption.this.getDefaultValue().yTranslation(), (evaluator) -> {
				return evaluator.yTrans;
			}, (value, evaluator) -> {
				evaluator.yTrans = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			answer.add(InstanceOption.make(new DoubleSpinnerOption<MarkerOffsetOptionEvaluator>("zTrans", "z Translation", MarkerOffsetOption.this.getDefaultValue().zTranslation(), (evaluator) -> {
				return evaluator.zTrans;
			}, (value, evaluator) -> {
				evaluator.zTrans = value;
			}, OFFSETS_MIN, OFFSETS_MAX, OFFSETS_STEP), this));
			return answer;
		}
	}
}