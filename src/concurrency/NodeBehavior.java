package concurrency;

/**Enum representing the available node behaviors.
*/
public enum NodeBehavior{
	/** Represents a blocking node.
	Blocking nodes do not let the producers deposit until all consumers have withdrawn.
	This ensures that all information is from the same frame.
	*/
	BLOCKING, 

	/** Represents a replacing node.
	Replacing nodes let the producers deposit whenever they want to.
	This speeds up the program.
	*/
	REPLACING
}