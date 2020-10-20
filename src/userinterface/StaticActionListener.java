package userinterface;

import java.awt.*;
import java.awt.event.*;

/**Functional interface which allows an extra parameter to be passed to an action listener.
@author Owen Kulik
*/

@FunctionalInterface
public interface StaticActionListener<V>{
	/**Invoked when an action occurs.
	@param action the ActionEvent object
	@param enactOn the extra parameter.
	*/
	void actionPerformed(ActionEvent action, V enactOn);
}