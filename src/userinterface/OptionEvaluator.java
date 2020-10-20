package userinterface;

import javax.swing.*;

/**Interface which is used to evaluate Options.<br>
Every Option class must have an implementation of a getEvaluator method which returns an OptionEvaluator.
@author Owen Kulik
*/

public interface OptionEvaluator<Q>{
	/**Returns the JComponent associated with this OptionEvaluator.<br> 
	Successive calls of this method should return the same object.<br>
	Implementations of this method should never return null.
	@return the JComponent associated with this OptionEvaluator.
	*/
	JComponent getComponent();

	/**Evaluates the current state of this OptionEvaluator and returns the result.<br>
	Implementations of this method can and should rely on the JComponents returned by the getComponent method.
	@return the current value of this OptionEvaluator.
	*/
	Q evaluate();
}