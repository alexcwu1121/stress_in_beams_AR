package userinterface;

//weak class which stores an option-V pair for type safety uses

public class InstanceOption<Q, V> {
	private final Option<Q, V> option;
	private final V enactedObject;
	private final OptionEvaluator<Q> evaluator;

	public InstanceOption(Option<Q, V> option, V enactOn){
		if(option == null || enactOn == null){
			throw new NullPointerException();
		}
		this.option = option;
		this.enactedObject = enactOn;
		this.evaluator = this.option.getEvaluator(this.option.read(this.enactedObject));
	}

	public static <Q, V> InstanceOption<Q, V> make(Option<Q, V> option, V enactOn){
		return new InstanceOption<Q, V>(option, enactOn);
	}

	public Option<Q, V> getOption(){
		return this.option;
	}

	public V getEnactedObject(){
		return this.enactedObject;
	}

	public OptionEvaluator<Q> getEvaluator(){
		return this.evaluator;
	}

	public void enact(){
		this.option.enact(this.evaluator.evaluate(), this.enactedObject);
	}

	public void resetToDefault(){
		this.option.resetToDefault(this.enactedObject);
	}

}