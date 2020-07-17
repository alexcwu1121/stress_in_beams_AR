package userinterface;

import javax.swing.*;
import java.util.*;

public class MenuBarSkeleton<V> implements Skeleton<JMenuBar, V>{
	private final List<MenuSkeleton<V>> menus;

	public MenuBarSkeleton(List<MenuSkeleton<V>> lms){
		this.menus = List.copyOf(lms);
	}

	public JMenuBar getComponent(V enactOn){
		JMenuBar bar = new JMenuBar();
		for(MenuSkeleton<V> ms : menus){
			bar.add(ms.getComponent(enactOn));
		}
		return bar;
	}
}