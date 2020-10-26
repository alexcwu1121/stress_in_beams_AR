package userinterface;

import javax.swing.*;

/**Interface representing a "skeleton" of a GUI component.<br>
This interface allows classes to statically specify GUI components in a single line of code.<br>
As such, the general constract of this interface is that if the getComponent method is called multiple times, the returned JComponent instances should not interfere with each other.<br>
Additionally, successive calls to the getComponent method should return identical JComponents, and implementing classes should be immutable.<br>
This interface has two type parameters. The first parameter is the JComponent type that is returned by the implementing class' getComponent method.<br>
The second type parameter is the type of an object which is provided to the getComponent method.<br>
This type parameter may be used by implementing classes to perform type-specific operations such as adding StaticActionListeners with the same type parameter.<br>
If this type parameter is not needed, implementing classes may declare it with type Object and provide null to the getComponent method.
@author Owen Kulik
*/

@FunctionalInterface
public interface Skeleton<T extends JComponent, V> {
	/**Returns this skeleton's JComponent.
	@param enactOn the object to perform type-specific operations on.
	@return this skeleton's JComponent.
	*/
	T getComponent(V enactOn);
}