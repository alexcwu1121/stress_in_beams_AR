package userinterface;

import javax.swing.*;
import java.util.*;

public class MenuSkeleton<V> implements Skeleton<JMenu, V>{
	private final String name;
	private final List<MenuItemSkeleton<V>> items;

	public MenuSkeleton(String name, List<MenuItemSkeleton<V>> lmis){
		this.name = name;
		this.items = List.copyOf(lmis);
	}

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

	public String getName(){
		return this.name;
	}
}