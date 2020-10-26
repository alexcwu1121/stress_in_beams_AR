package userinterface;

/**Functional interface representing a method which reads the current state of an option from a given object.
@author Owen Kulik
*/

@FunctionalInterface
public interface Reader<Q, V>{
	/**Reads the current value of an option from the given object.
	@param readFrom the given object.
	@throws NullPointerException if readFrom is null.
	@return the value of the read option.
	*/
	Q read(V readFrom);
}