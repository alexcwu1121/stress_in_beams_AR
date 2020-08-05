package userinterface;

/**Class which stores an Option, OptionEvaluator, and an object that the Option applies to.<br>
InstanceOption remedies the fact that successive calls to the Option.getEvaluator() method return separate evaluators.<br>
In constrast, successive calls to this class' getEvaluator() method always return the same OptionEvaluator.<br>
Additionally, this class is useful in cases when options are being stored in a Collection object,
 as otherwise there would be no guarantee that the object the option applies to is the same type that the option is written for.
This class should be used to represent Options whenever the object that the option applies to is known.<br>
*/

public class InstanceOption<Q, V> {
	private final Option<Q, V> option;
	private final V enactedObject;
	private final OptionEvaluator<Q> evaluator;

	/**Returns an InstanceOption from the given Option and V type.
	@param option the option to store.
	@param enactOn the object to apply the Option to.
	@throws NullPointerException if either parameter is null.
	*/
	public InstanceOption(Option<Q, V> option, V enactOn){
		if(option == null || enactOn == null){
			throw new NullPointerException();
		}
		this.option = option;
		this.enactedObject = enactOn;
		this.evaluator = this.option.getEvaluator(this.option.read(this.enactedObject));
	}

	/**Alias for the constructor which takes the same types.
	*/
	public static <Q, V> InstanceOption<Q, V> make(Option<Q, V> option, V enactOn){
		return new InstanceOption<Q, V>(option, enactOn);
	}

	/**Returns this InstanceOption's Option.
	@return this InstanceOption's Option.
	*/
	public Option<Q, V> getOption(){
		return this.option;
	}

	/**Returns the object that this InstanceOption applies to.
	@return the object that this InstanceOption applies to.
	*/
	public V getEnactedObject(){
		return this.enactedObject;
	}

	/**Returns the OptionEvaluator for this InstanceOption. Successive calls to this method will always return the same object.
	@return the OptionEvaluator for this InstanceOption.
	*/
	public OptionEvaluator<Q> getEvaluator(){
		return this.evaluator;
	}

	/**Enacts using the evaluator's current value.
	*/
	public void enact(){
		this.option.enact(this.evaluator.evaluate(), this.enactedObject);
	}

	/**Resets the option's value to its default.
	*/
	public void resetToDefault(){
		this.option.resetToDefault(this.enactedObject);
	}

}