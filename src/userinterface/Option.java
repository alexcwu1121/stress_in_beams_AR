package userinterface;

import javax.swing.*;
import org.json.*;

/**Class which is used to statically specify an application option.<br>
This class works by taking two functions at construction time: a reader and an enactor.<br>
The reader specifies how the option's current value can be read given the object that it reads from.<br>
The enactor specifies how to change the option's value given the new value and the object to enact on.<br>
The Option's user interface component is represented by an OptionEvaluator object.<br>
Since this class statically specifies options, each call to the getEvaluator method must return a new OptionEvaluator.<br>
For a view of an Option that returns the same OptionEvaluator each time, see the InstanceOption class.<br>
Subclasses of this class must override the getEvaluator method. All other methods are marked as final and as such cannot be overridden.<br>
This class has two type parameters. The first one, Q, represents the type of the option.<br>
The second one, V, represents the type of object that the option is being read from and written to.
@author Owen Kulik
*/

public abstract class Option<Q, V>{
	private final String name;
	private final String message;
	private final Q defaultValue;
	private final Enactor<Q, V> enactor;
	private final Reader<Q, V> reader;

	/**Constructs an option from the given values.
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can be null by default. However, subclasses may decide that the default value cannot be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader or enactor is null.
	*/
	public Option(String name, String message, Q defaultValue, Reader<Q, V> reader, Enactor<Q, V> enactor){
		if(reader == null || enactor == null){
			throw new NullPointerException();
		}
		this.name = name;
		this.message = message;
		this.defaultValue = defaultValue;
		this.enactor = enactor;
		this.reader = reader;
	}

	/**Returns an OptionEvaluator for this Option. As stated above, successive calls to the getEvaluator method must each return new OptionEvaluators.
	@param currentValue the initial value for the OptionEvaluator to be set to.
	@return an OptionEvaluator for this Option.
	*/
	public abstract OptionEvaluator<Q> getEvaluator(Q currentValue);

	//public abstract void addToJSONObject(JSONObject obj, Q value);

	//public abstract Q readFromJSONObject(JSONObject obj);

	/**Uses the provided Reader object to read the option's value from the given object.
	@param readFrom the object to read the option from.
	@throws NullPointerException if readFrom is null.
	@return the option value found in the object.
	*/
	public final Q read(V readFrom){
		return this.reader.read(readFrom);
	}

	/**Uses the provided Enactor object to enact the given value on the given object.
	@param value the value to write into the object.
	@param enactOn the object to enact the option on.
	@throws NullPointerException if enactOn is null.
	*/
	public final void enact(Q value, V enactOn){
		this.enactor.enact(value, enactOn);
	}

	/**Enacts this option's default value on the given object.
	@param enactOn the object to enact the option on.
	@throws NullPointerException if enactOn is null.
	*/
	public final void resetToDefault(V enactOn){
		this.enact(this.defaultValue, enactOn);
	}

	/**Returns this Option's name.
	@return this Option's name.
	*/
	public final String getName(){
		return this.name;
	}

	/**Returns this Option's message.
	@return this Option's message.
	*/
	public final String getMessage(){
		return this.message;
	}

	/**Returns this Option's default value.
	@return this Option's default value.
	*/
	public final Q getDefaultValue(){
		return this.defaultValue;
	}
}