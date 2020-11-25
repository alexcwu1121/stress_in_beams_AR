package userinterface;

import javax.swing.*;
import java.util.*;

/**Skeleton class returning a JMenuBar object.<br>
This class works by taking a list of MenuSkeleton objects at construction.<br>
When this class' getComponent method is called, it will call the MenuSkeleton's getComponent methods, and assemble the resulting JMenus into a JMenuBar.<br>
The menus in the returned menu bar will be in the iteration order of the list provided at construction time.<br> 
The V type parameter is the type that the menu item's action listeners will be applied to - see the MenuItemSkeleton and StaticActionListener classes.
@author Owen Kulik
*/

public class MenuBarSkeleton<V> implements Skeleton<JMenuBar, V>{
	private final List<MenuSkeleton<V>> menus;

	/**Constructs a MenuBarSkeleton from the given MenuSkeletons.
	@param lms the list of MenuSkeletons.
	@throws NullPointerException if lms or any value in lms is null.
	*/
	public MenuBarSkeleton(List<MenuSkeleton<V>> lms){
		this.menus = List.copyOf(lms);
	}

	/**Constructs and returns a JMenuBar from this skeleton.
	@param enactOn the object that menu items' action listeners will be run on.
	@return a JMenuBar from this skeleton.
	*/
	public JMenuBar getComponent(V enactOn){
		JMenuBar bar = new JMenuBar();
		for(MenuSkeleton<V> ms : menus){
			bar.add(ms.getComponent(enactOn));
		}
		return bar;
	}
}