package userinterface;

/**Functional interface representing a method which enacts a given value on a given object.
@author Owen Kulik
*/

@FunctionalInterface
public interface Enactor<Q, V>{
	/**Enacts the given value on the given object.<br> 
	@param value the value to enact.
	@param enactOn the object to enact the value on.
	@throws NullPointerException if enactOn is null. Additionally, implementing classes may, but do not have to, throw NullPointerException if value is null.
	@throws IllegalArgumentException implementing classes may choose to throw IllegalArgumentException depeding on the given value.
	*/
	void enact(Q value, V enactOn);
}