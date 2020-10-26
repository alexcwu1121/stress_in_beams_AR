package userinterface;

import javax.swing.*;
import java.util.*;

/**Skeleton class returning a JMenu object.<br>
This class works by taking a list of MenuItemSkeleton objects at construction.<br>
When this class' getComponent method is called, it will call the MenuItemSkeleton's getComponent methods, and assemble the resulting JMenusItems into a JMenu.<br>
The items in the returned menu bar will be in the iteration order of the list provided at construction time.<br> 
If the provided list has null values, menu separators will be inserted in the null items' positions.<br>
The V type parameter is the type that the menu item's action listeners will be applied to - see the MenuItemSkeleton and StaticActionListener classes.
@author Owen Kulik
*/

public class MenuSkeleton<V> implements Skeleton<JMenu, V>{
	private final String name;
	private final List<MenuItemSkeleton<V>> items;

	/**Constructs a MenuSkeleton with the given name and MenuItemSkeletons.
	@param name the menu's name. null value indicates a menu without a name.
	@param lmis the MenuItemSkeletons.
	@throws NullPointerException if lmis is null.
	*/
	public MenuSkeleton(String name, List<MenuItemSkeleton<V>> lmis){
		this.name = name;
		this.items = new LinkedList<MenuItemSkeleton<V>>(lmis);
	}

	/**Constructs and returns a JMenu from this skeleton.
	@param enactOn the object that menu items' action listeners will be run on.
	@return a JMenu from this skeleton.
	*/
	public JMenu getComponent(V enactOn){
		JMenu menu = new JMenu(this.name);
		for(MenuItemSkeleton<V> mis : this.items){
			if(mis == null){
				menu.addSeparator();
				continue;
			}
			menu.add(mis.getComponent(enactOn));
		}
		return menu;
	}

	/**Returns this MenuSkeleton's name. A null value indicates no name.
	@return this MenuSkeleton's name.
	*/
	public String getName(){
		return this.name;
	}
}